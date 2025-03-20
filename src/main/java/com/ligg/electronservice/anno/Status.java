package com.ligg.electronservice.anno;


import com.ligg.electronservice.validation.StateValidation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

//号码状态校验注解
@Documented//元注解
@Target({ElementType.PARAMETER, FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = { StateValidation.class })//指定提供校验规则的类
public @interface Status {

    String message() default "非法参数";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default {};
}
