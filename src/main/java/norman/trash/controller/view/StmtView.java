package norman.trash.controller.view;

import norman.trash.domain.AcctType;
import norman.trash.domain.Stmt;

import java.math.BigDecimal;
import java.util.Date;

import static norman.trash.domain.AcctType.BILL;
import static norman.trash.domain.AcctType.CC;

public class StmtView {
    private Long id;
    private BigDecimal openBalance;
    private BigDecimal credits;
    private BigDecimal debits;
    private BigDecimal fees;
    private BigDecimal interest;
    private BigDecimal closeBalance;
    private BigDecimal minimumDue;
    private Date dueDate;
    private Date closeDate;
    //
    private Long acctId;
    private String acctName;
    private AcctType acctType;
    private boolean cc;
    private boolean billOrCc;

    public Long getId() {
        return id;
    }

    public BigDecimal getOpenBalance() {
        return openBalance;
    }

    public BigDecimal getCredits() {
        return credits;
    }

    public BigDecimal getDebits() {
        return debits;
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

    public AcctType getAcctType() {
        return acctType;
    }

    public boolean isCc() {
        return cc;
    }

    public boolean isBillOrCc() {
        return billOrCc;
    }

    public StmtView(Stmt stmt) {
        id = stmt.getId();
        openBalance = stmt.getOpenBalance();
        credits = stmt.getCredits();
        debits = stmt.getDebits();
        fees = stmt.getFees();
        interest = stmt.getInterest();
        closeBalance = stmt.getCloseBalance();
        minimumDue = stmt.getMinimumDue();
        dueDate = stmt.getDueDate();
        closeDate = stmt.getCloseDate();
        acctId = stmt.getAcct().getId();
        acctName = stmt.getAcct().getName();
        acctType = stmt.getAcct().getType();
        cc = acctType == CC;
        billOrCc = acctType == BILL || acctType == CC;
    }
}
