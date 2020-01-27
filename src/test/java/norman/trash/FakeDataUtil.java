package norman.trash;

import norman.trash.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

public class FakeDataUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FakeDataUtil.class);
    private static final Random RANDOM = new Random();
    private static final String[] FIRST_PART_NAMES =
            {"Abominable", "Bulimic", "Cosmic", "Desperate", "Evil", "Funky", "Ginormous", "Hungry", "Inconceivable",
                    "Jurassic", "Kick-ass", "Ludicrous", "Malevolent", "Nuclear", "Obsequious", "Pedantic", "Quiescent",
                    "Recalcitrant", "Sleazy", "Taciturn", "Unbelievable", "Violent", "Withering", "Xenophobic", "Yucky",
                    "Zealous"};
    private static final String[] CHECKING_NAMES = {"Bank", "Credit Union", "Saving & Loan"};
    private static final String[] CC_NAMES = {"Credit Card", "Plastic Credit", "Gold Card", "Platinum Card"};
    private static final String[] BILL_NAMES =
            {"Cable TV", "Gas", "Gym", "Insurance", "Lawn Service", "Power", "Water and Sewer"};
    private static final String[] CAT_NAMES = {"Mortgage", "Groceries", "Utilities", "Miscellaneous"};
    private static final int NBR_OF_ACCTS = 6;
    private static final String INSERT_INTO_ACCT =
            "INSERT INTO `acct` (`name`,`type`,`version`) VALUES ('%s','%s',0);%n";
    private static final String INSERT_INTO_STMT =
            "INSERT INTO `stmt` (`close_date`,`version`,`acct_id`) VALUES ('%tF',0,(SELECT `id` FROM `acct` WHERE `name` = '%s'));%n";
    private static final String INSERT_INTO_CAT = "INSERT INTO `cat` (`name`,`version`) VALUES ('%s',0);%n";
    private static final int NBR_OF_TRANS = 216;
    private static final String INSERT_INTO_TRAN =
            "INSERT INTO `tran` (`amount`,`post_date`,`version`,`debit_stmt_id`,`credit_stmt_id`,`cat_id`) VALUES (%.2f,'%tF',0,%s,%s,%s);%n";
    private static final String SELECT_STMT_WITH_DATE =
            "(SELECT x.`id` FROM `stmt` x INNER JOIN `acct` y ON y.`id`=x.`acct_id` WHERE x.`close_date`='%tF' AND y.`name`='%s')";
    private static final String SELECT_STMT_WITH_NULL =
            "(SELECT x.`id` FROM `stmt` x INNER JOIN `acct` y ON y.`id`=x.`acct_id` WHERE x.`close_date` IS NULL AND y.`name`='%s')";
    private static final String SELECT_CAT_WITH_NAME = "(SELECT `id` FROM `cat` WHERE `name`='%s')";
    private static Date today;
    private static Date endOfTime;
    private static Date beginningOfTime;

    private FakeDataUtil() {
    }

    public static void main(String[] args) {
        LOGGER.debug("Starting FakeDataUtil");

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        today = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.YEAR, 1970);
        beginningOfTime = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, 31);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.YEAR, 9999);
        endOfTime = cal.getTime();

        long acctId = 1;
        long stmtId = 1;
        long catId = 1;
        long tranId = 1;
        Map<String, Acct> uniqueAccts = new HashMap<>();
        List<Stmt> stmts = new ArrayList<>();
        List<Tran> trans = new ArrayList<>();

        for (String name : CAT_NAMES) {
            Cat cat = buildCat(catId++, name);
            System.out.printf(INSERT_INTO_CAT, cat.getName());
        }

        for (int i = 0; i < NBR_OF_ACCTS; i++) {
            Acct acct = buildAcct(acctId++, uniqueAccts);
            uniqueAccts.put(acct.getName(), acct);
            System.out.printf(INSERT_INTO_ACCT, acct.getName(), acct.getType());

            Stmt stmt;
            do {
                stmt = buildStmt(acct, stmtId++);
                stmts.add(stmt);
            } while (!stmt.getCloseDate().equals(endOfTime));
        }

        for (int i = 0; i < NBR_OF_TRANS; i++) {
            Tran tran = buildTran(tranId, uniqueAccts);
            trans.add(tran);
        }

        for (Stmt stmt : stmts) {
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (Tran tran : stmt.getDebitTrans()) {
                totalAmount = totalAmount.subtract(tran.getAmount());
            }
            for (Tran tran : stmt.getCreditTrans()) {
                totalAmount = totalAmount.add(tran.getAmount());
            }
            System.out.printf(INSERT_INTO_STMT, stmt.getCloseDate(), stmt.getAcct().getName());
        }

        for (Tran tran : trans) {
            String debitStmt = "null";
            if (tran.getDebitStmt() != null) {
                String acctName = tran.getDebitStmt().getAcct().getName();
                Date stmtDate = tran.getDebitStmt().getCloseDate();
                if (stmtDate == null) {
                    debitStmt = String.format(SELECT_STMT_WITH_NULL, acctName);
                } else {
                    debitStmt = String.format(SELECT_STMT_WITH_DATE, stmtDate, acctName);
                }
            }
            String creditStmt = "null";
            if (tran.getCreditStmt() != null) {
                String acctName = tran.getCreditStmt().getAcct().getName();
                Date stmtDate = tran.getCreditStmt().getCloseDate();
                if (stmtDate == null) {
                    creditStmt = String.format(SELECT_STMT_WITH_NULL, acctName);
                } else {
                    creditStmt = String.format(SELECT_STMT_WITH_DATE, stmtDate, acctName);
                }
            }
            String cat = "null";
            if (tran.getCat() != null) {
                cat = String.format(SELECT_CAT_WITH_NAME, tran.getCat().getName());
            }
            System.out.printf(INSERT_INTO_TRAN, tran.getAmount(), tran.getPostDate(), debitStmt, creditStmt, cat);
        }
    }

    private static Acct buildAcct(long id, Map<String, Acct> uniqueAccts) {
        String name;
        AcctType acctType;
        do {
            String firstPartName = FIRST_PART_NAMES[RANDOM.nextInt(FIRST_PART_NAMES.length)];
            acctType = AcctType.values()[RANDOM.nextInt(AcctType.values().length)];
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
        } while (uniqueAccts.keySet().contains(name));

        Acct acct = new Acct();
        acct.setId(id);
        acct.setName(name);
        acct.setType(acctType);
        return acct;
    }

    private static Stmt buildStmt(Acct acct, long id) {
        Date closeDate = null;
        Calendar cal = Calendar.getInstance();

        // If this is the first statement, generate a random close date a bit more than a year ago.
        if (acct.getStmts().isEmpty()) {
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

        Stmt stmt = new Stmt();
        stmt.setId(id);
        stmt.setAcct(acct);
        acct.getStmts().add(stmt);
        stmt.setCloseDate(closeDate);
        return stmt;
    }

    private static Cat buildCat(long id, String name) {
        Cat cat = new Cat();
        cat.setId(id);
        cat.setName(name);
        return cat;
    }

    private static Tran buildTran(long id, Map<String, Acct> uniqueAccts) {
        List<String> uniqueNames = new ArrayList<>(uniqueAccts.keySet());
        String debitAcctName = null;
        String creditAcctName = null;
        int nameCase = RANDOM.nextInt(3);
        if (nameCase == 0) {
            debitAcctName = uniqueNames.get(RANDOM.nextInt(uniqueNames.size()));
        } else if (nameCase == 1) {
            creditAcctName = uniqueNames.get(RANDOM.nextInt(uniqueNames.size()));
        } else {
            debitAcctName = uniqueNames.get(RANDOM.nextInt(uniqueNames.size()));
            do {
                creditAcctName = uniqueNames.get(RANDOM.nextInt(uniqueNames.size()));
            } while (debitAcctName.equals(creditAcctName));
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DATE, -1 * RANDOM.nextInt(365));
        Date postDate = cal.getTime();
        BigDecimal amount = BigDecimal.valueOf(RANDOM.nextInt(100000), 2);
        String catName = null;
        if (RANDOM.nextInt(10) != 0) {
            catName = CAT_NAMES[RANDOM.nextInt(CAT_NAMES.length)];
        }

        Tran tran = new Tran();
        tran.setId(id);
        Stmt debitStmt = findStmt(postDate, debitAcctName, uniqueAccts);
        if (debitStmt != null) {
            tran.setDebitStmt(debitStmt);
            debitStmt.getDebitTrans().add(tran);
        }
        Stmt creditStmt = findStmt(postDate, creditAcctName, uniqueAccts);
        if (creditStmt != null) {
            tran.setCreditStmt(creditStmt);
            creditStmt.getCreditTrans().add(tran);
        }
        tran.setPostDate(postDate);
        tran.setAmount(amount);
        if (catName != null) {
            Cat cat = new Cat();
            cat.setName(catName);
            tran.setCat(cat);
        }
        return tran;
    }

    private static Stmt findStmt(Date postDate, String acctName, Map<String, Acct> uniqueAccts) {
        if (acctName == null) {
            return null;
        } else {
            Date closeDate = beginningOfTime;

            Acct acct = uniqueAccts.get(acctName);
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
    }
}
