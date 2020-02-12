package norman.trash.controller.view;

import norman.trash.domain.Cat;
import norman.trash.domain.Tran;

import java.math.BigDecimal;
import java.util.Date;

public class BalanceView {
    private BalanceType type;
    private Long id;
    private Long stmtId;
    private Date stmtCloseDate;
    private Date postDate;
    private String checkNumber;
    private String name;
    private String memo;
    private Long catId;
    private String catName;
    private BigDecimal amount;
    private BigDecimal balance;

    public BalanceView(Tran tran, BalanceType type) {
        this.type = type;
        id = tran.getId();
        postDate = tran.getPostDate();
        checkNumber = tran.getCheckNumber();
        name = tran.getName();
        memo = tran.getMemo();
        if (type == BalanceType.DEBIT_TRAN) {
            stmtId = tran.getDebitStmt().getId();
            stmtCloseDate = tran.getDebitStmt().getCloseDate();
            amount = tran.getAmount().negate();
        } else {
            stmtId = tran.getCreditStmt().getId();
            stmtCloseDate = tran.getCreditStmt().getCloseDate();
            amount = tran.getAmount();
        }
        Cat cat = tran.getCat();
        if (cat != null) {
            catId = cat.getId();
            catName = cat.getName();
        }
    }

    public BalanceType getType() {
        return type;
    }

    public Long getId() {
        return id;
    }

    public Long getStmtId() {
        return stmtId;
    }

    public Date getStmtCloseDate() {
        return stmtCloseDate;
    }

    public Date getPostDate() {
        return postDate;
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

    public Long getCatId() {
        return catId;
    }

    public String getCatName() {
        return catName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
