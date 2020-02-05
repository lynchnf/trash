package norman.trash.controller.view;

import norman.trash.domain.Stmt;

import java.math.BigDecimal;
import java.util.Date;

public class StmtView {
    private Long id;
    private String acctName;
    private Date closeDate;
    private BigDecimal openBalance;
    private BigDecimal debits;
    private BigDecimal credits;
    private BigDecimal fees;
    private BigDecimal interest;
    private BigDecimal closeBalance;
    private BigDecimal minimumDue;
    private Date dueDate;

    public StmtView(Stmt stmt) {
        id = stmt.getId();
        acctName = stmt.getAcct().getName();
        closeDate = stmt.getCloseDate();
        openBalance = stmt.getOpenBalance();
        debits = stmt.getDebits();
        credits = stmt.getCredits();
        fees = stmt.getFees();
        interest = stmt.getInterest();
        closeBalance = stmt.getCloseBalance();
        minimumDue = stmt.getMinimumDue();
        dueDate = stmt.getDueDate();
    }

    public Long getId() {
        return id;
    }

    public String getAcctName() {
        return acctName;
    }

    public Date getCloseDate() {
        return closeDate;
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
}
