package norman.trash.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Acct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Integer version = 0;
    @Column(length = 50, nullable = false)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private AcctType type;
    @Column(length = 50)
    private String addressName;
    @Column(length = 50)
    private String address1;
    @Column(length = 50)
    private String address2;
    @Column(length = 50)
    private String city;
    @Column(length = 2)
    private String state;
    @Column(length = 10)
    private String zipCode;
    @Column(length = 20)
    private String phoneNumber;
    @Column(precision = 9, scale = 2)
    private BigDecimal creditLimit;
    @Column(length = 50)
    private String ofxOrganization;
    @Column(length = 10)
    private String ofxFid;
    @Column(length = 10)
    private String ofxBankId;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "acct")
    private List<AcctNbr> acctNbrs = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "acct")
    private List<Stmt> stmts = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "acct")
    private List<DataFile> dataFiles = new ArrayList<>();

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AcctType getType() {
        return type;
    }

    public void setType(AcctType type) {
        this.type = type;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public String getOfxOrganization() {
        return ofxOrganization;
    }

    public void setOfxOrganization(String ofxOrganization) {
        this.ofxOrganization = ofxOrganization;
    }

    public String getOfxFid() {
        return ofxFid;
    }

    public void setOfxFid(String ofxFid) {
        this.ofxFid = ofxFid;
    }

    public String getOfxBankId() {
        return ofxBankId;
    }

    public void setOfxBankId(String ofxBankId) {
        this.ofxBankId = ofxBankId;
    }

    public List<AcctNbr> getAcctNbrs() {
        return acctNbrs;
    }

    public void setAcctNbrs(List<AcctNbr> acctNbrs) {
        this.acctNbrs = acctNbrs;
    }

    public List<Stmt> getStmts() {
        return stmts;
    }

    public void setStmts(List<Stmt> stmts) {
        this.stmts = stmts;
    }

    public List<DataFile> getDataFiles() {
        return dataFiles;
    }

    public void setDataFiles(List<DataFile> dataFiles) {
        this.dataFiles = dataFiles;
    }
}
