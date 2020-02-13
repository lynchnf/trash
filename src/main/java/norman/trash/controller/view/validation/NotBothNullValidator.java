package norman.trash.controller.view.validation;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotBothNullValidator implements ConstraintValidator<NotBothNull, Object> {
    private String fieldName1;
    private String fieldName2;

    @Override
    public void initialize(NotBothNull constraintAnnotation) {
        fieldName1 = constraintAnnotation.fieldName1();
        fieldName2 = constraintAnnotation.fieldName2();
    }

    @Override
    public boolean isValid(Object formBean, ConstraintValidatorContext context) {
        if (formBean != null) {
            Object field1Value = new BeanWrapperImpl(formBean).getPropertyValue(fieldName1);
            Object field2Value = new BeanWrapperImpl(formBean).getPropertyValue(fieldName2);
            if (field1Value == null && field2Value == null) {
                return false;
            }
        }

        return true;
    }
}
