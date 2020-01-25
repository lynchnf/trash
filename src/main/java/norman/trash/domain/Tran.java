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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debit_acct_id")
    private Acct debitAcct;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_acct_id")
    private Acct creditAcct;
    @Temporal(TemporalType.DATE)
    private Date postDate;
    @Column(precision = 9, scale = 2)
    private BigDecimal amount;

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

    public Acct getDebitAcct() {
        return debitAcct;
    }

    public void setDebitAcct(Acct debitAcct) {
        this.debitAcct = debitAcct;
    }

    public Acct getCreditAcct() {
        return creditAcct;
    }

    public void setCreditAcct(Acct creditAcct) {
        this.creditAcct = creditAcct;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
