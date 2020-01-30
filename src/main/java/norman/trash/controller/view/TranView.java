package norman.trash.controller.view;

import norman.trash.domain.Cat;
import norman.trash.domain.Stmt;
import norman.trash.domain.Tran;

import java.math.BigDecimal;
import java.util.Date;

public class TranView {
    private Long viewingAcctId;
    private Long id;
    private String debitAcctName;
    private String creditAcctName;
    private String viewingAcctName;
    private String otherAcctName;
    private String catName;
    private Date postDate;
    private BigDecimal amount;
    private String checkNumber;
    private String name;
    private String memo;

    public TranView(Tran tran, Long viewingAcctId) {
        this.viewingAcctId = viewingAcctId;
        id = tran.getId();
        Stmt debitStmt = tran.getDebitStmt();
        if (debitStmt != null) {
            debitAcctName = debitStmt.getAcct().getName();
        }
        Stmt creditStmt = tran.getCreditStmt();
        if (creditStmt != null) {
            creditAcctName = creditStmt.getAcct().getName();
        }
        amount = tran.getAmount();
        if (viewingAcctId != null) {
            if (debitStmt != null && viewingAcctId == debitStmt.getAcct().getId()) {
                viewingAcctName = debitAcctName;
                otherAcctName = creditAcctName;
                amount = tran.getAmount().negate();
            } else if (creditStmt != null && viewingAcctId == creditStmt.getAcct().getId()) {
                viewingAcctName = creditAcctName;
                otherAcctName = debitAcctName;
            }
        }
        Cat cat = tran.getCat();
        if (cat != null) {
            catName = cat.getName();
        }
        postDate = tran.getPostDate();
        checkNumber = tran.getCheckNumber();
        name = tran.getName();
        memo = tran.getMemo();
    }

    public Long getViewingAcctId() {
        return viewingAcctId;
    }

    public Long getId() {
        return id;
    }

    public String getDebitAcctName() {
        return debitAcctName;
    }

    public String getCreditAcctName() {
        return creditAcctName;
    }

    public String getViewingAcctName() {
        return viewingAcctName;
    }

    public String getOtherAcctName() {
        return otherAcctName;
    }

    public String getCatName() {
        return catName;
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
}
