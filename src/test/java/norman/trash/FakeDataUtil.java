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
    private static final String INSERT_INTO_STMT_WITH_DATE =
            "INSERT INTO `stmt` (`close_date`,`open_date`,`version`,`acct_id`) VALUES ('%tF','%tF',0,(SELECT `id` FROM `acct` WHERE `name` = '%s'));%n";
    private static final String INSERT_INTO_STMT_WITH_NULL =
            "INSERT INTO `stmt` (`close_date`,`open_date`,`version`,`acct_id`) VALUES (NULL,'%tF',0,(SELECT `id` FROM `acct` WHERE `name` = '%s'));%n";
    private static final String INSERT_INTO_CAT = "INSERT INTO `cat` (`name`,`version`) VALUES ('%s',0);%n";
    private static final int NBR_OF_TRANS = 216;
    private static final String INSERT_INTO_TRAN =
            "INSERT INTO `tran` (`amount`,`post_date`,`version`,`debit_stmt_id`,`credit_stmt_id`) VALUES (%.2f,'%tF',0,%s,%s);%n";
    private static final String SELECT_STMT_WITH_DATE =
            "(SELECT x.`id` FROM `stmt` x INNER JOIN `acct` y ON y.`id`=x.`acct_id` WHERE x.`close_date`='%tF' AND y.`name`='%s')";
    private static final String SELECT_STMT_WITH_NULL =
            "(SELECT x.`id` FROM `stmt` x INNER JOIN `acct` y ON y.`id`=x.`acct_id` WHERE x.`close_date` IS NULL AND y.`name`='%s')";

    private FakeDataUtil() {
    }

    public static void main(String[] args) {
        LOGGER.debug("Starting FakeDataUtil");
        long acctId = 1;
        long stmtId = 1;
        long catId = 1;
        long tranId = 1;
        Map<String, Acct> uniqueAccts = new HashMap<>();

        for (int i = 0; i < NBR_OF_ACCTS; i++) {
            Acct acct = buildAcct(acctId++, uniqueAccts);
            System.out.printf(INSERT_INTO_ACCT, acct.getName(), acct.getType());

            Stmt stmt = null;
            do {
                stmt = buildStmt(acct, stmtId++);
                if (stmt.getCloseDate() == null) {
                    System.out.printf(INSERT_INTO_STMT_WITH_NULL, stmt.getOpenDate(), stmt.getAcct().getName());
                } else {
                    System.out.printf(INSERT_INTO_STMT_WITH_DATE, stmt.getCloseDate(), stmt.getOpenDate(),
                            stmt.getAcct().getName());
                }
            } while (stmt.getCloseDate() != null);
        }

        for (String name : CAT_NAMES) {
            Cat cat = buildCat(catId++, name);
            System.out.printf(INSERT_INTO_CAT, cat.getName());
        }

        for (int i = 0; i < NBR_OF_TRANS; i++) {
            Tran tran = buildTran(tranId, uniqueAccts);
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
            System.out.printf(INSERT_INTO_TRAN, tran.getAmount(), tran.getPostDate(), debitStmt, creditStmt);
        }
    }

    private static Acct buildAcct(long id, Map<String, Acct> uniqueAccts) {
        String name = null;
        AcctType acctType = null;
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
        uniqueAccts.put(name, acct);
        return acct;
    }

    private static Stmt buildStmt(Acct acct, long id) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        Date today = cal.getTime();

        Date openDate = null;
        Date closeDate = null;

        // If this is the first statement, open date is the beginning of time and close date is a bit more than a year ago.
        if (acct.getStmts().isEmpty()) {
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.MONTH, Calendar.JANUARY);
            cal.set(Calendar.YEAR, 1970);
            openDate = cal.getTime();
            cal.setTime(today);
            cal.add(Calendar.DATE, -1 * (RANDOM.nextInt(28) + 365));
            closeDate = cal.getTime();
        } else {
            // Otherwise, this statement's opening date is the day after the last close date and the this close date is
            // a month after the last close date.
            int lastIdx = acct.getStmts().size() - 1;
            Stmt lastStmt = acct.getStmts().get(lastIdx);
            Date lastCloseDate = lastStmt.getCloseDate();
            cal.setTime(lastCloseDate);
            cal.add(Calendar.DATE, 1);
            openDate = cal.getTime();
            cal.setTime(lastCloseDate);
            cal.add(Calendar.MONTH, 1);
            closeDate = cal.getTime();
        }

        // If the close date is today or later, set it to null to indicate this is the current statement.
        if (!closeDate.before(today)) {
            closeDate = null;
        }

        Stmt stmt = new Stmt();
        stmt.setId(id);
        stmt.setAcct(acct);
        stmt.setOpenDate(openDate);
        stmt.setCloseDate(closeDate);
        acct.getStmts().add(stmt);
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
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.add(Calendar.DATE, -1 * RANDOM.nextInt(365));
        Date postDate = cal.getTime();
        BigDecimal amount = BigDecimal.valueOf(RANDOM.nextInt(100000), 2);

        Tran tran = new Tran();
        tran.setId(id);
        tran.setDebitStmt(findStmt(postDate, debitAcctName, uniqueAccts));
        tran.setCreditStmt(findStmt(postDate, creditAcctName, uniqueAccts));
        tran.setPostDate(postDate);
        tran.setAmount(amount);
        return tran;
    }

    private static Stmt findStmt(Date postDate, String acctName, Map<String, Acct> uniqueAccts) {
        if (acctName == null) {
            return null;
        } else {
            Acct acct = uniqueAccts.get(acctName);
            Stmt stmt;
            int idx = 0;
            do {
                stmt = acct.getStmts().get(idx++);
            } while (stmt.getOpenDate().after(postDate) ||
                    stmt.getCloseDate() != null && stmt.getCloseDate().before(postDate));
            return stmt;
        }
    }
}
