package norman.trash.controller.view.validation;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotNullIfConditionValidator implements ConstraintValidator<NotNullIfCondition, Object> {
    private String field;
    private String condition;

    @Override
    public void initialize(NotNullIfCondition constraintAnnotation) {
        field = constraintAnnotation.field();
        condition = constraintAnnotation.condition();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(o);
        Object fieldValue = beanWrapper.getPropertyValue(field);
        boolean conditionValue = (boolean) beanWrapper.getPropertyValue(condition);
        // If condition is false, we don't care about the value of the field.
        if (!conditionValue) {
            return true;
            // Otherwise, the field must not be null.
        } else {
            return fieldValue != null;
        }
    }
}
