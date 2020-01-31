package norman.trash.controller.view.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NotNullIfNewValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotNullIfNew {
    String message() default "If new, must not be null";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String obj();

    String id();
}
