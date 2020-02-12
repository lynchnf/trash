package norman.trash.controller.view.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CloseBalanceValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CloseBalance {
    String message() default "Close Balance entered must match the actual close balance.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
