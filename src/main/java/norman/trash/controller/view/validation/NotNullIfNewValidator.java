package norman.trash.controller.view.validation;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotNullIfNewValidator implements ConstraintValidator<NotNullIfNew, Object> {
    private String obj;
    private String id;

    @Override
    public void initialize(NotNullIfNew constraintAnnotation) {
        obj = constraintAnnotation.obj();
        id = constraintAnnotation.id();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(o);
        Object objValue = beanWrapper.getPropertyValue(obj);
        Object idValue = beanWrapper.getPropertyValue(id);
        // If id is not null, this is an existing record and we don't care about the object.
        if (idValue != null) {
            return true;
            // Otherwise, the object must not be null.
        } else {
            return objValue != null;
        }
    }
}
