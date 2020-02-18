package norman.trash.domain;

import javax.persistence.*;

@Entity
public class OfxAcct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Integer version = 0;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_file_id")
    private DataFile dataFile;
    @Column(length = 10)
    private String bankId;
    @Column(length = 20)
    private String acctId;
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private AcctType type;

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

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getAcctId() {
        return acctId;
    }

    public void setAcctId(String acctId) {
        this.acctId = acctId;
    }

    public AcctType getType() {
        return type;
    }

    public void setType(AcctType type) {
        this.type = type;
    }
}
