package norman.trash;

import norman.trash.domain.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

public class FakeDataUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FakeDataUtil.class);
    private static final Random RANDOM = new Random();
    private static final int NBR_OF_ACCTS = 12;
    private static final int NBR_OF_TRANS = 1728;
    private static final String[] WORDS =
            {"Abominable", "Bulimic", "Cosmic", "Desperate", "Evil", "Funky", "Ginormous", "Hungry", "Inconceivable",
                    "Jurassic", "Kick-ass", "Ludicrous", "Malevolent", "Nuclear", "Obsequious", "Pedantic", "Quiescent",
                    "Recalcitrant", "Sleazy", "Taciturn", "Unbelievable", "Violent", "Withering", "Xenophobic", "Yucky",
                    "Zealous"};
    private static final String[] CAT_NAMES = {"Mortgage", "Groceries", "Utilities", "Miscellaneous"};
    private static final String[] CHECKING_NAMES = {"Bank", "Credit Union", "Saving & Loan"};
    private static final String[] CC_NAMES = {"Credit Card", "Plastic Credit", "Gold Card", "Platinum Card"};
    private static final String[] BILL_NAMES =
            {"Cable TV", "Gas", "Gym", "Insurance", "Lawn Service", "Power", "Water and Sewer"};
    private static final String INSERT_INTO_ACCT =
            "INSERT INTO `acct` (`name`,`type`,`version`,`address_name`,`address1`,`address2`,`city`,`state`,`zip_code`,`phone_number`,`credit_limit`) VALUES ('%s','%s',0,'%s','%s',%s,'%s','%s','%s','%s',%s);%n";
    private static final String INSERT_INTO_ACCT_NBR =
            "INSERT INTO `acct_nbr` (`eff_date`, `number`, `version`, `acct_id`) VALUES ('%tF','%s',0,(SELECT `id` FROM `acct` WHERE `name` = '%s'));%n";
    private static final String INSERT_INTO_STMT =
            "INSERT INTO `stmt` (`close_date`,`version`,`acct_id`) VALUES ('%tF',0,(SELECT `id` FROM `acct` WHERE `name` = '%s'));%n";
    private static final String INSERT_INTO_CAT = "INSERT INTO `cat` (`name`,`version`) VALUES ('%s',0);%n";
    private static final String INSERT_INTO_TRAN =
            "INSERT INTO `tran` (`amount`,`post_date`,`check_number`,`name`,`memo`,`version`,`debit_stmt_id`,`credit_stmt_id`,`cat_id`) VALUES (%.2f,'%tF',%s,'%s','%s',0,%s,%s,%s);%n";
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

        long catId = 1;
        long acctId = 1;
        long acctNbrId = 1;
        long stmtId = 1;
        long tranId = 1;
        Map<String, Cat> catMap = new HashMap<>();
        Map<String, Acct> acctMap = new HashMap<>();

        for (String name : CAT_NAMES) {
            Cat cat = buildCat(catId++, name);
            catMap.put(cat.getName(), cat);
            System.out.printf(INSERT_INTO_CAT, cat.getName());
        }

        for (int i = 0; i < NBR_OF_ACCTS; i++) {
            Acct acct = buildAcct(acctId++, acctMap);
            acctMap.put(acct.getName(), acct);
            String address2 = "NULL";
            if (acct.getAddress2() != null) {
                address2 = "'" + acct.getAddress2() + "'";
            }
            String creditLimit = "NULL";
            if (acct.getCreditLimit() != null) {
                creditLimit = String.format("%.2f", acct.getCreditLimit());
            }
            System.out
                    .printf(INSERT_INTO_ACCT, acct.getName(), acct.getType(), acct.getAddressName(), acct.getAddress1(),
                            address2, acct.getCity(), acct.getState(), acct.getZipCode(), acct.getPhoneNumber(),
                            creditLimit);

            int nbrOfAcctNbrs = RANDOM.nextInt(4) + 1;
            for (int j = 0; j < nbrOfAcctNbrs; j++) {
                int effDaysAgo = 400 - j * 100;
                AcctNbr acctNbr = buildAcctNbr(acctNbrId++, effDaysAgo, acct);
                System.out.printf(INSERT_INTO_ACCT_NBR, acctNbr.getEffDate(), acctNbr.getNumber(), acct.getName());
            }

            Stmt stmt;
            do {
                stmt = buildStmt(stmtId++, acct);
                System.out.printf(INSERT_INTO_STMT, stmt.getCloseDate(), stmt.getAcct().getName());
            } while (!stmt.getCloseDate().equals(endOfTime));
        }

        for (int i = 0; i < NBR_OF_TRANS; i++) {
            Tran tran = buildTran(tranId++, acctMap, catMap);
            String debitStmt = "NULL";
            if (tran.getDebitStmt() != null) {
                String acctName = tran.getDebitStmt().getAcct().getName();
                Date stmtDate = tran.getDebitStmt().getCloseDate();
                if (stmtDate == null) {
                    debitStmt = String.format(SELECT_STMT_WITH_NULL, acctName);
                } else {
                    debitStmt = String.format(SELECT_STMT_WITH_DATE, stmtDate, acctName);
                }
            }
            String creditStmt = "NULL";
            if (tran.getCreditStmt() != null) {
                String acctName = tran.getCreditStmt().getAcct().getName();
                Date stmtDate = tran.getCreditStmt().getCloseDate();
                if (stmtDate == null) {
                    creditStmt = String.format(SELECT_STMT_WITH_NULL, acctName);
                } else {
                    creditStmt = String.format(SELECT_STMT_WITH_DATE, stmtDate, acctName);
                }
            }
            String cat = "NULL";
            if (tran.getCat() != null) {
                cat = String.format(SELECT_CAT_WITH_NAME, tran.getCat().getName());
            }
            String checkNumber = "NULL";
            if (tran.getCheckNumber() != null) {
                checkNumber = "'" + tran.getCheckNumber() + "'";
            }
            System.out.printf(INSERT_INTO_TRAN, tran.getAmount(), tran.getPostDate(), checkNumber, tran.getName(),
                    tran.getMemo(), debitStmt, creditStmt, cat);
        }
    }

    private static Cat buildCat(long id, String name) {
        Cat cat = new Cat();
        cat.setId(id);
        cat.setName(name);
        return cat;
    }

    private static Acct buildAcct(long id, Map<String, Acct> acctMap) {
        Acct acct = new Acct();
        acct.setId(id);
        acct.setType(AcctType.values()[RANDOM.nextInt(AcctType.values().length)]);

        // Build acct name based on the account type.
        AcctType acctType = acct.getType();
        String name;
        do {
            String firstPartName = WORDS[RANDOM.nextInt(WORDS.length)];
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
        return acct;
    }

    private static AcctNbr buildAcctNbr(long id, int effDaysAgo, Acct acct) {
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
        cal.setTime(today);
        cal.add(Calendar.DATE, -1 * effDaysAgo);
        Date effDate = cal.getTime();
        acctNbr.setEffDate(effDate);

        // Attach to account.
        acctNbr.setAcct(acct);
        acct.getAcctNbrs().add(acctNbr);
        return acctNbr;
    }

    private static Stmt buildStmt(long id, Acct acct) {
        Stmt stmt = new Stmt();
        stmt.setId(id);

        // Generate a random close date.
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

        stmt.setCloseDate(closeDate);

        // Attach to account.
        stmt.setAcct(acct);
        acct.getStmts().add(stmt);
        return stmt;
    }

    private static Tran buildTran(long id, Map<String, Acct> acctMap, Map<String, Cat> catMap) {
        Tran tran = new Tran();
        tran.setId(id);
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DATE, -1 * RANDOM.nextInt(365));
        Date postDate = cal.getTime();
        tran.setPostDate(postDate);
        tran.setAmount(BigDecimal.valueOf(RANDOM.nextInt(100000), 2));
        tran.setName(WORDS[RANDOM.nextInt(WORDS.length)] + " " + WORDS[RANDOM.nextInt(WORDS.length)]);
        tran.setMemo(RandomStringUtils.randomAlphanumeric(10, 20));

        // Attach to statements from random accounts.
        int acctCase = RANDOM.nextInt(3);
        List<String> uniqueNames = new ArrayList<>(acctMap.keySet());
        String acctName = uniqueNames.get(RANDOM.nextInt(uniqueNames.size()));
        Acct acct = acctMap.get(acctName);
        Stmt stmt = findStmt(acct, postDate);
        if (acctCase == 0) {
            tran.setDebitStmt(stmt);
            stmt.getDebitTrans().add(tran);
        } else if (acctCase == 1) {
            tran.setCreditStmt(stmt);
            stmt.getCreditTrans().add(tran);
        } else {
            tran.setDebitStmt(stmt);
            stmt.getDebitTrans().add(tran);
            String creditAcctName;
            do {
                creditAcctName = uniqueNames.get(RANDOM.nextInt(uniqueNames.size()));
            } while (acctName.equals(creditAcctName));
            Acct creditAcct = acctMap.get(creditAcctName);
            Stmt creditStmt = findStmt(creditAcct, postDate);
            tran.setCreditStmt(creditStmt);
            creditStmt.getCreditTrans().add(tran);
        }

        // If this transaction comes from a checking account, we need a check number.
        Stmt debitStmt = tran.getDebitStmt();
        Stmt creditStmt = tran.getCreditStmt();
        if (debitStmt != null && debitStmt.getAcct().getType() == AcctType.CHECKING) {
            tran.setCheckNumber(RandomStringUtils.randomNumeric(4));
        }

        // If we have both a credit and debit account, change name and memo to reflect the credit account.
        if (debitStmt != null && creditStmt != null) {
            tran.setName(creditStmt.getAcct().getAddressName());
            tran.setMemo(creditStmt.getAcct().getAcctNbrs().iterator().next().getNumber());
        }

        // Attach to random category.
        if (RANDOM.nextInt(10) != 0) {
            Cat cat = catMap.get(CAT_NAMES[RANDOM.nextInt(CAT_NAMES.length)]);
            tran.setCat(cat);
            cat.getTrans().add(tran);
        }
        return tran;
    }

    private static Stmt findStmt(Acct acct, Date postDate) {
        Date closeDate = beginningOfTime;
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
