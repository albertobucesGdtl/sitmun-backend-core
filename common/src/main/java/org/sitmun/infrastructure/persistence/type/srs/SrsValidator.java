package org.sitmun.infrastructure.persistence.type.srs;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class SrsValidator
  implements ConstraintValidator<Srs, String> {

  private static final Pattern pattern = Pattern.compile("^[A-Z\\-]+:\\d+$");

  @Override
  public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
    if (value == null) {
      return true;
    }
    return pattern.matcher(value).matches();
  }
}
