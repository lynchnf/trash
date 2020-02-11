package norman.trash.controller.view;

import norman.trash.domain.Cat;
import norman.trash.domain.Stmt;
import norman.trash.domain.Tran;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

public class StmtReconcileRow {
    private BalanceType type;
    private boolean selected;
    private Long id;
    private Integer version = 0;
    private Long debitStmtId;
    private Long creditStmtId;
    private Long catId;
    @DateTimeFormat(pattern = "M/d/yyyy")
    private Date postDate;
    private BigDecimal amount;
    private String checkNumber;
    private String name;
    private String memo;

    public StmtReconcileRow() {
    }

    public StmtReconcileRow(Tran tran, BalanceType type) {
        this.type = type;
        selected = false;
        id = tran.getId();
        version = tran.getVersion();
        Stmt debitStmt = tran.getDebitStmt();
        if (debitStmt != null) {
            debitStmtId = debitStmt.getId();
        }
        Stmt creditStmt = tran.getCreditStmt();
        if (creditStmt != null) {
            creditStmtId = creditStmt.getId();
        }
        Cat cat = tran.getCat();
        if (cat != null) {
            catId = cat.getId();
        }
        postDate = tran.getPostDate();
        if (type == BalanceType.DEBIT_TRAN) {
            amount = tran.getAmount().negate();
        } else {
            amount = tran.getAmount();
        }
        checkNumber = tran.getCheckNumber();
        name = tran.getName();
        memo = tran.getMemo();
    }

    public Tran toTran() {
        Tran tran = new Tran();
        tran.setId(id);
        tran.setVersion(version);
        if (debitStmtId != null) {
            tran.setDebitStmt(new Stmt());
            tran.getDebitStmt().setId(debitStmtId);
        }
        if (creditStmtId != null) {
            tran.setCreditStmt(new Stmt());
            tran.getCreditStmt().setId(creditStmtId);
        }
        if (catId != null) {
            tran.setCat(new Cat());
            tran.getCat().setId(catId);
        }
        tran.setPostDate(postDate);
        tran.setAmount(amount);
        tran.setCheckNumber(checkNumber);
        tran.setName(name);
        tran.setMemo(memo);
        return tran;
    }

    public BalanceType getType() {
        return type;
    }

    public void setType(BalanceType type) {
        this.type = type;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Long getDebitStmtId() {
        return debitStmtId;
    }

    public void setDebitStmtId(Long debitStmtId) {
        this.debitStmtId = debitStmtId;
    }

    public Long getCreditStmtId() {
        return creditStmtId;
    }

    public void setCreditStmtId(Long creditStmtId) {
        this.creditStmtId = creditStmtId;
    }

    public Long getCatId() {
        return catId;
    }

    public void setCatId(Long catId) {
        this.catId = catId;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
