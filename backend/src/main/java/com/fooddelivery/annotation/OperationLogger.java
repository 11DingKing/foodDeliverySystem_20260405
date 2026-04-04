package com.fooddelivery.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLogger {
    String module() default "";
    String operation() default "";
}
