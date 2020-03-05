package norman.trash;

import norman.trash.domain.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static norman.trash.MessagesConstants.BEGINNING_BALANCE;

public class FakeDataUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FakeDataUtil.class);
    private static final Random RANDOM = new Random();
    private static final DateFormat YYYYMMDD = new SimpleDateFormat("yyyyMMdd");
    private static final String[] WORDS =
            {"Abominable", "Bulimic", "Cosmic", "Desperate", "Evil", "Funky", "Ginormous", "Hungry", "Inconceivable",
                    "Jurassic", "Kick-ass", "Ludicrous", "Malevolent", "Nuclear", "Obsequious", "Pedantic", "Quiescent",
                    "Recalcitrant", "Sleazy", "Taciturn", "Unbelievable", "Violent", "Withering", "Xenophobic", "Yucky",
                    "Zealous"};
    private static final String[] CAT_NAMES =
            {"Mortgage", "Groceries", "Utilities", "Automobile", "Cable", "Miscellaneous"};
    private static final int NBR_OF_PATTERNS_MAX = 3;
    private static final int NBR_OF_PATTERNS_MIN = 1;
    //
    private static final String[] CHECKING_NAMES = {"Bank", "Credit Union", "Saving & Loan"};
    private static final String[] CC_NAMES = {"Credit Card", "Plastic Credit", "Gold Card", "Platinum Card"};
    private static final String[] BILL_NAMES =
            {"Cable TV", "Gas", "Gym", "Insurance", "Lawn Service", "Power", "Water and Sewer"};
    private static final int NBR_OF_ACCTS = 12;
    private static final int NBR_OF_ACCT_NBRS_MAX = 4;
    private static final int NBR_OF_ACCT_NBRS_MIN = 1;
    private static final int CENTS_MAX = 500000;
    private static final int CENTS_MIN = -500000;
    private static final int NBR_OF_TRANS_MAX = 150;
    private static final int NBR_OF_TRANS_MIN = 10;

    private FakeDataUtil() {
    }

    public static void main(String[] args) {
        LOGGER.debug("Starting FakeDataUtil");

        // Set standard dates.
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.YEAR, 9999);
        Date endOfTime = cal.getTime();

        long catId = 1;
        long patternId = 1;
        long acctId = 1;
        long acctNbrId = 1;
        long stmtId = 1;
        long tranId = 1;
        Map<String, Cat> catMap = new LinkedHashMap<>();
        Map<String, Acct> acctMap = new LinkedHashMap<>();

        for (String name : CAT_NAMES) {
            Cat cat = buildCat(catId++, name);
            catMap.put(cat.getName(), cat);

            int nbrOfPatterns = RANDOM.nextInt(NBR_OF_PATTERNS_MAX - NBR_OF_PATTERNS_MIN + 1) + NBR_OF_PATTERNS_MIN;
            for (int j = 0; j < nbrOfPatterns; j++) {
                Pattern pattern = buildPattern(patternId++, cat);
            }
        }

        for (int i = 0; i < NBR_OF_ACCTS; i++) {
            Acct acct = buildAcct(acctId++, acctMap);
            acctMap.put(acct.getName(), acct);

            int nbrOfAcctNbrs = RANDOM.nextInt(NBR_OF_ACCT_NBRS_MAX - NBR_OF_ACCT_NBRS_MIN + 1) + NBR_OF_ACCT_NBRS_MIN;
            for (int j = 0; j < nbrOfAcctNbrs; j++) {
                int effDaysAgo = 400 - j * 100;
                AcctNbr acctNbr = buildAcctNbr(acctNbrId++, effDaysAgo, acct);
            }

            Stmt stmt;
            do {
                stmt = buildStmt(stmtId++, acct, tranId++);
            } while (!stmt.getCloseDate().equals(endOfTime));

            int nbrOfTrans = RANDOM.nextInt(NBR_OF_TRANS_MAX - NBR_OF_TRANS_MIN + 1) + NBR_OF_TRANS_MIN;
            for (int j = 0; j < nbrOfTrans; j++) {
                Tran tran = buildTran(tranId++, acct, catMap);
            }
        }

        // Update statements.
        for (Acct acct : acctMap.values()) {
            Stmt previousStmt = null;
            for (Stmt stmt : acct.getStmts()) {
                if (previousStmt != null && !stmt.getCloseDate().equals(endOfTime)) {
                    updateStmt(stmt, previousStmt);
                }
                previousStmt = stmt;
            }
        }

        // Now print insert statements for everything.
        for (Cat cat : catMap.values()) {
            printInsertCat(cat);
            for (Pattern pattern : cat.getPatterns()) {
                printInsertPattern(pattern);
            }
        }
        for (Acct acct : acctMap.values()) {
            printInsertAcct(acct);
            for (AcctNbr acctNbr : acct.getAcctNbrs()) {
                printInsertAcctNbr(acctNbr);
            }
            boolean first = true;
            for (Stmt stmt : acct.getStmts()) {
                if (first || stmt.getCloseDate().equals(endOfTime)) {
                    printInsertBeginOrCurrentStmt(stmt);
                    first = false;
                } else {
                    printInsertStmt(stmt);
                }
                for (Tran tran : stmt.getTrans()) {
                    printInsertTran(tran);
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

    public static Pattern buildPattern(long id, Cat cat) {
        Pattern pattern = new Pattern();
        pattern.setId(id);
        pattern.setSeq((int) id);
        pattern.setRegex(".*" + WORDS[RANDOM.nextInt(WORDS.length)].toUpperCase() + ".*");
        pattern.setCat(cat);
        cat.getPatterns().add(pattern);
        return pattern;
    }

    public static Acct buildAcct(long id, Map<String, Acct> acctMap) {
        Acct acct = new Acct();
        acct.setId(id);
        acct.setType(AcctType.values()[RANDOM.nextInt(AcctType.values().length)]);

        // Build acct name based on the account type.
        AcctType acctType = acct.getType();
        String firstPartName;
        String name;
        do {
            firstPartName = WORDS[RANDOM.nextInt(WORDS.length)];
            String lastPartName = null;
            if (acctType == AcctType.CHECKING) {
                int len = CHECKING_NAMES.length;
                int idx = RANDOM.nextInt(len);
                lastPartName = CHECKING_NAMES[idx];
            } else if (acctType == AcctType.CC) {
                int len = CC_NAMES.length;
                int idx = RANDOM.nextInt(len);
                lastPartName = CC_NAMES[idx];
            } else if (acctType == AcctType.BILL) {
                int len = BILL_NAMES.length;
                int idx = RANDOM.nextInt(len);
                lastPartName = BILL_NAMES[idx];
            } else {
                throw new RuntimeException("Invalid acctType=\"" + acctType + "\"");
            }
            name = firstPartName + " " + lastPartName;
        } while (acctMap.keySet().contains(name));
        acct.setName(name);

        acct.setAddressName(name + " Inc.");
        acct.setAddress1(RandomStringUtils.randomNumeric(4) + " " + WORDS[RANDOM.nextInt(WORDS.length)] + " St");
        if (RANDOM.nextInt(2) != 0) {
            acct.setAddress2("Suite " + RandomStringUtils.randomNumeric(3));
        }
        acct.setCity(WORDS[RANDOM.nextInt(WORDS.length)] + " City");
        acct.setState(WORDS[RANDOM.nextInt(WORDS.length)].substring(0, 2).toUpperCase());
        acct.setZipCode(RandomStringUtils.randomNumeric(5) + "-" + RandomStringUtils.randomNumeric(4));
        acct.setPhoneNumber(RandomStringUtils.randomNumeric(3) + "-" + RandomStringUtils.randomNumeric(3) + "-" +
                RandomStringUtils.randomNumeric(4));
        if (acctType == AcctType.CC) {
            acct.setCreditLimit(BigDecimal.valueOf((RANDOM.nextInt(99) + 1) * 100000, 2));
        }
        acct.setOfxOrganization(firstPartName);
        acct.setOfxFid(RandomStringUtils.randomNumeric(5));
        acct.setOfxBankId(RandomStringUtils.randomNumeric(10));
        return acct;
    }

    public static AcctNbr buildAcctNbr(long id, int effDaysAgo, Acct acct) {
        AcctNbr acctNbr = new AcctNbr();
        acctNbr.setId(id);

        // Build account number based on account type.
        String number = null;
        if (acct.getType() == AcctType.CHECKING) {
            number = RandomStringUtils.randomNumeric(12);
        } else if (acct.getType() == AcctType.CC) {
            number = RandomStringUtils.randomNumeric(16);
        } else if (acct.getType() == AcctType.BILL) {
            number = RandomStringUtils.randomNumeric(5);
        } else {
            throw new RuntimeException("Invalid acctType=\"" + acct.getType() + "\"");
        }
        acctNbr.setNumber(number);

        // Build effective date.
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.add(Calendar.DATE, -1 * effDaysAgo);
        Date effDate = cal.getTime();
        acctNbr.setEffDate(effDate);

        // Attach to account.
        acctNbr.setAcct(acct);
        acct.getAcctNbrs().add(acctNbr);
        return acctNbr;
    }

    public static Stmt buildCurrentStmt(long id, Acct acct) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.YEAR, 9999);
        Date endOfTime = cal.getTime();
        return buildStmtImpl(id, acct, endOfTime);
    }

    public static Stmt buildBeginStmt(long id, Acct acct) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.add(Calendar.DATE, -1 * (RANDOM.nextInt(28) + 365));
        Date beginDate = cal.getTime();
        return buildStmtImpl(id, acct, beginDate);
    }

    private static Stmt buildStmtImpl(long id, Acct acct, Date closeDate) {
        Stmt stmt = new Stmt();
        stmt.setId(id);
        //stmt.setOpenBalance();
        //stmt.setCredits();
        //stmt.setDebits();
        //stmt.setFees();
        //stmt.setInterest();
        //stmt.setCloseBalance();
        //stmt.setMinimumDue();
        //stmt.setDueDate();
        stmt.setCloseDate(closeDate);
        stmt.setAcct(acct);
        acct.getStmts().add(stmt);
        return stmt;
    }

    private static Stmt buildStmt(long id, Acct acct, long tranId) {
        Stmt stmt = new Stmt();
        stmt.setId(id);

        // Generate a random close date.
        Date closeDate = null;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        Date today = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, 31);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.YEAR, 9999);
        Date endOfTime = cal.getTime();

        // If this is the first statement, generate a random close date a bit more than a year ago.
        boolean firstStmt = acct.getStmts().isEmpty();
        if (firstStmt) {
            cal.setTime(today);
            cal.add(Calendar.DATE, -1 * (RANDOM.nextInt(28) + 365));
            closeDate = cal.getTime();
        } else {
            // Otherwise this close date is a month after the last close date.
            int lastIdx = acct.getStmts().size() - 1;
            Stmt lastStmt = acct.getStmts().get(lastIdx);
            Date lastCloseDate = lastStmt.getCloseDate();
            cal.setTime(lastCloseDate);
            cal.add(Calendar.MONTH, 1);
            closeDate = cal.getTime();
        }

        // If the close date is today or later, set it to the end of time.
        if (!closeDate.before(today)) {
            closeDate = endOfTime;
        }

        stmt.setCloseDate(closeDate);

        // Attach to account.
        stmt.setAcct(acct);
        acct.getStmts().add(stmt);

        // Also add the first transaction to set beginning balance.
        if (firstStmt) {
            Tran tran = new Tran();
            tran.setId(tranId);
            tran.setPostDate(closeDate);
            int cents = RANDOM.nextInt(CENTS_MAX - CENTS_MIN + 1) + CENTS_MIN;
            BigDecimal amount = BigDecimal.valueOf(cents, 2);

            tran.setName(BEGINNING_BALANCE);
            tran.setAmount(amount);
            tran.setStmt(stmt);
            stmt.getTrans().add(tran);
        }
        return stmt;
    }

    private static Tran buildTran(long id, Date beginDate, Date endDate, Acct acct, List<Cat> cats) {
        Tran tran = new Tran();
        tran.setId(id);

        // Post date is some random date between the begin date (exclusive) and end date (inclusive).
        long diffDays = (endDate.getTime() - beginDate.getTime()) / 86400000L;
        Calendar cal = Calendar.getInstance();
        cal.setTime(beginDate);
        cal.add(Calendar.DATE, RANDOM.nextInt((int) diffDays) + 1);
        tran.setPostDate(cal.getTime());

        int cents = RANDOM.nextInt(CENTS_MAX - CENTS_MIN + 1) + CENTS_MIN;
        BigDecimal amount = BigDecimal.valueOf(cents, 2);
        tran.setAmount(amount);

        // If checking account and amount is a debit, there is half a chance this transaction has a check number.
        if (acct.getType() == AcctType.CHECKING && amount.compareTo(BigDecimal.ZERO) > 0 && RANDOM.nextInt(2) == 0) {
            tran.setCheckNumber(RandomStringUtils.randomNumeric(4));
        }

        tran.setName(WORDS[RANDOM.nextInt(WORDS.length)] + " " + WORDS[RANDOM.nextInt(WORDS.length)]);
        tran.setMemo(RandomStringUtils.randomAlphanumeric(10, 20));
        //tran.setOfxFitId();

        // Find the correct statement for this transaction.
        List<Stmt> stmts = acct.getStmts();
        stmts.sort(Comparator.comparing(Stmt::getCloseDate));
        Stmt stmt = null;
        for (int i = 0; i < stmts.size(); i++) {
            stmt = stmts.get(i);
            if (tran.getPostDate().after(stmt.getCloseDate())) {
                break;
            }
        }
        if (stmt != null) {
            tran.setStmt(stmt);
            stmt.getTrans().add(tran);
        }

        // There is half a chance this transaction has a category assigned to it.
        if (RANDOM.nextInt(2) == 0) {
            Cat cat = cats.get(RANDOM.nextInt(cats.size()));
            tran.setCat(cat);
            cat.getTrans().add(tran);
        }
        return tran;
    }

    private static Tran buildTran(long id, Acct acct, Map<String, Cat> catMap) {
        Tran tran = new Tran();
        tran.setId(id);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.add(Calendar.DATE, -1 * RANDOM.nextInt(365));
        Date postDate = cal.getTime();
        tran.setPostDate(postDate);
        int cents = RANDOM.nextInt(CENTS_MAX - CENTS_MIN + 1) + CENTS_MIN;
        BigDecimal amount = BigDecimal.valueOf(cents, 2);
        tran.setAmount(amount);
        tran.setName(WORDS[RANDOM.nextInt(WORDS.length)] + " " + WORDS[RANDOM.nextInt(WORDS.length)]);
        tran.setMemo(RandomStringUtils.randomAlphanumeric(10, 20));

        // Attach to statements;
        Stmt stmt = findStmt(acct, postDate);
        tran.setStmt(stmt);
        stmt.getTrans().add(tran);

        // For checking accounts, we want a check number half the time.
        if (acct.getType() == AcctType.CHECKING) {
            if (RANDOM.nextInt(2) == 0) {
                tran.setCheckNumber(RandomStringUtils.randomNumeric(4));
            }
        }

        // Attach to random category 9 out of 10 times.
        if (RANDOM.nextInt(10) != 0) {
            Cat cat = catMap.get(CAT_NAMES[RANDOM.nextInt(CAT_NAMES.length)]);
            tran.setCat(cat);
            cat.getTrans().add(tran);
        }
        return tran;
    }

    public static DataFile buildDataFile(long id) {
        DataFile dataFile = new DataFile();
        dataFile.setId(id);
        dataFile.setOriginalFilename(
                WORDS[RANDOM.nextInt(WORDS.length)].toLowerCase() + "-" + RANDOM.nextInt(10000) + ".ofx");
        dataFile.setContentType("application/octet-stream");
        dataFile.setSize(RANDOM.nextInt(30000) + 800L);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, -1 * RANDOM.nextInt(30000000));
        dataFile.setUploadTimestamp(cal.getTime());
        dataFile.setStatus(DataFileStatus.UPLOADED);
        //dataFile.setOfxOrganization(WORDS[RANDOM.nextInt(WORDS.length)]);
        //dataFile.setOfxFid(RandomStringUtils.randomNumeric(5));
        //dataFile.setOfxBankId(RandomStringUtils.randomNumeric(10));
        //dataFile.setOfxAcctId(RandomStringUtils.randomNumeric(20));
        //dataFile.setOfxType(AcctType.CHECKING);
        //dataFile.setAcct(acct);
        return dataFile;
    }

    public static DataLine buildDataLine(long id, int idx, DataFile dataFile) {
        DataLine dataLine = new DataLine();
        dataLine.setId(id);
        dataLine.setSeq(idx + 1);
        dataLine.setText(RandomStringUtils.randomAlphanumeric(100));
        dataLine.setDataFile(dataFile);
        dataFile.getDataLines().add(dataLine);
        return dataLine;
    }

    public static DataTran buildDataTran(long id, DataFile dataFile) {
        DataTran dataTran = new DataTran();
        dataTran.setId(id);
        dataTran.setOfxType(TranType.values()[RANDOM.nextInt(TranType.values().length)]);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.add(Calendar.DATE, -1 * RANDOM.nextInt(365));
        Date postDate = cal.getTime();
        dataTran.setOfxPostDate(postDate);
        //dataTran.setOfxUserDate();
        int cents = RANDOM.nextInt(CENTS_MAX - CENTS_MIN + 1) + CENTS_MIN;
        BigDecimal amount = BigDecimal.valueOf(cents, 2);
        dataTran.setOfxAmount(amount);
        dataTran.setOfxFitId(YYYYMMDD.format(postDate) + RandomStringUtils.randomNumeric(23));
        //dataTran.setOfxSic();

        // If checking account and amount is a debit, there is half a chance this transaction has a check number.
        if (dataFile.getOfxType() == AcctType.CHECKING && amount.compareTo(BigDecimal.ZERO) > 0 &&
                RANDOM.nextInt(2) == 0) {
            dataTran.setOfxCheckNumber(RandomStringUtils.randomNumeric(4));
        }

        //dataTran.setOfxCorrectFitId();
        //dataTran.setOfxCorrectAction();
        dataTran.setOfxName(WORDS[RANDOM.nextInt(WORDS.length)] + " " + WORDS[RANDOM.nextInt(WORDS.length)]);
        //dataTran.setOfxCategory();
        dataTran.setOfxMemo(RandomStringUtils.randomAlphanumeric(10, 20));
        dataTran.setDataFile(dataFile);
        dataFile.getDataTrans().add(dataTran);
        return dataTran;
    }

    private static Stmt findStmt(Acct acct, Date postDate) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.YEAR, 1970);
        Date closeDate = cal.getTime();
        List<Stmt> stmts = acct.getStmts();
        Stmt stmt = null;
        for (int i = 0; i < stmts.size(); i++) {
            Date lastCloseDate = closeDate;
            closeDate = stmts.get(i).getCloseDate();
            if (lastCloseDate.before(postDate) && !closeDate.before(postDate)) {
                stmt = stmts.get(i);
            }
        }
        return stmt;
    }

    private static void updateStmt(Stmt stmt, Stmt previousStmt) {
        BigDecimal openBalance;
        if (previousStmt.getCloseBalance() != null) {
            openBalance = previousStmt.getCloseBalance();
        } else {
            openBalance = BigDecimal.ZERO;
            for (Tran tran : previousStmt.getTrans()) {
                openBalance = openBalance.add(tran.getAmount());
            }
        }
        stmt.setOpenBalance(openBalance);

        BigDecimal credits = BigDecimal.ZERO;
        BigDecimal debits = BigDecimal.ZERO;
        for (Tran tran : stmt.getTrans()) {
            if (tran.getAmount().compareTo(BigDecimal.ZERO) >= 0) {
                credits = credits.add(tran.getAmount());
            } else {
                debits = debits.subtract(tran.getAmount());
            }
        }
        stmt.setDebits(debits);
        stmt.setCredits(credits);

        stmt.setFees(BigDecimal.ZERO);
        stmt.setInterest(BigDecimal.ZERO);
        stmt.setCloseBalance(openBalance.add(debits).add(credits));
        stmt.setMinimumDue(BigDecimal.valueOf(2500, 2));

        Calendar cal = Calendar.getInstance();
        cal.setTime(stmt.getCloseDate());
        cal.add(Calendar.DATE, 27);
        stmt.setDueDate(cal.getTime());
    }

    private static void printInsertCat(Cat cat) {
        String insertIntoCat = "INSERT INTO `cat` (`name`,`version`) VALUES ('%s',0);%n";
        System.out.printf(insertIntoCat, cat.getName());
    }

    private static void printInsertPattern(Pattern pattern) {
        String insertIntoPattern = "INSERT INTO `pattern` (`regex`,`seq`,`version`,`cat_id`)" +
                " VALUES ('%s',%d,0,(SELECT `id` FROM `cat` WHERE `name`='%s'));%n";
        System.out.printf(insertIntoPattern, pattern.getRegex(), pattern.getSeq(), pattern.getCat().getName());
    }

    private static void printInsertAcct(Acct acct) {
        String address2 = "NULL";
        if (acct.getAddress2() != null) {
            address2 = "'" + acct.getAddress2() + "'";
        }

        String creditLimit = "NULL";
        if (acct.getCreditLimit() != null) {
            creditLimit = String.format("%.2f", acct.getCreditLimit());
        }

        String insertIntoAcct =
                "INSERT INTO `acct` (`address1`,`address2`,`address_name`,`city`,`credit_limit`,`name`,`phone_number`,`state`,`type`,`version`,`zip_code`)" +
                        " VALUES ('%s',%s,'%s','%s',%s,'%s','%s','%s','%s',0,'%s');%n";
        System.out.printf(insertIntoAcct, acct.getAddress1(), address2, acct.getAddressName(), acct.getCity(),
                creditLimit, acct.getName(), acct.getPhoneNumber(), acct.getState(), acct.getType(), acct.getZipCode());
    }

    private static void printInsertAcctNbr(AcctNbr acctNbr) {
        String insertIntoAcctNbr = "INSERT INTO `acct_nbr` (`eff_date`, `number`, `version`, `acct_id`)" +
                " VALUES ('%tF','%s',0,(SELECT `id` FROM `acct` WHERE `name`='%s'));%n";
        System.out.printf(insertIntoAcctNbr, acctNbr.getEffDate(), acctNbr.getNumber(), acctNbr.getAcct().getName());
    }

    private static void printInsertStmt(Stmt stmt) {
        String insertIntoStmt =
                "INSERT INTO `stmt` (`close_balance`,`close_date`,`credits`,`debits`,`due_date`,`fees`,`interest`,`minimum_due`,`open_balance`,`version`,`acct_id`)" +
                        " VALUES (%.2f,'%tF',%.2f,%.2f,'%tF',%.2f,%.2f,%.2f,%.2f,0,(SELECT `id` FROM `acct` WHERE `name`='%s'));%n";
        System.out.printf(insertIntoStmt, stmt.getCloseBalance(), stmt.getCloseDate(), stmt.getCredits(),
                stmt.getDebits(), stmt.getDueDate(), stmt.getFees(), stmt.getInterest(), stmt.getMinimumDue(),
                stmt.getOpenBalance(), stmt.getAcct().getName());
    }

    private static void printInsertBeginOrCurrentStmt(Stmt stmt) {
        String insertIntoStmt = "INSERT INTO `stmt` (`close_date`,`version`,`acct_id`)" +
                " VALUES ('%tF',0,(SELECT `id` FROM `acct` WHERE `name`='%s'));%n";
        System.out.printf(insertIntoStmt, stmt.getCloseDate(), stmt.getAcct().getName());
    }

    private static void printInsertTran(Tran tran) {
        String checkNumber = "NULL";
        if (tran.getCheckNumber() != null) {
            checkNumber = "'" + tran.getCheckNumber() + "'";
        }

        String memo = "NULL";
        if (tran.getMemo() != null) {
            memo = "'" + tran.getMemo() + "'";
        }

        String selectCatWithName = "(SELECT `id` FROM `cat` WHERE `name`='%s')";
        String cat = "NULL";
        if (tran.getCat() != null) {
            cat = String.format(selectCatWithName, tran.getCat().getName());
        }

        String selectStmtWithDateAndName =
                "(SELECT x.`id` FROM `stmt` x INNER JOIN `acct` y ON y.`id`=x.`acct_id` WHERE x.`close_date`='%tF' AND y.`name`='%s')";
        String stmt = "NULL";
        String acctName = tran.getStmt().getAcct().getName();
        Date stmtDate = tran.getStmt().getCloseDate();
        stmt = String.format(selectStmtWithDateAndName, stmtDate, acctName);

        String insertIntoTran =
                "INSERT INTO `tran` (`amount`,`check_number`,`memo`,`name`,`post_date`,`version`,`stmt_id`,`cat_id`)" +
                        " VALUES (%.2f,%s,%s,'%s','%tF',0,%s,%s);%n";
        System.out.printf(insertIntoTran, tran.getAmount(), checkNumber, memo, tran.getName(), tran.getPostDate(), stmt,
                cat);
    }
}
