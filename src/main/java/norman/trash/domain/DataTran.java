package norman.trash.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
public class DataTran {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Integer version = 0;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_file_id")
    private DataFile dataFile;
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private TranType ofxType;
    @Temporal(TemporalType.DATE)
    private Date ofxPostDate;
    @Temporal(TemporalType.DATE)
    private Date ofxUserDate;
    @Column(precision = 9, scale = 2)
    private BigDecimal ofxAmount;
    @Column(length = 50)
    private String ofxFitId;
    @Column(length = 10)
    private String ofxSic;
    @Column(length = 10)
    private String ofxCheckNumber;
    @Column(length = 10)
    private String ofxCorrectFitId;
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private CorrectAction ofxCorrectAction;
    @Column(length = 100)
    private String ofxName;
    @Column(length = 10)
    private String ofxCategory;
    @Column(length = 100)
    private String ofxMemo;

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

    public DataFile getDataFile() {
        return dataFile;
    }

    public void setDataFile(DataFile dataFile) {
        this.dataFile = dataFile;
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
}
