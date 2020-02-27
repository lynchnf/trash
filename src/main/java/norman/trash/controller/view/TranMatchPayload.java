package norman.trash.controller.view;

import norman.trash.domain.Cat;
import norman.trash.domain.Tran;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

public class TranMatchPayload {
    private Long id;
    private Integer version = 0;
    @DateTimeFormat(pattern = "M/d/yyyy")
    private Date postDate;
    private BigDecimal amount;
    private String checkNumber;
    private String name;
    private String memo;
    private Long stmtId;
    private Long catId;

    public TranMatchPayload() {
    }

    public TranMatchPayload(Tran tran) {
        id = tran.getId();
        version = tran.getVersion();
        postDate = tran.getPostDate();
        amount = tran.getAmount();
        checkNumber = tran.getCheckNumber();
        name = tran.getName();
        memo = tran.getMemo();
        stmtId = tran.getStmt().getId();
        Cat cat = tran.getCat();
        if (cat != null) {
            catId = cat.getId();
        }
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
