package norman.trash.controller.view.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NotBothNullValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotBothNull {
    String fieldName1();

    String fieldName2();

    String message() default "must not both be null";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
