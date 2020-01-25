package norman.trash;

import norman.trash.domain.Acct;
import norman.trash.domain.AcctType;
import norman.trash.domain.Tran;
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
    private static final int NBR_OF_ACCTS = 12;
    private static final String INSERT_INTO_ACCT =
            "INSERT INTO `acct` (`name`,`type`,`version`) VALUES ('%s','%s',0);%n";
    private static final int NBR_OF_TRANS = 1234;
    private static final String INSERT_INTO_TRAN =
            "INSERT INTO `tran` (`amount`,`post_date`,`version`,`debit_acct_id`,`credit_acct_id`) VALUES (%.2f,'%tF',0,%s,%s);%n";
    private static final String SELECT_ACCT_ID = "(SELECT `id` FROM `acct` WHERE `name` = '%s')";

    private FakeDataUtil() {
    }

    public static void main(String[] args) {
        LOGGER.debug("Starting FakeDataUtil");
        long acctId = 1;
        long tranId = 1;
        Map<String, Acct> uniqueAccts = new HashMap<>();

        for (int i = 0; i < NBR_OF_ACCTS; i++) {
            Acct acct = buildAcct(acctId++, uniqueAccts);
            System.out.printf(INSERT_INTO_ACCT, acct.getName(), acct.getType());
        }

        for (int i = 0; i < NBR_OF_TRANS; i++) {
            Tran tran = buildTran(tranId, uniqueAccts);
            String debitAcct = "null";
            if (tran.getDebitAcct() != null) {
                debitAcct = String.format(SELECT_ACCT_ID, tran.getDebitAcct().getName());
            }
            String creditAcct = "null";
            if (tran.getCreditAcct() != null) {
                creditAcct = String.format(SELECT_ACCT_ID, tran.getCreditAcct().getName());
            }
            System.out.printf(INSERT_INTO_TRAN, tran.getAmount(), tran.getPostDate(), debitAcct, creditAcct);
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

    private static Tran buildTran(long id, Map<String, Acct> uniqueAccts) {
        List<String> uniqueNames = new ArrayList<>(uniqueAccts.keySet());
        String debitAcctName = null;
        String creditAcctName = null;
        int foo = RANDOM.nextInt(3);
        if (foo == 0) {
            debitAcctName = uniqueNames.get(RANDOM.nextInt(uniqueNames.size()));
        } else if (foo == 1) {
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
        cal.add(Calendar.DATE, -1 * RANDOM.nextInt(265));
        Date postDate = cal.getTime();
        BigDecimal amount = BigDecimal.valueOf(RANDOM.nextInt(100000), 2);

        Tran tran = new Tran();
        tran.setId(id);
        if (debitAcctName == null) {
            tran.setDebitAcct(null);
        } else {
            tran.setDebitAcct(uniqueAccts.get(debitAcctName));
        }
        if (creditAcctName == null) {
            tran.setCreditAcct(null);
        } else {
            tran.setCreditAcct(uniqueAccts.get(creditAcctName));
        }
        tran.setPostDate(postDate);
        tran.setAmount(amount);
        return tran;
    }
}
