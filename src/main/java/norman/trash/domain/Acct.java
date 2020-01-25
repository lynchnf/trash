package norman.trash.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Acct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Integer version = 0;
    @Column(length = 50)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private AcctType type;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "debitAcct")
    private List<Tran> debitTrans = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "creditAcct")
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
