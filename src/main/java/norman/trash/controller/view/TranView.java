package norman.trash.controller.view;

import norman.trash.domain.Cat;
import norman.trash.domain.Tran;

import java.math.BigDecimal;
import java.util.Date;

import static norman.trash.controller.view.TranStatus.*;

public class TranView {
    private Long id;
    private TranStatus status;
    private Date postDate;
    private Date manualPostDate;
    private Date uploadedPostDate;
    private BigDecimal amount;
    private BigDecimal manualAmount;
    private BigDecimal uploadedAmount;
    private String checkNumber;
    private String manualCheckNumber;
    private String uploadedCheckNumber;
    private String name;
    private String manualName;
    private String uploadedName;
    private String memo;
    private String manualMemo;
    private String uploadedMemo;
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
        if (tran.getManualPostDate() == null && tran.getUploadedPostDate() != null) {
            status = UPLOADED;
        } else if (tran.getManualPostDate() != null && tran.getUploadedPostDate() == null) {
            status = MANUAL;
        } else if (tran.getManualPostDate() != null && tran.getUploadedPostDate() != null) {
            status = BOTH;
        }
        postDate = tran.getPostDate();
        manualPostDate = tran.getManualPostDate();
        uploadedPostDate = tran.getUploadedPostDate();
        amount = tran.getAmount();
        manualAmount = tran.getManualAmount();
        uploadedAmount = tran.getUploadedAmount();
        checkNumber = tran.getCheckNumber();
        manualCheckNumber = tran.getManualCheckNumber();
        uploadedCheckNumber = tran.getUploadedCheckNumber();
        name = tran.getName();
        manualName = tran.getManualName();
        uploadedName = tran.getUploadedName();
        memo = tran.getMemo();
        manualMemo = tran.getManualMemo();
        uploadedMemo = tran.getUploadedMemo();
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

    public TranStatus getStatus() {
        return status;
    }

    public Date getPostDate() {
        return postDate;
    }

    public Date getManualPostDate() {
        return manualPostDate;
    }

    public Date getUploadedPostDate() {
        return uploadedPostDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getManualAmount() {
        return manualAmount;
    }

    public BigDecimal getUploadedAmount() {
        return uploadedAmount;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public String getManualCheckNumber() {
        return manualCheckNumber;
    }

    public String getUploadedCheckNumber() {
        return uploadedCheckNumber;
    }

    public String getName() {
        return name;
    }

    public String getManualName() {
        return manualName;
    }

    public String getUploadedName() {
        return uploadedName;
    }

    public String getMemo() {
        return memo;
    }

    public String getManualMemo() {
        return manualMemo;
    }

    public String getUploadedMemo() {
        return uploadedMemo;
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
