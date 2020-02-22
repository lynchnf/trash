package norman.trash.controller.view;

import norman.trash.domain.Stmt;

import java.math.BigDecimal;
import java.util.Date;

public class StmtView {
    // Stmt
    private Long id;
    private BigDecimal openBalance;
    private BigDecimal debits;
    private BigDecimal credits;
    private BigDecimal fees;
    private BigDecimal interest;
    private BigDecimal closeBalance;
    private BigDecimal minimumDue;
    private Date dueDate;
    private Date closeDate;
    // Acct
    private Long acctId;
    private String acctName;

    public StmtView(Stmt stmt) {
        id = stmt.getId();
        openBalance = stmt.getOpenBalance();
        debits = stmt.getDebits();
        credits = stmt.getCredits();
        fees = stmt.getFees();
        interest = stmt.getInterest();
        closeBalance = stmt.getCloseBalance();
        minimumDue = stmt.getMinimumDue();
        dueDate = stmt.getDueDate();
        closeDate = stmt.getCloseDate();
        acctId = stmt.getAcct().getId();
        acctName = stmt.getAcct().getName();
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getOpenBalance() {
        return openBalance;
    }

    public BigDecimal getDebits() {
        return debits;
    }

    public BigDecimal getCredits() {
        return credits;
    }

    public BigDecimal getFees() {
        return fees;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public BigDecimal getCloseBalance() {
        return closeBalance;
    }

    public BigDecimal getMinimumDue() {
        return minimumDue;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public Long getAcctId() {
        return acctId;
    }

    public String getAcctName() {
        return acctName;
    }
}
