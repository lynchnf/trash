package norman.trash.controller.view;

import norman.trash.domain.Cat;
import norman.trash.domain.DataTran;
import norman.trash.domain.Stmt;
import norman.trash.domain.Tran;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

public class DataTranMatchTarget {
    @DateTimeFormat(pattern = "M/d/yyyy")
    private Date ofxPostDate;
    private BigDecimal ofxAmount;
    private String ofxFitId;
    private String ofxCheckNumber;
    private String ofxName;
    private String ofxMemo;
    private Long tranId;
    private Integer tranVersion = 0;
    @DateTimeFormat(pattern = "M/d/yyyy")
    private Date postDate;
    private BigDecimal amount;
    private String checkNumber;
    private String name;
    private String memo;
    private Long stmtId;
    private Long catId;

    public DataTranMatchTarget() {
    }

    public DataTranMatchTarget(DataTran dataTran) {
        ofxPostDate = dataTran.getOfxPostDate();
        postDate = dataTran.getOfxPostDate();
        ofxAmount = dataTran.getOfxAmount();
        amount = dataTran.getOfxAmount();
        ofxFitId = dataTran.getOfxFitId();
        ofxCheckNumber = dataTran.getOfxCheckNumber();
        checkNumber = dataTran.getOfxCheckNumber();
        ofxName = dataTran.getOfxName();
        name = dataTran.getOfxName();
        ofxMemo = dataTran.getOfxMemo();
        memo = dataTran.getOfxMemo();
    }

    public Tran toTran() {
        Tran tran = new Tran();
        tran.setId(tranId);
        tran.setVersion(tranVersion);
        tran.setPostDate(postDate);
        tran.setAmount(amount);
        tran.setCheckNumber(StringUtils.trimToNull(checkNumber));
        tran.setName(StringUtils.trimToNull(name));
        tran.setMemo(StringUtils.trimToNull(memo));
        tran.setOfxFitId(StringUtils.trimToNull(ofxFitId));
        tran.setStmt(new Stmt());
        tran.getStmt().setId(stmtId);
        if (catId != null) {
            tran.setCat(new Cat());
            tran.getCat().setId(catId);
        }
        return tran;
    }

    public Date getOfxPostDate() {
        return ofxPostDate;
    }

    public void setOfxPostDate(Date ofxPostDate) {
        this.ofxPostDate = ofxPostDate;
    }

    public BigDecimal getOfxAmount() {
        return ofxAmount;
    }

    public void setOfxAmount(BigDecimal ofxAmount) {
        this.ofxAmount = ofxAmount;
    }

    public String getOfxFitId() {
        return ofxFitId;
    }

    public void setOfxFitId(String ofxFitId) {
        this.ofxFitId = ofxFitId;
    }

    public String getOfxCheckNumber() {
        return ofxCheckNumber;
    }

    public void setOfxCheckNumber(String ofxCheckNumber) {
        this.ofxCheckNumber = ofxCheckNumber;
    }

    public String getOfxName() {
        return ofxName;
    }

    public void setOfxName(String ofxName) {
        this.ofxName = ofxName;
    }

    public String getOfxMemo() {
        return ofxMemo;
    }

    public void setOfxMemo(String ofxMemo) {
        this.ofxMemo = ofxMemo;
    }

    public Long getTranId() {
        return tranId;
    }

    public void setTranId(Long tranId) {
        this.tranId = tranId;
    }

    public Integer getTranVersion() {
        return tranVersion;
    }

    public void setTranVersion(Integer tranVersion) {
        this.tranVersion = tranVersion;
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

    public Long getStmtId() {
        return stmtId;
    }

    public void setStmtId(Long stmtId) {
        this.stmtId = stmtId;
    }

    public Long getCatId() {
        return catId;
    }

    public void setCatId(Long catId) {
        this.catId = catId;
    }
}
