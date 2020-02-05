package norman.trash.controller.view;

import norman.trash.controller.view.validation.NotNullIfCondition;
import norman.trash.domain.Acct;
import norman.trash.domain.AcctType;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

// @formatter:off
@NotNullIfCondition.List({
        @NotNullIfCondition(fieldName = "openBalance", conditionField = "cc", message = "If Credit Card, Previous Balance may not be blank."),
        @NotNullIfCondition(fieldName = "debits", conditionField = "cc", message = "If Credit Card, Purchases And Adjustments may not be blank."),
        @NotNullIfCondition(fieldName = "credits", conditionField = "cc", message = "If Credit Card, Payments And Other Credits may not be blank."),
        @NotNullIfCondition(fieldName = "fees", conditionField = "cc", message = "If Credit Card, Fees Charged may not be blank."),
        @NotNullIfCondition(fieldName = "interest", conditionField = "cc", message = "If Credit Card, Interest Charged may not be blank."),
        @NotNullIfCondition(fieldName = "closeDate", conditionField = "cc", message = "If Credit Card, Statement Closing Date may not be blank.")})
// @formatter:on
public class AcctReconcileForm {
    private Long id;
    private Integer version = 0;
    private String name;
    private AcctType type;
    private boolean cc;
    @Digits(integer = 7, fraction = 2,
            message = "Previous Balance value out of bounds. (<{integer} digits>.<{fraction} digits> expected)")
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
    @NotNull(message = "New Balance may not be blank.")
    @Digits(integer = 7, fraction = 2,
            message = "New Balance value out of bounds. (<{integer} digits>.<{fraction} digits> expected)")
    private BigDecimal closeBalance;
    @Digits(integer = 7, fraction = 2,
            message = "Minimum Payment Due value out of bounds. (<{integer} digits>.<{fraction} digits> expected)")
    private BigDecimal minimumDue;
    @NotNull(message = "Payment Due Date may not be blank.")
    @DateTimeFormat(pattern = "M/d/yyyy")
    private Date dueDate;
    @DateTimeFormat(pattern = "M/d/yyyy")
    private Date closeDate;

    public AcctReconcileForm() {
    }

    public AcctReconcileForm(Acct acct) {
        id = acct.getId();
        version = acct.getVersion();
        name = acct.getName();
        type = acct.getType();
        cc = type == AcctType.CC;
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
}
