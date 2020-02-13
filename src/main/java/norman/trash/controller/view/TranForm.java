package norman.trash.controller.view;

import norman.trash.controller.view.validation.NotBothNull;
import norman.trash.domain.Acct;
import norman.trash.domain.Cat;
import norman.trash.domain.Stmt;
import norman.trash.domain.Tran;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Version;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Date;

@NotBothNull(fieldName1 = "debitAcctId", fieldName2 = "creditAcctId",
        message = "Must select a Debit or Credit Account (or both).")
public class TranForm {
    private Long id;
    @Version
    private Integer version = 0;
    private Long debitAcctId;
    private Long creditAcctId;
    private Long catId;
    @NotNull(message = "Post Date may not be blank.")
    @DateTimeFormat(pattern = "M/d/yyyy")
    private Date postDate;
    @NotNull(message = "Amount may not be blank.")
    @Digits(integer = 7, fraction = 2,
            message = "Amount value out of bounds. (<{integer} digits>.<{fraction} digits> expected)")
    @PositiveOrZero(message = "Amount may not be negative.")
    private BigDecimal amount;
    @Size(max = 10, message = "Check Number may not be over {max} characters long.")
    private String checkNumber;
    @NotBlank(message = "Name may not be blank.")
    @Size(max = 100, message = "Name may not be over {max} characters long.")
    private String name;
    @Size(max = 100, message = "Memo may not be over {max} characters long.")
    private String memo;

    public TranForm() {
    }

    public TranForm(Tran tran) {
        id = tran.getId();
        version = tran.getVersion();
        Stmt debitStmt = tran.getDebitStmt();
        if (debitStmt != null) {
            debitAcctId = debitStmt.getAcct().getId();
        }
        Stmt creditStmt = tran.getCreditStmt();
        if (creditStmt != null) {
            creditAcctId = creditStmt.getAcct().getId();
        }
        Cat cat = tran.getCat();
        if (cat != null) {
            catId = cat.getId();
        }
        postDate = tran.getPostDate();
        amount = tran.getAmount();
        checkNumber = tran.getCheckNumber();
        name = tran.getName();
        memo = tran.getMemo();
    }

    public Tran toTran() {
        Tran tran = new Tran();
        tran.setId(id);
        tran.setVersion(version);
        if (debitAcctId != null) {
            tran.setDebitStmt(new Stmt());
            tran.getDebitStmt().setAcct(new Acct());
            tran.getDebitStmt().getAcct().setId(debitAcctId);
        }
        if (creditAcctId != null) {
            tran.setCreditStmt(new Stmt());
            tran.getCreditStmt().setAcct(new Acct());
            tran.getCreditStmt().getAcct().setId(creditAcctId);
        }
        if (catId != null) {
            tran.setCat(new Cat());
            tran.getCat().setId(catId);
        }
        tran.setPostDate(postDate);
        tran.setAmount(amount);
        tran.setName(StringUtils.trimToNull(name));
        tran.setMemo(StringUtils.trimToNull(memo));
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

    public Long getDebitAcctId() {
        return debitAcctId;
    }

    public void setDebitAcctId(Long debitAcctId) {
        this.debitAcctId = debitAcctId;
    }

    public Long getCreditAcctId() {
        return creditAcctId;
    }

    public void setCreditAcctId(Long creditAcctId) {
        this.creditAcctId = creditAcctId;
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
