package norman.trash.controller.view;

import norman.trash.TrashUtils;
import norman.trash.domain.*;

import java.math.BigDecimal;
import java.util.*;

public class AcctView {
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
    //
    private String number;
    private Date effDate;
    private List<AcctNbrView> oldAcctNbrs = new ArrayList<>();
    //
    private Long currentStmtId;
    //
    private Date lastTranDate;
    private BigDecimal balance = BigDecimal.ZERO;

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

        // Get current account number and effective date.
        List<AcctNbr> acctNbrs = acct.getAcctNbrs();
        acctNbrs.sort(Comparator.comparing(AcctNbr::getEffDate));
        Collections.reverse(acctNbrs);
        if (acctNbrs.size() >= 1) {
            number = acctNbrs.get(0).getNumber();
            effDate = acctNbrs.get(0).getEffDate();
        }

        // Get older account numbers, if there are any.
        if (acctNbrs.size() > 1) {
            for (int i = 1; i < acctNbrs.size(); i++) {
                AcctNbr acctNbr = acctNbrs.get(i);
                AcctNbrView acctNbrView = new AcctNbrView(acctNbr);
                oldAcctNbrs.add(acctNbrView);
            }
        }

        // Get id for the current statement and last transaction date and balance.
        Date endOfTime = TrashUtils.getEndOfTime();
        for (Stmt stmt : acct.getStmts()) {
            if (stmt.getCloseDate().equals(endOfTime)) {
                currentStmtId = stmt.getId();
            }
            for (Tran tran : stmt.getTrans()) {
                if (lastTranDate == null || lastTranDate.before(tran.getPostDate())) {
                    lastTranDate = tran.getPostDate();
                }
                balance = balance.add(tran.getAmount());
            }
        }
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

    public List<AcctNbrView> getOldAcctNbrs() {
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
