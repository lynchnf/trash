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
    @JoinColumn(name = "debit_stmt_id")
    private Stmt debitStmt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_stmt_id")
    private Stmt creditStmt;
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

    public Stmt getDebitStmt() {
        return debitStmt;
    }

    public void setDebitStmt(Stmt debitStmt) {
        this.debitStmt = debitStmt;
    }

    public Stmt getCreditStmt() {
        return creditStmt;
    }

    public void setCreditStmt(Stmt creditStmt) {
        this.creditStmt = creditStmt;
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
