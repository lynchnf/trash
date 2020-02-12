package norman.trash.controller.view.validation;

import norman.trash.controller.view.StmtReconcileForm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class CreditsAndDebitsValidator implements ConstraintValidator<CreditsAndDebits, Object> {
    @Override
    public boolean isValid(Object formBean, ConstraintValidatorContext context) {
        boolean isValid = true;

        if (formBean != null) {
            StmtReconcileForm stmtReconcileForm = (StmtReconcileForm) formBean;
            BigDecimal openBalance = stmtReconcileForm.getOpenBalance();
            BigDecimal credits = stmtReconcileForm.getCredits();
            BigDecimal debits = stmtReconcileForm.getDebits();
            BigDecimal fees = stmtReconcileForm.getFees();
            BigDecimal interest = stmtReconcileForm.getInterest();
            BigDecimal closeBalance = stmtReconcileForm.getCloseBalance();
            if (stmtReconcileForm.isCc() && openBalance != null && credits != null && debits != null && fees != null &&
                    interest != null && closeBalance != null) {
                BigDecimal calculatedCloseBalance = openBalance.add(credits).add(debits).add(fees).add(interest);
                isValid = calculatedCloseBalance.compareTo(closeBalance) == 0;
            }
        }

        return isValid;
    }
}
