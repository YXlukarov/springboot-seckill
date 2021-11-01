package com.jesper.seckill.validator;

import com.jesper.seckill.util.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsPasswordValidator implements ConstraintValidator<IsPassword, String> {
    private boolean required = false;

    //初始化
    @Override
    public void initialize(IsPassword isPassword) {
        required = isPassword.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (required) {
            return ValidatorUtil.isPassword(value);
        } else {
            if (StringUtils.isEmpty(value)) {
                return true;
            } else {
                return ValidatorUtil.isPassword(value);
            }
        }
    }
}
