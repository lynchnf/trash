package norman.trash.controller.view.validation;

import norman.trash.TrashUtils;
import norman.trash.controller.view.StmtReconcileForm;
import norman.trash.domain.Acct;
import norman.trash.domain.Stmt;
import norman.trash.domain.Tran;
import norman.trash.exception.NotFoundException;
import norman.trash.service.AcctService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class OpenBalanceValidator implements ConstraintValidator<OpenBalance, Object> {
    @Autowired
    private AcctService acctService;

    @Override
    public boolean isValid(Object formBean, ConstraintValidatorContext context) {
        boolean isValid = true;
        String message = context.getDefaultConstraintMessageTemplate();

        if (formBean != null) {
            StmtReconcileForm stmtReconcileForm = (StmtReconcileForm) formBean;
            if (stmtReconcileForm.isCc() && stmtReconcileForm.getOpenBalance() != null) {
                Long acctId = stmtReconcileForm.getAcctId();
                try {
                    Acct acct = acctService.findById(acctId);
                    BigDecimal actualOpenBalance = BigDecimal.ZERO;
                    for (Stmt stmt : acct.getStmts()) {
                        if (!stmt.getCloseDate().equals(TrashUtils.getEndOfTime())) {
                            for (Tran tran : stmt.getDebitTrans()) {
                                actualOpenBalance = actualOpenBalance.subtract(tran.getAmount());
                            }
                            for (Tran tran : stmt.getCreditTrans()) {
                                actualOpenBalance = actualOpenBalance.add(tran.getAmount());
                            }
                        }
                    }
                    isValid = actualOpenBalance.compareTo(stmtReconcileForm.getOpenBalance()) == 0;
                } catch (NotFoundException e) {
                    isValid = false;
                    message = e.getMessage();
                }
            }
        }

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addPropertyNode("openBalance")
                    .addConstraintViolation();
        }
        return isValid;
    }
}
