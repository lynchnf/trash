package norman.trash.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class DataFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Integer version = 0;
    @Column(length = 100)
    private String originalFilename;
    @Column(length = 100)
    private String contentType;
    private Long size;
    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadTimestamp;
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private DataFileStatus status;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "dataFile")
    private List<DataLine> dataLines = new ArrayList<>();
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "dataFile")
    private OfxAcct ofxAcct;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "dataFile")
    private OfxInst ofxInst;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "dataFile")
    private List<OfxStmtTran> ofxStmtTrans = new ArrayList<>();

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

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Date getUploadTimestamp() {
        return uploadTimestamp;
    }

    public void setUploadTimestamp(Date uploadTimestamp) {
        this.uploadTimestamp = uploadTimestamp;
    }

    public DataFileStatus getStatus() {
        return status;
    }

    public void setStatus(DataFileStatus status) {
        this.status = status;
    }

    public List<DataLine> getDataLines() {
        return dataLines;
    }

    public void setDataLines(List<DataLine> dataLines) {
        this.dataLines = dataLines;
    }

    public OfxAcct getOfxAcct() {
        return ofxAcct;
    }

    public void setOfxAcct(OfxAcct ofxAcct) {
        this.ofxAcct = ofxAcct;
    }

    public OfxInst getOfxInst() {
        return ofxInst;
    }

    public void setOfxInst(OfxInst ofxInst) {
        this.ofxInst = ofxInst;
    }

    public List<OfxStmtTran> getOfxStmtTrans() {
        return ofxStmtTrans;
    }

    public void setOfxStmtTrans(List<OfxStmtTran> ofxStmtTrans) {
        this.ofxStmtTrans = ofxStmtTrans;
    }
}
