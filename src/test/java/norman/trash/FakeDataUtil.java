package norman.trash;

import norman.trash.controller.view.TranStatus;
import norman.trash.domain.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static norman.trash.MessagesConstants.BEGINNING_BALANCE;
import static norman.trash.controller.view.TranStatus.*;
import static norman.trash.domain.AcctType.BILL;
import static norman.trash.domain.AcctType.CC;

public class FakeDataUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FakeDataUtil.class);
    private static final Random RANDOM = new Random();
    private static final DateFormat YYMD = new SimpleDateFormat("yyyy-MM-dd");
    private static final String[] WORDS =
            {"Abominable", "Bulimic", "Cosmic", "Desperate", "Evil", "Funky", "Ginormous", "Hungry", "Inconceivable",
                    "Jurassic", "Kick-ass", "Ludicrous", "Malevolent", "Nuclear", "Obsequious", "Pedantic", "Quiescent",
                    "Recalcitrant", "Sleazy", "Taciturn", "Unbelievable", "Violent", "Withering", "Xenophobic", "Yucky",
                    "Zealous"};
    private static final String[] CAT_NAMES =
            {"Mortgage", "Groceries", "Utilities", "Automobile", "Cable", "Miscellaneous"};
    private static final int NBR_OF_PATTERNS_MAX = 3;
    private static final int NBR_OF_PATTERNS_MIN = 1;
    private static final String[] BILL_NAMES =
            {"Cable TV", "Gas", "Gym", "Insurance", "Lawn Service", "Power", "Water and Sewer"};
    private static final String[] CC_NAMES = {"Credit Card", "Plastic Credit", "Gold Card", "Platinum Card"};
    private static final String[] CHECKING_NAMES = {"Bank", "Credit Union", "Saving & Loan"};
    private static final int NBR_OF_ACCTS = 12;
    private static final int NBR_OF_ACCT_NBRS_MAX = 4;
    private static final int NBR_OF_ACCT_NBRS_MIN = 1;
    private static final int NBR_OF_STMT_MAX = 120;
    private static final int NBR_OF_STMT_MIN = 60;
    private static final int CENTS_MAX = 500000;
    private static final int CENTS_MIN = -500000;

    private FakeDataUtil() {
    }

    public static void main(String[] args) {
        LOGGER.debug("Starting FakeDataUtil");
        long catId = 1L;
        long patternId = 1L;
        long acctId = 1L;
        long acctNbrId = 1L;
        long stmtId = 1L;
        long tranId = 1L;
        List<Cat> catList = new ArrayList<>();
        Map<String, Acct> acctMap = new LinkedHashMap<>();
        for (String name : CAT_NAMES) {
            Cat cat = buildCat(catId++, name);
            catList.add(cat);
            int nbrOfPatterns = RANDOM.nextInt(NBR_OF_PATTERNS_MAX - NBR_OF_PATTERNS_MIN + 1) + NBR_OF_PATTERNS_MIN;
            for (int i = 0; i < nbrOfPatterns; i++) {
                buildRandomPattern(patternId++, i, cat);
            }
        }
        for (int i = 0; i < NBR_OF_ACCTS; i++) {
            Acct acct = buildRandomAcct(acctId++, acctMap);
            acctMap.put(acct.getName(), acct);
            int nbrOfAcctNbrs = RANDOM.nextInt(NBR_OF_ACCT_NBRS_MAX - NBR_OF_ACCT_NBRS_MIN + 1) + NBR_OF_ACCT_NBRS_MIN;
            long daysAgo = RANDOM.nextInt(31) + 365;
            LocalDate beginDate = LocalDate.now().minusDays(daysAgo);
            long interval = daysAgo / (nbrOfAcctNbrs + 1);
            for (int j = 0; j < nbrOfAcctNbrs; j++) {
                LocalDate effDate = beginDate.plusDays(interval * j);
                buildRandomAcctNbr(acctNbrId++, effDate, acct);
            }

            Stmt beginStmt = buildBeginStmt(stmtId++, beginDate, acct);
            buildRandomBeginTran(tranId++, beginStmt);
            LocalDate stmtDate = beginDate.plusMonths(1L);
            while (stmtDate.isBefore(LocalDate.now())) {
                buildStmt(acctNbrId++, stmtDate, acct);
                stmtDate = stmtDate.plusMonths(1L);
            }
            buildCurrentStmt(stmtId++, acct);

            int nbrOfStmts = RANDOM.nextInt(NBR_OF_STMT_MAX - NBR_OF_STMT_MIN + 1) + NBR_OF_STMT_MIN;
            for (int j = 0; j < nbrOfStmts; j++) {
                buildRandomTran(tranId++, (int) daysAgo, acct, catList);
            }

            List<Stmt> stmts = acct.getStmts();
            Comparator<Stmt> comp = new Comparator<Stmt>() {
                public int compare(Stmt stmt1, Stmt stmt2) {
                    return stmt1.getCloseDate().compareTo(stmt2.getCloseDate());
                }
            };
            stmts.sort(comp);
            BigDecimal openBalance = stmts.get(0).getTrans().get(0).getAmount();
            for (int j = 1; j < stmts.size() - 1; j++) {
                Stmt stmt = stmts.get(j);

                BigDecimal credits = BigDecimal.ZERO;
                BigDecimal debits = BigDecimal.ZERO;
                for (Tran tran : stmt.getTrans()) {
                    BigDecimal amount = tran.getAmount();
                    if (amount.compareTo(BigDecimal.ZERO) > 0) {
                        credits = credits.add(amount);
                    } else {
                        debits = debits.add(amount);
                    }
                }
                if (stmt.getAcct().getType() == CC) {
                    stmt.setOpenBalance(openBalance);
                    stmt.setCredits(credits);
                    stmt.setDebits(debits);
                    stmt.setFees(BigDecimal.ZERO);
                    stmt.setInterest(BigDecimal.ZERO);
                    stmt.setMinimumDue(BigDecimal.valueOf(2500, 2));
                }
                if (stmt.getAcct().getType() == CC || stmt.getAcct().getType() == BILL) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(stmt.getCloseDate());
                    cal.add(Calendar.DATE, 25);
                    stmt.setDueDate(cal.getTime());
                }
                stmt.setCloseBalance(openBalance.add(credits).add(debits));
                openBalance = openBalance.add(credits).add(debits);
            }
        }

        for (Cat cat : catList) {
            printInsert(cat);
            for (Pattern pattern : cat.getPatterns()) {
                printInsert(pattern);
            }
        }
        for (Acct acct : acctMap.values()) {
            printInsert(acct);
            for (AcctNbr acctNbr : acct.getAcctNbrs()) {
                printInsert(acctNbr);
            }
            for (Stmt stmt : acct.getStmts()) {
                printInsert(stmt);
                for (Tran tran : stmt.getTrans()) {
                    printInsert(tran);
                }
            }
        }
    }

    public static Cat buildCat(long id, String name) {
        Cat cat = new Cat();
        cat.setId(id);
        cat.setName(name);
        return cat;
    }

    public static Pattern buildPattern(long id, int seq, String regex, Cat cat) {
        Pattern pattern = new Pattern();
        pattern.setId(id);
        pattern.setSeq(seq);
        pattern.setRegex(regex);
        pattern.setCat(cat);
        cat.getPatterns().add(pattern);
        return pattern;
    }

    public static Acct buildAcct(long id, String name, AcctType type, String ofxFid) {
        Acct acct = new Acct();
        acct.setId(id);
        acct.setName(name);
        acct.setType(type);
        acct.setOfxFid(ofxFid);
        return acct;
    }

    public static AcctNbr buildAcctNbr(long id, String number, LocalDate effDate, Acct acct) {
        AcctNbr acctNbr = new AcctNbr();
        acctNbr.setId(id);
        acctNbr.setNumber(number);
        Date date = Date.from(effDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        acctNbr.setEffDate(date);
        acctNbr.setAcct(acct);
        acct.getAcctNbrs().add(acctNbr);
        return acctNbr;
    }

    public static Stmt buildStmt(long id, LocalDate closeDate, Acct acct) {
        Stmt stmt = new Stmt();
        stmt.setId(id);
        Date date = Date.from(closeDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        stmt.setCloseDate(date);
        stmt.setAcct(acct);
        acct.getStmts().add(stmt);
        return stmt;
    }

    public static Stmt buildBeginStmt(long id, LocalDate beginDate, Acct acct) {
        return buildStmt(id, beginDate, acct);
    }

    public static Stmt buildCurrentStmt(long id, Acct acct) {
        return buildStmt(id, LocalDate.of(9999, 12, 31), acct);
    }

    public static Tran buildTran(long id, TranStatus status, LocalDate postDate, BigDecimal amount, String name,
            String ofxFitId, Stmt stmt) {
        Date date = Date.from(postDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return buildTranImpl(id, status, date, amount, name, ofxFitId, stmt);
    }

    public static Tran buildBeginTran(long id, BigDecimal beginBalance, Stmt beginStmt) {
        Tran tran =
                buildTranImpl(id, MANUAL, beginStmt.getCloseDate(), beginBalance, BEGINNING_BALANCE, null, beginStmt);
        return tran;
    }

    private static Pattern buildRandomPattern(long id, int seq, Cat cat) {
        String regex = WORDS[RANDOM.nextInt(WORDS.length)] + ".*";
        return buildPattern(id, seq, regex, cat);
    }

    private static Acct buildRandomAcct(long id, Map<String, Acct> acctMap) {
        String name = null;
        String nameFirstPart = null;
        AcctType type = null;
        do {
            type = AcctType.values()[RANDOM.nextInt(AcctType.values().length)];
            nameFirstPart = WORDS[RANDOM.nextInt(WORDS.length)];
            String nameSecondPart = null;
            if (type == AcctType.BILL) {
                nameSecondPart = BILL_NAMES[RANDOM.nextInt(BILL_NAMES.length)];
            } else if (type == CC) {
                nameSecondPart = CC_NAMES[RANDOM.nextInt(CC_NAMES.length)];
            } else if (type == AcctType.CHECKING) {
                nameSecondPart = CHECKING_NAMES[RANDOM.nextInt(CHECKING_NAMES.length)];
            }
            name = nameFirstPart + " " + nameSecondPart;
        } while (acctMap.keySet().contains(name));
        String ofxFid = null;
        if (RANDOM.nextInt(2) == 0) {
            ofxFid = RandomStringUtils.randomNumeric(4, 5);
        }
        Acct acct = buildAcct(id, name, type, ofxFid);
        acct.setAddressName(name + " Inc.");
        acct.setAddress1(RandomStringUtils.randomNumeric(4) + " " + WORDS[RANDOM.nextInt(WORDS.length)] + " St");
        if (RANDOM.nextInt(2) == 0) {
            acct.setAddress2("Suite " + RandomStringUtils.randomNumeric(3));
        }
        acct.setCity(WORDS[RANDOM.nextInt(WORDS.length)] + " City");
        acct.setState(WORDS[RANDOM.nextInt(WORDS.length)].substring(0, 2).toUpperCase());
        acct.setZipCode(RandomStringUtils.randomNumeric(5) + "-" + RandomStringUtils.randomNumeric(4));
        acct.setPhoneNumber(RandomStringUtils.randomNumeric(3) + "-" + RandomStringUtils.randomNumeric(3) + "-" +
                RandomStringUtils.randomNumeric(4));

        if (type == CC) {
            acct.setCreditLimit(BigDecimal.valueOf((RANDOM.nextInt(99) + 1) * 100000, 2));
        }
        if (ofxFid != null) {
            acct.setOfxOrganization(nameFirstPart);
            acct.setOfxFid(ofxFid);
            acct.setOfxBankId(RandomStringUtils.randomNumeric(9));
        }
        return acct;
    }

    private static AcctNbr buildRandomAcctNbr(long id, LocalDate effDate, Acct acct) {
        String number = RandomStringUtils.randomNumeric(12);
        return buildAcctNbr(id, number, effDate, acct);
    }

    private static Tran buildRandomBeginTran(long id, Stmt beginStmt) {
        int cents = RANDOM.nextInt(CENTS_MAX - CENTS_MIN + 1) + CENTS_MIN;
        BigDecimal beginBalance = BigDecimal.valueOf(cents, 2);
        return buildBeginTran(id, beginBalance, beginStmt);
    }

    private static Tran buildRandomTran(long id, int maxDaysAgo, Acct acct, List<Cat> catList) {
        TranStatus status = TranStatus.values()[RANDOM.nextInt(TranStatus.values().length)];

        LocalDate localDate = LocalDate.now().minusDays(RANDOM.nextInt(maxDaysAgo));
        Date postDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        int cents = RANDOM.nextInt(CENTS_MAX - CENTS_MIN + 1) + CENTS_MIN;
        BigDecimal amount = BigDecimal.valueOf(cents, 2);

        String name = WORDS[RANDOM.nextInt(WORDS.length)] + " " + WORDS[RANDOM.nextInt(WORDS.length)];

        String ofxFitId = null;
        if (status == UPLOADED || status == BOTH) {
            ofxFitId = RandomStringUtils.randomAlphanumeric(30);
        }

        List<Stmt> stmts = acct.getStmts();
        stmts.sort(Comparator.comparing(Stmt::getCloseDate));
        Stmt foundStmt = null;
        for (Stmt stmt : stmts) {
            if (!stmt.getCloseDate().before(postDate)) {
                foundStmt = stmt;
                break;
            }
        }

        Tran tran = buildTranImpl(id, status, postDate, amount, name, ofxFitId, foundStmt);

        if (acct.getType() == CC && amount.compareTo(BigDecimal.ZERO) <= 0) {
            if (RANDOM.nextInt(2) == 0) {
                String checkNumber = RandomStringUtils.randomNumeric(4);
                tran.setCheckNumber(checkNumber);
                if (status == MANUAL || status == BOTH) {
                    tran.setManualCheckNumber(checkNumber);
                }
                if (status == UPLOADED || status == BOTH) {
                    tran.setUploadedCheckNumber(checkNumber);
                }
            }
        }

        if (RANDOM.nextInt(2) == 0) {
            String memo = RandomStringUtils.randomAlphanumeric(10);
            tran.setMemo(memo);
            if (status == MANUAL || status == BOTH) {
                tran.setManualMemo(memo);
            }
            if (status == UPLOADED || status == BOTH) {
                tran.setUploadedMemo(memo);
            }
        }

        if (RANDOM.nextInt(2) == 0) {
            Cat cat = catList.get(RANDOM.nextInt(catList.size()));
            tran.setCat(cat);
        }
        return tran;
    }

    private static Tran buildTranImpl(long id, TranStatus status, Date postDate, BigDecimal amount, String name,
            String ofxFitId, Stmt stmt) {
        Tran tran = new Tran();
        tran.setId(id);
        tran.setPostDate(postDate);
        tran.setAmount(amount);
        tran.setName(name);

        if (status == MANUAL || status == BOTH) {
            tran.setManualPostDate(postDate);
            tran.setManualAmount(amount);
            tran.setManualName(name);
        }
        if (status == UPLOADED || status == BOTH) {
            tran.setUploadedPostDate(postDate);
            tran.setUploadedAmount(amount);
            tran.setUploadedName(name);
            tran.setOfxFitId(ofxFitId);
        }

        tran.setStmt(stmt);
        stmt.getTrans().add(tran);
        return tran;
    }

    private static void printInsert(Object bean) {
        String beanName = bean.getClass().getSimpleName();
        List<Class> simpleTypes =
                Arrays.asList(BigDecimal.class, Boolean.class, Date.class, Integer.class, Long.class, String.class);
        List<String> simpleGetterNames = new ArrayList<>();
        List<String> otherGetterNames = new ArrayList<>();
        for (Method method : bean.getClass().getDeclaredMethods()) {
            String methodName = method.getName();
            if (method.getParameterCount() == 0 && !methodName.equals("getId") &&
                    (methodName.startsWith("in") || methodName.startsWith("get"))) {
                Class<?> returnType = method.getReturnType();
                if (simpleTypes.contains(returnType) || returnType.isEnum()) {
                    simpleGetterNames.add(methodName);
                } else if (!List.class.isAssignableFrom(returnType)) {
                    otherGetterNames.add(methodName);
                }
            }
        }

        String tableName = "`" + camelToSnake(beanName) + "`";
        StringBuilder columnNames = null;
        StringBuilder columnValues = null;

        Collections.sort(simpleGetterNames);
        for (String methodName : simpleGetterNames) {
            columnNames = buildColumnNames(columnNames, methodName);
            columnValues = buildColumnValues(columnValues, methodName, bean);
        }

        Collections.sort(otherGetterNames);
        for (String methodName : otherGetterNames) {
            columnNames = buildColumnNames(columnNames, methodName + "Id");
            columnValues = buildColumnValues(columnValues, methodName, bean);
        }
        System.out.printf("INSERT INTO %s (%s)  VALUES (%s);%n", tableName, columnNames.toString(),
                columnValues.toString());
    }

    private static StringBuilder buildColumnNames(StringBuilder columnNames, String methodName) {
        String fieldName = null;
        if (methodName.startsWith("is")) {
            fieldName = methodName.substring(2);
        } else {
            fieldName = methodName.substring(3);
        }

        String columnName = "`" + camelToSnake(fieldName) + "`";
        if (columnNames == null) {
            columnNames = new StringBuilder(columnName);
        } else {
            columnNames.append(",").append(columnName);
        }
        return columnNames;
    }

    private static StringBuilder buildColumnValues(StringBuilder columnValues, String methodName, Object bean) {
        Class<?> returnType = null;
        Object value = null;
        try {
            Method method = bean.getClass().getDeclaredMethod(methodName);
            returnType = method.getReturnType();
            value = method.invoke(bean);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
        }

        String columnValue = null;
        if (value == null) {
            columnValue = "NULL";
        } else if (BigDecimal.class.isAssignableFrom(returnType)) {
            columnValue = String.valueOf(value);
        } else if (Boolean.class.isAssignableFrom(returnType)) {
            columnValue = String.valueOf(value).toUpperCase();
        } else if (Date.class.isAssignableFrom(returnType)) {
            columnValue = "'" + YYMD.format((Date) value) + "'";
        } else if (Integer.class.isAssignableFrom(returnType)) {
            columnValue = String.valueOf(value);
        } else if (Long.class.isAssignableFrom(returnType)) {
            columnValue = String.valueOf(value);
        } else if (Cat.class.isAssignableFrom(returnType)) {
            columnValue = String.format("(SELECT `id` FROM `cat` WHERE `name`='%s')", ((Cat) value).getName());
        } else if (Acct.class.isAssignableFrom(returnType)) {
            columnValue = String.format("(SELECT `id` FROM `acct` WHERE `name`='%s')", ((Acct) value).getName());
        } else if (Stmt.class.isAssignableFrom(returnType)) {
            Stmt stmt = (Stmt) value;
            columnValue = String.format(
                    "(SELECT x.`id` FROM `stmt` x INNER JOIN `acct` y ON y.`id`=x.`acct_id` WHERE x.`close_date`='%tF' AND y.`name`='%s')",
                    stmt.getCloseDate(), stmt.getAcct().getName());
        } else {
            columnValue = "'" + String.valueOf(value) + "'";
        }

        if (columnValues == null) {
            columnValues = new StringBuilder(columnValue);
        } else {
            columnValues.append(",").append(columnValue);
        }
        return columnValues;
    }

    private static String camelToSnake(String camelStr) {
        String ret = camelStr.replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2").replaceAll("([a-z])([A-Z])", "$1_$2");
        return ret.toLowerCase();
    }
}
