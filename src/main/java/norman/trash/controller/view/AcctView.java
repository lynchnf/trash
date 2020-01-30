package norman.trash.controller.view;

import norman.trash.domain.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
    private String number;
    private Date effDate;
    private BigDecimal balance;
    private Date lastTranDate;

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

        // Get the account number and effective date from the latest acctNbr.
        List<AcctNbr> acctNbrs = acct.getAcctNbrs();
        acctNbrs.sort((acctNbr1, acctNbr2) -> acctNbr2.getEffDate().compareTo(acctNbr1.getEffDate()));
        AcctNbr acctNbr = acctNbrs.iterator().next();
        number = acctNbr.getNumber();
        effDate = acctNbr.getEffDate();

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

    public String getNumber() {
        return number;
    }

    public Date getEffDate() {
        return effDate;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Date getLastTranDate() {
        return lastTranDate;
    }
}
