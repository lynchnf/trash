package norman.trash.controller.view.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CreditsAndDebitsValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CreditsAndDebits {
    String message() default "If Credit Card, Open Balance + Credits - Debits - Fees - Interest must equal Close Balance.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
