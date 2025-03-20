package com.ligg.electronservice.validation;

import com.ligg.electronservice.anno.Status;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


// 重写自定义注解
public class StateValidation implements ConstraintValidator<Status, Integer> {
    @Override
    public void initialize(Status constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    /**
     *
     * @param value 将来要校验的数据
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        // 提供校验规则
        if (value == null) {
            return false;
        }
        return value == 0 || value == 1;
    }
}
