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
        @NotNullIfCondition(field = "previousBalance", condition = "cc", message = "If Credit Card, Previous Balance may not be blank."),
        @NotNullIfCondition(field = "paymentsAndOtherCredits", condition = "cc", message = "If Credit Card, Payments And Other Credits may not be blank."),
        @NotNullIfCondition(field = "purchasesAndAdjustments", condition = "cc", message = "If Credit Card, Purchases And Adjustments may not be blank."),
        @NotNullIfCondition(field = "feesCharged", condition = "cc", message = "If Credit Card, Fees Charged may not be blank."),
        @NotNullIfCondition(field = "interestCharged", condition = "cc", message = "If Credit Card, Interest Charged may not be blank."),
        @NotNullIfCondition(field = "statementClosingDate", condition = "cc", message = "If Credit Card, Statement Closing Date may not be blank.")})
// @formatter:on
public class AcctReconcileForm {
    private Long id;
    private Integer version = 0;
    private String name;
    private AcctType type;
    private boolean cc;
    @Digits(integer = 7, fraction = 2,
            message = "Previous Balance value out of bounds. (<{integer} digits>.<{fraction} digits> expected)")
    private BigDecimal previousBalance;
    @Digits(integer = 7, fraction = 2,
            message = "Payments And Other Credits value out of bounds. (<{integer} digits>.<{fraction} digits> expected)")
    private BigDecimal paymentsAndOtherCredits;
    @Digits(integer = 7, fraction = 2,
            message = "Purchases And Adjustments value out of bounds. (<{integer} digits>.<{fraction} digits> expected)")
    private BigDecimal purchasesAndAdjustments;
    @Digits(integer = 7, fraction = 2,
            message = "Fees Charged value out of bounds. (<{integer} digits>.<{fraction} digits> expected)")
    private BigDecimal feesCharged;
    @Digits(integer = 7, fraction = 2,
            message = "Interest Charged value out of bounds. (<{integer} digits>.<{fraction} digits> expected)")
    private BigDecimal interestCharged;
    @NotNull(message = "New Balance may not be blank.")
    @Digits(integer = 7, fraction = 2,
            message = "New Balance value out of bounds. (<{integer} digits>.<{fraction} digits> expected)")
    private BigDecimal newBalance;
    @Digits(integer = 7, fraction = 2,
            message = "Minimum Payment Due value out of bounds. (<{integer} digits>.<{fraction} digits> expected)")
    private BigDecimal minimumPaymentDue;
    @NotNull(message = "Payment Due Date may not be blank.")
    @DateTimeFormat(pattern = "M/d/yyyy")
    private Date paymentDueDate;
    @DateTimeFormat(pattern = "M/d/yyyy")
    private Date statementClosingDate;

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

    public BigDecimal getPreviousBalance() {
        return previousBalance;
    }

    public void setPreviousBalance(BigDecimal previousBalance) {
        this.previousBalance = previousBalance;
    }

    public BigDecimal getPaymentsAndOtherCredits() {
        return paymentsAndOtherCredits;
    }

    public void setPaymentsAndOtherCredits(BigDecimal paymentsAndOtherCredits) {
        this.paymentsAndOtherCredits = paymentsAndOtherCredits;
    }

    public BigDecimal getPurchasesAndAdjustments() {
        return purchasesAndAdjustments;
    }

    public void setPurchasesAndAdjustments(BigDecimal purchasesAndAdjustments) {
        this.purchasesAndAdjustments = purchasesAndAdjustments;
    }

    public BigDecimal getFeesCharged() {
        return feesCharged;
    }

    public void setFeesCharged(BigDecimal feesCharged) {
        this.feesCharged = feesCharged;
    }

    public BigDecimal getInterestCharged() {
        return interestCharged;
    }

    public void setInterestCharged(BigDecimal interestCharged) {
        this.interestCharged = interestCharged;
    }

    public BigDecimal getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(BigDecimal newBalance) {
        this.newBalance = newBalance;
    }

    public BigDecimal getMinimumPaymentDue() {
        return minimumPaymentDue;
    }

    public void setMinimumPaymentDue(BigDecimal minimumPaymentDue) {
        this.minimumPaymentDue = minimumPaymentDue;
    }

    public Date getPaymentDueDate() {
        return paymentDueDate;
    }

    public void setPaymentDueDate(Date paymentDueDate) {
        this.paymentDueDate = paymentDueDate;
    }

    public Date getStatementClosingDate() {
        return statementClosingDate;
    }

    public void setStatementClosingDate(Date statementClosingDate) {
        this.statementClosingDate = statementClosingDate;
    }
}
