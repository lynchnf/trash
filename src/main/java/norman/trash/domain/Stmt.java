package norman.trash.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Stmt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Integer version = 0;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acct_id", nullable = false)
    private Acct acct;
    @Column(precision = 9, scale = 2)
    private BigDecimal openBalance;
    @Column(precision = 9, scale = 2)
    private BigDecimal debits;
    @Column(precision = 9, scale = 2)
    private BigDecimal credits;
    @Column(precision = 9, scale = 2)
    private BigDecimal fees;
    @Column(precision = 9, scale = 2)
    private BigDecimal interest;
    @Column(precision = 9, scale = 2)
    private BigDecimal closeBalance;
    @Column(precision = 9, scale = 2)
    private BigDecimal minimumDue;
    @Temporal(TemporalType.DATE)
    private Date dueDate;
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date closeDate;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "debitStmt")
    private List<Tran> debitTrans = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "creditStmt")
    private List<Tran> creditTrans = new ArrayList<>();

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

    public Acct getAcct() {
        return acct;
    }

    public void setAcct(Acct acct) {
        this.acct = acct;
    }

    public BigDecimal getOpenBalance() {
        return openBalance;
    }

    public void setOpenBalance(BigDecimal openBalance) {
        this.openBalance = openBalance;
    }

    public BigDecimal getDebits() {
        return debits;
    }

    public void setDebits(BigDecimal debits) {
        this.debits = debits;
    }

    public BigDecimal getCredits() {
        return credits;
    }

    public void setCredits(BigDecimal credits) {
        this.credits = credits;
    }

    public BigDecimal getFees() {
        return fees;
    }

    public void setFees(BigDecimal fees) {
        this.fees = fees;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }

    public BigDecimal getCloseBalance() {
        return closeBalance;
    }

    public void setCloseBalance(BigDecimal closeBalance) {
        this.closeBalance = closeBalance;
    }

    public BigDecimal getMinimumDue() {
        return minimumDue;
    }

    public void setMinimumDue(BigDecimal minimumDue) {
        this.minimumDue = minimumDue;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    public List<Tran> getDebitTrans() {
        return debitTrans;
    }

    public void setDebitTrans(List<Tran> debitTrans) {
        this.debitTrans = debitTrans;
    }

    public List<Tran> getCreditTrans() {
        return creditTrans;
    }

    public void setCreditTrans(List<Tran> creditTrans) {
        this.creditTrans = creditTrans;
    }
}
