package norman.trash.controller.view.validation;

import norman.trash.NotFoundException;
import norman.trash.TrashUtils;
import norman.trash.controller.view.StmtReconcileForm;
import norman.trash.controller.view.StmtReconcileRow;
import norman.trash.domain.Acct;
import norman.trash.domain.Stmt;
import norman.trash.domain.Tran;
import norman.trash.service.AcctService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class CloseBalanceValidator implements ConstraintValidator<CloseBalance, Object> {
    @Autowired
    private AcctService acctService;

    @Override
    public boolean isValid(Object formBean, ConstraintValidatorContext context) {
        boolean isValid = true;
        String message = context.getDefaultConstraintMessageTemplate();

        if (formBean != null) {
            StmtReconcileForm stmtReconcileForm = (StmtReconcileForm) formBean;
            if (stmtReconcileForm.getCloseBalance() != null) {
                Long acctId = stmtReconcileForm.getAcctId();
                try {
                    Acct acct = acctService.findById(acctId);
                    BigDecimal actualCloseBalance = BigDecimal.ZERO;
                    for (Stmt stmt : acct.getStmts()) {
                        if (!stmt.getCloseDate().equals(TrashUtils.getEndOfTime())) {
                            for (Tran tran : stmt.getDebitTrans()) {
                                actualCloseBalance = actualCloseBalance.subtract(tran.getAmount());
                            }
                            for (Tran tran : stmt.getCreditTrans()) {
                                actualCloseBalance = actualCloseBalance.add(tran.getAmount());
                            }
                        }
                    }
                    for (StmtReconcileRow stmtReconcileRow : stmtReconcileForm.getStmtReconcileRows()) {
                        if (stmtReconcileRow.isSelected()) {
                            actualCloseBalance = actualCloseBalance.add(stmtReconcileRow.getAmount());
                        }
                    }
                    isValid = actualCloseBalance.compareTo(stmtReconcileForm.getCloseBalance()) == 0;
                } catch (NotFoundException e) {
                    isValid = false;
                    message = e.getMessage();
                }
            }
        }

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addPropertyNode("closeBalance")
                    .addConstraintViolation();
        }
        return isValid;
    }
}
