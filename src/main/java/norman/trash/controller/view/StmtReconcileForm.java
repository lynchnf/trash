package norman.trash.controller.view;

import norman.trash.TrashUtils;
import norman.trash.controller.view.validation.CloseBalance;
import norman.trash.controller.view.validation.CreditsAndDebits;
import norman.trash.controller.view.validation.NotNullIfCondition;
import norman.trash.controller.view.validation.OpenBalance;
import norman.trash.domain.Acct;
import norman.trash.domain.AcctType;
import norman.trash.domain.Stmt;
import norman.trash.domain.Tran;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static norman.trash.domain.AcctType.BILL;
import static norman.trash.domain.AcctType.CC;

// @formatter:off
@NotNullIfCondition.List({
        @NotNullIfCondition(fieldName = "openBalance", conditionField = "cc", message = "If Credit Card, Opening Balance may not be blank."),
        @NotNullIfCondition(fieldName = "debits", conditionField = "cc", message = "If Credit Card, Purchases And Adjustments may not be blank."),
        @NotNullIfCondition(fieldName = "credits", conditionField = "cc", message = "If Credit Card, Payments And Other Credits may not be blank."),
        @NotNullIfCondition(fieldName = "fees", conditionField = "cc", message = "If Credit Card, Fees Charged may not be blank."),
        @NotNullIfCondition(fieldName = "interest", conditionField = "cc", message = "If Credit Card, Interest Charged may not be blank."),
        @NotNullIfCondition(fieldName = "minimumDue", conditionField = "cc", message = "If Credit Card, Minimum Payment may not be blank."),
        @NotNullIfCondition(fieldName = "dueDate", conditionField = "billOrCc", message = "If Bill or Credit Card, Payment Due Date may not be blank.")})
@OpenBalance
@CreditsAndDebits
@CloseBalance
// @formatter:on
public class StmtReconcileForm {
    private Long id;
    private Integer version = 0;
    private Long acctId;
    private String name;
    private AcctType type;
    private boolean cc;
    private boolean billOrCc;
    @Digits(integer = 7, fraction = 2,
            message = "Opening Balance value out of bounds. (<{integer} digits>.<{fraction} digits> expected)")
    private BigDecimal openBalance;
    @Digits(integer = 7, fraction = 2,
            message = "Purchases And Adjustments value out of bounds. (<{integer} digits>.<{fraction} digits> expected)")
    private BigDecimal debits;
    @Digits(integer = 7, fraction = 2,
            message = "Payments And Other Credits value out of bounds. (<{integer} digits>.<{fraction} digits> expected)")
    private BigDecimal credits;
    @Digits(integer = 7, fraction = 2,
            message = "Fees Charged value out of bounds. (<{integer} digits>.<{fraction} digits> expected)")
    private BigDecimal fees;
    @Digits(integer = 7, fraction = 2,
            message = "Interest Charged value out of bounds. (<{integer} digits>.<{fraction} digits> expected)")
    private BigDecimal interest;
    @NotNull(message = "Closing Balance may not be blank.")
    @Digits(integer = 7, fraction = 2,
            message = "Closing Balance value out of bounds. (<{integer} digits>.<{fraction} digits> expected)")
    private BigDecimal closeBalance;
    @Digits(integer = 7, fraction = 2,
            message = "Minimum Payment Due value out of bounds. (<{integer} digits>.<{fraction} digits> expected)")
    private BigDecimal minimumDue;
    @DateTimeFormat(pattern = "M/d/yyyy")
    private Date dueDate;
    @DateTimeFormat(pattern = "M/d/yyyy")
    @NotNull(message = "Statement Closing Date may not be blank.")
    private Date closeDate;
    @Valid
    private List<StmtReconcileRow> stmtReconcileRows = new ArrayList<>();

    public StmtReconcileForm() {
    }

    public StmtReconcileForm(Stmt stmt) {
        id = stmt.getId();
        version = stmt.getVersion();
        acctId = stmt.getAcct().getId();
        name = stmt.getAcct().getName();
        type = stmt.getAcct().getType();
        cc = type == CC;
        billOrCc = type == BILL || type == CC;

        for (Tran tran : stmt.getDebitTrans()) {
            stmtReconcileRows.add(new StmtReconcileRow(tran, BalanceType.DEBIT_TRAN));
        }
        for (Tran tran : stmt.getCreditTrans()) {
            stmtReconcileRows.add(new StmtReconcileRow(tran, BalanceType.CREDIT_TRAN));
        }

        Comparator<StmtReconcileRow> comparator = new Comparator<StmtReconcileRow>() {
            @Override
            public int compare(StmtReconcileRow row1, StmtReconcileRow row2) {
                return row1.getPostDate().compareTo(row2.getPostDate());
            }
        };
        stmtReconcileRows.sort(comparator);
    }

    public List<Stmt> toStmts() {
        List<Stmt> stmts = new ArrayList<>();
        Stmt reconciledStmt = new Stmt();
        reconciledStmt.setId(id);
        reconciledStmt.setVersion(version);
        reconciledStmt.setAcct(new Acct());
        reconciledStmt.getAcct().setId(acctId);
        reconciledStmt.setOpenBalance(openBalance);
        reconciledStmt.setDebits(debits);
        reconciledStmt.setCredits(credits);
        reconciledStmt.setFees(fees);
        reconciledStmt.setInterest(interest);
        reconciledStmt.setCloseBalance(closeBalance);
        reconciledStmt.setMinimumDue(minimumDue);
        reconciledStmt.setDueDate(dueDate);
        reconciledStmt.setCloseDate(closeDate);
        stmts.add(reconciledStmt);

        // Current period statement.
        Stmt currentStmt = new Stmt();
        currentStmt.setAcct(new Acct());
        currentStmt.getAcct().setId(acctId);
        currentStmt.setCloseDate(TrashUtils.getEndOfTime());
        stmts.add(currentStmt);

        for (StmtReconcileRow stmtReconcileRow : stmtReconcileRows) {
            Tran tran = stmtReconcileRow.toTran();
            if (stmtReconcileRow.isSelected()) {
                if (stmtReconcileRow.getType() == BalanceType.DEBIT_TRAN) {
                    tran.setDebitStmt(reconciledStmt);
                    reconciledStmt.getDebitTrans().add(tran);
                } else {
                    tran.setCreditStmt(reconciledStmt);
                    reconciledStmt.getCreditTrans().add(tran);
                }
            } else {
                if (stmtReconcileRow.getType() == BalanceType.DEBIT_TRAN) {
                    tran.setDebitStmt(currentStmt);
                    currentStmt.getDebitTrans().add(tran);
                } else {
                    tran.setCreditStmt(currentStmt);
                    currentStmt.getCreditTrans().add(tran);
                }
            }
        }
        return stmts;
    }

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

    public Long getAcctId() {
        return acctId;
    }

    public void setAcctId(Long acctId) {
        this.acctId = acctId;
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

    public boolean isCc() {
        return cc;
    }

    public void setCc(boolean cc) {
        this.cc = cc;
    }

    public boolean isBillOrCc() {
        return billOrCc;
    }

    public void setBillOrCc(boolean billOrCc) {
        this.billOrCc = billOrCc;
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

    public List<StmtReconcileRow> getStmtReconcileRows() {
        return stmtReconcileRows;
    }

    public void setStmtReconcileRows(List<StmtReconcileRow> stmtReconcileRows) {
        this.stmtReconcileRows = stmtReconcileRows;
    }
}
