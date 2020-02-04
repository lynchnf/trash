package norman.trash.controller.view.validation;

import javax.validation.Constraint;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NotNullIfConditionValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotNullIfCondition {
    String message() default "If condition, must not be null";

    String field();

    String condition();

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        NotNullIfCondition[] value();
    }
}
