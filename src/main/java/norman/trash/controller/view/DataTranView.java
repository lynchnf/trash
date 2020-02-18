package norman.trash.controller.view;

import norman.trash.domain.CorrectAction;
import norman.trash.domain.DataTran;
import norman.trash.domain.TranType;

import java.math.BigDecimal;
import java.util.Date;

public class DataTranView {
    private Long id;
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

    public DataTranView(DataTran dataTran) {
        id = dataTran.getId();
        type = dataTran.getType();
        postDate = dataTran.getPostDate();
        userDate = dataTran.getUserDate();
        amount = dataTran.getAmount();
        fitId = dataTran.getFitId();
        sic = dataTran.getSic();
        checkNumber = dataTran.getCheckNumber();
        correctFitId = dataTran.getCorrectFitId();
        correctAction = dataTran.getCorrectAction();
        name = dataTran.getName();
        category = dataTran.getCategory();
        memo = dataTran.getMemo();
    }

    public Long getId() {
        return id;
    }

    public TranType getType() {
        return type;
    }

    public Date getPostDate() {
        return postDate;
    }

    public Date getUserDate() {
        return userDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getFitId() {
        return fitId;
    }

    public String getSic() {
        return sic;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public String getCorrectFitId() {
        return correctFitId;
    }

    public CorrectAction getCorrectAction() {
        return correctAction;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getMemo() {
        return memo;
    }
}
