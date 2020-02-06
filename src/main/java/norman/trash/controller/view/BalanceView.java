package norman.trash.controller.view;

import norman.trash.domain.Cat;
import norman.trash.domain.Stmt;
import norman.trash.domain.Tran;

import java.math.BigDecimal;
import java.util.Date;

public class BalanceView {
    private BalanceType type;
    private Long id;
    private Date postDate;
    private String checkNumber;
    private String name;
    private String memo;
    private String catName;
    private BigDecimal amount;
    private BigDecimal balance;

    public BalanceView(Stmt stmt) {
        type = BalanceType.STMT;
        id = stmt.getId();
        postDate = stmt.getCloseDate();
        amount = stmt.getCloseBalance();
    }

    public BalanceView(Tran tran, BalanceType type) {
        this.type = type;
        id = tran.getId();
        postDate = tran.getPostDate();
        checkNumber = tran.getCheckNumber();
        name = tran.getName();
        memo = tran.getMemo();
        Cat cat = tran.getCat();
        if (cat != null) {
            catName = cat.getName();
        }
        if (type == BalanceType.DEBIT_TRAN) {
            amount = tran.getAmount().negate();
        } else {
            amount = tran.getAmount();
        }
    }

    public BalanceType getType() {
        return type;
    }

    public Long getId() {
        return id;
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
