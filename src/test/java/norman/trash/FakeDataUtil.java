package norman.trash;

import norman.trash.domain.Acct;
import norman.trash.domain.AcctType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

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
    private static final int NBR_OF_ACCTS = 123;
    private static final String INSERT_INTO_ACCT =
            "INSERT INTO `acct` (`name`, `type`, `version`) VALUES ('%s','%s',0);%n";

    private FakeDataUtil() {
    }

    public static void main(String[] args) {
        LOGGER.debug("Starting FakeDataUtil");
        long acctId = 1;
        Set<String> uniqueAcctNames = new HashSet<>();

        for (int i = 0; i < NBR_OF_ACCTS; i++) {
            Acct acct = buildAcct(acctId++, uniqueAcctNames);
            System.out.printf(INSERT_INTO_ACCT, acct.getName(), acct.getType());
        }
    }

    private static Acct buildAcct(long id, Set<String> uniqueNames) {
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
        } while (uniqueNames.contains(name));
        uniqueNames.add(name);
        Acct acct = new Acct();
        acct.setId(id);
        acct.setName(name);
        acct.setType(acctType);
        return acct;
    }
}
