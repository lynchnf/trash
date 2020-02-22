package norman.trash.controller.view;

import norman.trash.domain.Cat;
import norman.trash.domain.Tran;

import java.math.BigDecimal;
import java.util.Date;

public class BalanceView {
    // Tran
    private Long id;
    private Date postDate;
    private BigDecimal amount;
    private String checkNumber;
    private String name;
    private String memo;
    private BigDecimal balance;
    private BalanceType type;
    // Stmt
    private Long stmtId;
    private Date stmtCloseDate;
    // Cat
    private Long catId;
    private String catName;

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

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BalanceType getType() {
        return type;
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
