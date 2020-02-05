package norman.trash;

import norman.trash.domain.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

import static norman.trash.MessagesConstants.BEGINNING_BALANCE;

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
    private static Date today;
    private static Date endOfTime;
    private static Date beginningOfTime;

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
        Map<String, Cat> catMap = new LinkedHashMap<>();
        Map<String, Acct> acctMap = new LinkedHashMap<>();
        List<Tran> tranList = new ArrayList<>();

        for (String name : CAT_NAMES) {
            Cat cat = buildCat(catId++, name);
            catMap.put(cat.getName(), cat);
        }

        for (int i = 0; i < NBR_OF_ACCTS; i++) {
            Acct acct = buildAcct(acctId++, acctMap);
            acctMap.put(acct.getName(), acct);

            int nbrOfAcctNbrs = RANDOM.nextInt(4) + 1;
            for (int j = 0; j < nbrOfAcctNbrs; j++) {
                int effDaysAgo = 400 - j * 100;
                AcctNbr acctNbr = buildAcctNbr(acctNbrId++, effDaysAgo, acct);
            }

            Stmt stmt;
            do {
                stmt = buildStmt(stmtId++, acct, tranId++, tranList);
            } while (!stmt.getCloseDate().equals(endOfTime));
        }

        for (int i = 0; i < NBR_OF_TRANS; i++) {
            Tran tran = buildTran(tranId++, acctMap, catMap);
            tranList.add(tran);
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
            }
        }
        for (Tran tran : tranList) {
            printInsertTran(tran);
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

    private static Stmt buildStmt(long id, Acct acct, long tranId, List<Tran> tranList) {
        Stmt stmt = new Stmt();
        stmt.setId(id);

        // Generate a random close date.
        Date closeDate = null;
        Calendar cal = Calendar.getInstance();
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
            tran.setPostDate(closeDate);
            BigDecimal amount = BigDecimal.valueOf(RANDOM.nextInt(200000) - 100000, 2);

            tran.setName(BEGINNING_BALANCE);
            // If amount is zero or positive, it is attached as a credit transaction.
            if (amount.compareTo(BigDecimal.ZERO) >= 0) {
                tran.setAmount(amount);
                tran.setCreditStmt(stmt);
                stmt.getCreditTrans().add(tran);
            } else {
                // Otherwise, it is attached as a debit transaction.
                tran.setAmount(amount.negate());
                tran.setDebitStmt(stmt);
                stmt.getDebitTrans().add(tran);
            }
            tranList.add(tran);
        }
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

    private static void updateStmt(Stmt stmt, Stmt previousStmt) {
        BigDecimal openBalance;
        if (previousStmt.getCloseBalance() != null) {
            openBalance = previousStmt.getCloseBalance();
        } else {
            openBalance = BigDecimal.ZERO;
            for (Tran tran : previousStmt.getDebitTrans()) {
                openBalance = openBalance.subtract(tran.getAmount());
            }
            for (Tran tran : previousStmt.getCreditTrans()) {
                openBalance = openBalance.add(tran.getAmount());
            }
        }
        stmt.setOpenBalance(openBalance);

        BigDecimal debits = BigDecimal.ZERO;
        for (Tran tran : stmt.getDebitTrans()) {
            debits = debits.subtract(tran.getAmount());
        }
        stmt.setDebits(debits);

        BigDecimal credits = BigDecimal.ZERO;
        for (Tran tran : stmt.getCreditTrans()) {
            credits = credits.add(tran.getAmount());
        }
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
        String creditStmt = "NULL";
        if (tran.getCreditStmt() != null) {
            String acctName = tran.getCreditStmt().getAcct().getName();
            Date stmtDate = tran.getCreditStmt().getCloseDate();
            creditStmt = String.format(selectStmtWithDateAndName, stmtDate, acctName);
        }

        String debitStmt = "NULL";
        if (tran.getDebitStmt() != null) {
            String acctName = tran.getDebitStmt().getAcct().getName();
            Date stmtDate = tran.getDebitStmt().getCloseDate();
            debitStmt = String.format(selectStmtWithDateAndName, stmtDate, acctName);
        }

        String insertIntoTran =
                "INSERT INTO `tran` (`amount`,`check_number`,`memo`,`name`,`post_date`,`version`,`cat_id`,`credit_stmt_id`,`debit_stmt_id`)" +
                        " VALUES (%.2f,%s,%s,'%s','%tF',0,%s,%s,%s);%n";
        System.out.printf(insertIntoTran, tran.getAmount(), checkNumber, memo, tran.getName(), tran.getPostDate(), cat,
                creditStmt, debitStmt);
    }
}
