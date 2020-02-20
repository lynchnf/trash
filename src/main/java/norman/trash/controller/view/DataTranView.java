package norman.trash.controller.view;

import norman.trash.domain.CorrectAction;
import norman.trash.domain.DataTran;
import norman.trash.domain.TranType;

import java.math.BigDecimal;
import java.util.Date;

public class DataTranView {
    private Long id;
    private TranType ofxType;
    private Date ofxPostDate;
    private Date ofxUserDate;
    private BigDecimal ofxAmount;
    private String ofxFitId;
    private String ofxSic;
    private String ofxCheckNumber;
    private String ofxCorrectFitId;
    private CorrectAction ofxCorrectAction;
    private String ofxName;
    private String ofxCategory;
    private String ofxMemo;

    public DataTranView(DataTran dataTran) {
        id = dataTran.getId();
        ofxType = dataTran.getOfxType();
        ofxPostDate = dataTran.getOfxPostDate();
        ofxUserDate = dataTran.getOfxUserDate();
        ofxAmount = dataTran.getOfxAmount();
        ofxFitId = dataTran.getOfxFitId();
        ofxSic = dataTran.getOfxSic();
        ofxCheckNumber = dataTran.getOfxCheckNumber();
        ofxCorrectFitId = dataTran.getOfxCorrectFitId();
        ofxCorrectAction = dataTran.getOfxCorrectAction();
        ofxName = dataTran.getOfxName();
        ofxCategory = dataTran.getOfxCategory();
        ofxMemo = dataTran.getOfxMemo();
    }

    public Long getId() {
        return id;
    }

    public TranType getOfxType() {
        return ofxType;
    }

    public Date getOfxPostDate() {
        return ofxPostDate;
    }

    public Date getOfxUserDate() {
        return ofxUserDate;
    }

    public BigDecimal getOfxAmount() {
        return ofxAmount;
    }

    public String getOfxFitId() {
        return ofxFitId;
    }

    public String getOfxSic() {
        return ofxSic;
    }

    public String getOfxCheckNumber() {
        return ofxCheckNumber;
    }

    public String getOfxCorrectFitId() {
        return ofxCorrectFitId;
    }

    public CorrectAction getOfxCorrectAction() {
        return ofxCorrectAction;
    }

    public String getOfxName() {
        return ofxName;
    }

    public String getOfxCategory() {
        return ofxCategory;
    }

    public String getOfxMemo() {
        return ofxMemo;
    }
}
