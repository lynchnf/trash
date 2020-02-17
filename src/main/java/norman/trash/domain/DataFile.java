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

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
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
}
