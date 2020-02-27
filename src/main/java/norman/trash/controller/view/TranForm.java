package norman.trash.controller.view;

import norman.trash.domain.Acct;
import norman.trash.domain.Cat;
import norman.trash.domain.Stmt;
import norman.trash.domain.Tran;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;

public class TranForm {
    private Long id;
    private Integer version = 0;
    @NotNull(message = "Post Date may not be blank.")
    @DateTimeFormat(pattern = "M/d/yyyy")
    private Date postDate;
    @NotNull(message = "Amount may not be blank.")
    @Digits(integer = 7, fraction = 2,
            message = "Amount value out of bounds. (<{integer} digits>.<{fraction} digits> expected)")
    private BigDecimal amount;
    @Size(max = 10, message = "Check Number may not be over {max} characters long.")
    private String checkNumber;
    @NotBlank(message = "Name may not be blank.")
    @Size(max = 100, message = "Name may not be over {max} characters long.")
    private String name;
    @Size(max = 100, message = "Memo may not be over {max} characters long.")
    private String memo;
    private String ofxFitId;
    @NotNull(message = "Account may not be blank.")
    private Long acctId;
    private Long catId;

    public TranForm() {
    }

    public TranForm(Tran tran) {
        id = tran.getId();
        version = tran.getVersion();
        postDate = tran.getPostDate();
        amount = tran.getAmount();
        checkNumber = tran.getCheckNumber();
        name = tran.getName();
        memo = tran.getMemo();
        ofxFitId = tran.getOfxFitId();
        acctId = tran.getStmt().getAcct().getId();
        Cat cat = tran.getCat();
        if (cat != null) {
            catId = cat.getId();
        }
    }

    public Tran toTran() {
        Tran tran = new Tran();
        tran.setId(id);
        tran.setVersion(version);
        tran.setPostDate(postDate);
        tran.setAmount(amount);
        tran.setName(StringUtils.trimToNull(name));
        tran.setMemo(StringUtils.trimToNull(memo));
        tran.setOfxFitId(StringUtils.trimToNull(ofxFitId));
        tran.setStmt(new Stmt());
        tran.getStmt().setAcct(new Acct());
        tran.getStmt().getAcct().setId(acctId);
        if (catId != null) {
            tran.setCat(new Cat());
            tran.getCat().setId(catId);
        }
        return tran;
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

    public String getOfxFitId() {
        return ofxFitId;
    }

    public void setOfxFitId(String ofxFitId) {
        this.ofxFitId = ofxFitId;
    }

    public Long getAcctId() {
        return acctId;
    }

    public void setAcctId(Long acctId) {
        this.acctId = acctId;
    }

    public Long getCatId() {
        return catId;
    }

    public void setCatId(Long catId) {
        this.catId = catId;
    }
}
