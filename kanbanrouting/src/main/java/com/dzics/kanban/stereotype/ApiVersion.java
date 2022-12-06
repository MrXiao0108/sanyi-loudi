package com.dzics.kanban.stereotype;

import java.lang.annotation.*;

/**
 * 版本注解
 * @author neverend
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiVersion {
    String value() default "1.0.0";
}
