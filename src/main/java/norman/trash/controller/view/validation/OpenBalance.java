package norman.trash.controller.view.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = OpenBalanceValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface OpenBalance {
    String message() default "If Credit Card, Open Balance entered must match the actual open balance.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
