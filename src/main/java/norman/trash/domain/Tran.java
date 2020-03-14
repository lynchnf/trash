package norman.trash.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
public class Tran {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Integer version = 0;
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date postDate;
    @Temporal(TemporalType.DATE)
    private Date manualPostDate;
    @Temporal(TemporalType.DATE)
    private Date uploadedPostDate;
    @Column(precision = 9, scale = 2, nullable = false)
    private BigDecimal amount;
    @Column(precision = 9, scale = 2)
    private BigDecimal manualAmount;
    @Column(precision = 9, scale = 2)
    private BigDecimal uploadedAmount;
    @Column(length = 10)
    private String checkNumber;
    @Column(length = 10)
    private String manualCheckNumber;
    @Column(length = 10)
    private String uploadedCheckNumber;
    @Column(length = 100, nullable = false)
    private String name;
    @Column(length = 100)
    private String manualName;
    @Column(length = 100)
    private String uploadedName;
    @Column(length = 100)
    private String memo;
    @Column(length = 100)
    private String manualMemo;
    @Column(length = 100)
    private String uploadedMemo;
    @Column(length = 50)
    private String ofxFitId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stmt_id", nullable = false)
    private Stmt stmt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_id")
    private Cat cat;

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

    public Date getManualPostDate() {
        return manualPostDate;
    }

    public void setManualPostDate(Date manualPostDate) {
        this.manualPostDate = manualPostDate;
    }

    public Date getUploadedPostDate() {
        return uploadedPostDate;
    }

    public void setUploadedPostDate(Date uploadedPostDate) {
        this.uploadedPostDate = uploadedPostDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getManualAmount() {
        return manualAmount;
    }

    public void setManualAmount(BigDecimal manualAmount) {
        this.manualAmount = manualAmount;
    }

    public BigDecimal getUploadedAmount() {
        return uploadedAmount;
    }

    public void setUploadedAmount(BigDecimal uploadedAmount) {
        this.uploadedAmount = uploadedAmount;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public String getManualCheckNumber() {
        return manualCheckNumber;
    }

    public void setManualCheckNumber(String manualCheckNumber) {
        this.manualCheckNumber = manualCheckNumber;
    }

    public String getUploadedCheckNumber() {
        return uploadedCheckNumber;
    }

    public void setUploadedCheckNumber(String uploadedCheckNumber) {
        this.uploadedCheckNumber = uploadedCheckNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManualName() {
        return manualName;
    }

    public void setManualName(String manualName) {
        this.manualName = manualName;
    }

    public String getUploadedName() {
        return uploadedName;
    }

    public void setUploadedName(String uploadedName) {
        this.uploadedName = uploadedName;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getManualMemo() {
        return manualMemo;
    }

    public void setManualMemo(String manualMemo) {
        this.manualMemo = manualMemo;
    }

    public String getUploadedMemo() {
        return uploadedMemo;
    }

    public void setUploadedMemo(String uploadedMemo) {
        this.uploadedMemo = uploadedMemo;
    }

    public String getOfxFitId() {
        return ofxFitId;
    }

    public void setOfxFitId(String ofxFitId) {
        this.ofxFitId = ofxFitId;
    }

    public Stmt getStmt() {
        return stmt;
    }

    public void setStmt(Stmt stmt) {
        this.stmt = stmt;
    }

    public Cat getCat() {
        return cat;
    }

    public void setCat(Cat cat) {
        this.cat = cat;
    }
}
