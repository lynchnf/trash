package norman.trash.controller.view.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RegexPatternValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RegexPattern {
    String message() default "Invalid regular expression.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}