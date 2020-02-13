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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_id")
    private Cat cat;
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date postDate;
    @Column(precision = 9, scale = 2, nullable = false)
    private BigDecimal amount;
    @Column(length = 10)
    private String checkNumber;
    @Column(length = 100, nullable = false)
    private String name;
    @Column(length = 100)
    private String memo;

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

    public Cat getCat() {
        return cat;
    }

    public void setCat(Cat cat) {
        this.cat = cat;
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

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
