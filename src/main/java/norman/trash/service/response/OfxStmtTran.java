package norman.trash.service.response;

import norman.trash.domain.CorrectAction;
import norman.trash.domain.TranType;

import java.math.BigDecimal;
import java.util.Date;

public class OfxStmtTran {
    private TranType type;
    private Date postDate;
    private Date userDate;
    private BigDecimal amount;
    private String fitId;
    private String sic;
    private String checkNumber;
    private String correctFitId;
    private CorrectAction correctAction;
    private String name;
    private String category;
    private String memo;

    public TranType getType() {
        return type;
    }

    public void setType(TranType type) {
        this.type = type;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public Date getUserDate() {
        return userDate;
    }

    public void setUserDate(Date userDate) {
        this.userDate = userDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getFitId() {
        return fitId;
    }

    public void setFitId(String fitId) {
        this.fitId = fitId;
    }

    public String getSic() {
        return sic;
    }

    public void setSic(String sic) {
        this.sic = sic;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public String getCorrectFitId() {
        return correctFitId;
    }

    public void setCorrectFitId(String correctFitId) {
        this.correctFitId = correctFitId;
    }

    public CorrectAction getCorrectAction() {
        return correctAction;
    }

    public void setCorrectAction(CorrectAction correctAction) {
        this.correctAction = correctAction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
