package norman.trash.controller.view;

import norman.trash.domain.CorrectAction;
import norman.trash.domain.DataTran;
import norman.trash.domain.Tran;
import norman.trash.domain.TranType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataTranMatchRow {
    private Long id;
    private Integer version = 0;
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
    private Long dataFileId;
    private List<TranView> options = new ArrayList<>();

    public DataTranMatchRow() {
    }

    public DataTranMatchRow(DataTran dataTran, List<Tran> trans) {
        id = dataTran.getId();
        version = dataTran.getVersion();
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
        dataFileId = dataTran.getDataFile().getId();
        for (Tran tran : trans) {
            TranView option = new TranView(tran);
            options.add(option);
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

    public TranType getOfxType() {
        return ofxType;
    }

    public void setOfxType(TranType ofxType) {
        this.ofxType = ofxType;
    }

    public Date getOfxPostDate() {
        return ofxPostDate;
    }

    public void setOfxPostDate(Date ofxPostDate) {
        this.ofxPostDate = ofxPostDate;
    }

    public Date getOfxUserDate() {
        return ofxUserDate;
    }

    public void setOfxUserDate(Date ofxUserDate) {
        this.ofxUserDate = ofxUserDate;
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

    public String getOfxSic() {
        return ofxSic;
    }

    public void setOfxSic(String ofxSic) {
        this.ofxSic = ofxSic;
    }

    public String getOfxCheckNumber() {
        return ofxCheckNumber;
    }

    public void setOfxCheckNumber(String ofxCheckNumber) {
        this.ofxCheckNumber = ofxCheckNumber;
    }

    public String getOfxCorrectFitId() {
        return ofxCorrectFitId;
    }

    public void setOfxCorrectFitId(String ofxCorrectFitId) {
        this.ofxCorrectFitId = ofxCorrectFitId;
    }

    public CorrectAction getOfxCorrectAction() {
        return ofxCorrectAction;
    }

    public void setOfxCorrectAction(CorrectAction ofxCorrectAction) {
        this.ofxCorrectAction = ofxCorrectAction;
    }

    public String getOfxName() {
        return ofxName;
    }

    public void setOfxName(String ofxName) {
        this.ofxName = ofxName;
    }

    public String getOfxCategory() {
        return ofxCategory;
    }

    public void setOfxCategory(String ofxCategory) {
        this.ofxCategory = ofxCategory;
    }

    public String getOfxMemo() {
        return ofxMemo;
    }

    public void setOfxMemo(String ofxMemo) {
        this.ofxMemo = ofxMemo;
    }

    public Long getDataFileId() {
        return dataFileId;
    }

    public void setDataFileId(Long dataFileId) {
        this.dataFileId = dataFileId;
    }

    public List<TranView> getOptions() {
        return options;
    }

    public void setOptions(List<TranView> options) {
        this.options = options;
    }
}
