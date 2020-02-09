package norman.trash.controller.view;

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
    private String number;
    private Date effDate;
    private BigDecimal balance;
    private Date lastTranDate;
    private List<AcctNbr> oldAcctNbrs = new ArrayList<>();
    private Long currentStmtId;

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
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.YEAR, 9999);
        Date endOfTime = cal.getTime();

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

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AcctType getType() {
        return type;
    }

    public void setType(AcctType type) {
        this.type = type;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getEffDate() {
        return effDate;
    }

    public void setEffDate(Date effDate) {
        this.effDate = effDate;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Date getLastTranDate() {
        return lastTranDate;
    }

    public void setLastTranDate(Date lastTranDate) {
        this.lastTranDate = lastTranDate;
    }

    public List<AcctNbr> getOldAcctNbrs() {
        return oldAcctNbrs;
    }

    public void setOldAcctNbrs(List<AcctNbr> oldAcctNbrs) {
        this.oldAcctNbrs = oldAcctNbrs;
    }

    public Long getCurrentStmtId() {
        return currentStmtId;
    }

    public void setCurrentStmtId(Long currentStmtId) {
        this.currentStmtId = currentStmtId;
    }
}
