package norman.trash.controller.view;

import norman.trash.domain.Cat;
import norman.trash.domain.Tran;

import java.math.BigDecimal;
import java.util.Date;

public class TranView {
    private Long id;
    private Date postDate;
    private BigDecimal amount;
    private String checkNumber;
    private String name;
    private String memo;
    private String ofxFitId;
    private BigDecimal balance = BigDecimal.ZERO;
    private Long acctId;
    private String acctName;
    private Long stmtId;
    private Date stmtCloseDate;
    private Long catId;
    private String catName;

    public TranView(Tran tran) {
        id = tran.getId();
        postDate = tran.getPostDate();
        amount = tran.getAmount();
        checkNumber = tran.getCheckNumber();
        name = tran.getName();
        memo = tran.getMemo();
        ofxFitId = tran.getOfxFitId();
        acctId = tran.getStmt().getAcct().getId();
        acctName = tran.getStmt().getAcct().getName();
        stmtId = tran.getStmt().getId();
        stmtCloseDate = tran.getStmt().getCloseDate();
        Cat cat = tran.getCat();
        if (cat != null) {
            catId = cat.getId();
            catName = cat.getName();
        }
    }

    public Long getId() {
        return id;
    }

    public Date getPostDate() {
        return postDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public String getName() {
        return name;
    }

    public String getMemo() {
        return memo;
    }

    public String getOfxFitId() {
        return ofxFitId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Long getAcctId() {
        return acctId;
    }

    public String getAcctName() {
        return acctName;
    }

    public Long getStmtId() {
        return stmtId;
    }

    public Date getStmtCloseDate() {
        return stmtCloseDate;
    }

    public Long getCatId() {
        return catId;
    }

    public String getCatName() {
        return catName;
    }
}
