package norman.trash.controller.view;

import norman.trash.TrashUtils;
import norman.trash.domain.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class AcctView {
    // Acct
    private Long id;
    private String name;
    private AcctType type;
    private String addressName;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String zipCode;
    private String phoneNumber;
    private BigDecimal creditLimit;
    private String ofxOrganization;
    private String ofxFid;
    private String ofxBankId;
    // AcctNbr
    private String number;
    private Date effDate;
    private List<AcctNbr> oldAcctNbrs = new ArrayList<>();
    // Stmt
    private Long currentStmtId;
    // Tran
    private Date lastTranDate;
    private BigDecimal balance;

    public AcctView(Acct acct) {
        id = acct.getId();
        name = acct.getName();
        type = acct.getType();
        addressName = acct.getAddressName();
        address1 = acct.getAddress1();
        address2 = acct.getAddress2();
        city = acct.getCity();
        state = acct.getState();
        zipCode = acct.getZipCode();
        phoneNumber = acct.getPhoneNumber();
        creditLimit = acct.getCreditLimit();
        ofxOrganization = acct.getOfxOrganization();
        ofxFid = acct.getOfxFid();
        ofxBankId = acct.getOfxBankId();

        // Get the account number and effective date from the latest acctNbr.
        List<AcctNbr> acctNbrs = acct.getAcctNbrs();
        Comparator<AcctNbr> comparator = new Comparator<AcctNbr>() {
            @Override
            public int compare(AcctNbr acctNbr1, AcctNbr acctNbr2) {
                return acctNbr2.getEffDate().compareTo(acctNbr1.getEffDate());
            }
        };
        acctNbrs.sort(comparator);
        if (acctNbrs.size() >= 1) {
            number = acctNbrs.get(0).getNumber();
            effDate = acctNbrs.get(0).getEffDate();
        }

        // Get older account numbers, if there are any.
        if (acctNbrs.size() > 1) {
            oldAcctNbrs.addAll(acctNbrs.subList(1, acctNbrs.size()));
        }

        balance = BigDecimal.ZERO;
        lastTranDate = null;
        for (Stmt stmt : acct.getStmts()) {
            for (Tran tran : stmt.getDebitTrans()) {
                balance = balance.subtract(tran.getAmount());
                if (lastTranDate == null || lastTranDate.before(tran.getPostDate())) {
                    lastTranDate = tran.getPostDate();
                }
            }
            for (Tran tran : stmt.getCreditTrans()) {
                balance = balance.add(tran.getAmount());
                if (lastTranDate == null || lastTranDate.before(tran.getPostDate())) {
                    lastTranDate = tran.getPostDate();
                }
            }
        }

        // Get id for the current statement.
        Date endOfTime = TrashUtils.getEndOfTime();
        List<Stmt> stmts = acct.getStmts();
        Stmt stmt = null;
        for (int i = 0; i < stmts.size() && (stmt == null || !stmt.getCloseDate().equals(endOfTime)); i++) {
            stmt = stmts.get(i);
        }
        currentStmtId = stmt.getId();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public AcctType getType() {
        return type;
    }

    public String getAddressName() {
        return addressName;
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public String getOfxOrganization() {
        return ofxOrganization;
    }

    public String getOfxFid() {
        return ofxFid;
    }

    public String getOfxBankId() {
        return ofxBankId;
    }

    public String getNumber() {
        return number;
    }

    public Date getEffDate() {
        return effDate;
    }

    public List<AcctNbr> getOldAcctNbrs() {
        return oldAcctNbrs;
    }

    public Long getCurrentStmtId() {
        return currentStmtId;
    }

    public Date getLastTranDate() {
        return lastTranDate;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
