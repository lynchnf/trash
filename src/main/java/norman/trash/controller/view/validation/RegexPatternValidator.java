package norman.trash.controller.view.validation;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class RegexPatternValidator implements ConstraintValidator<RegexPattern, String> {
    @Override
    public void initialize(RegexPattern constraintAnnotation) {
    }

    @Override
    public boolean isValid(String regexValue, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isBlank(regexValue)) {
            return true;
        }
        try {
            Pattern.compile(regexValue);
            return true;
        } catch (Exception PatternSyntaxException) {
            return false;
        }
    }
}