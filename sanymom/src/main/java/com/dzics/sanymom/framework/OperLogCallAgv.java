package com.dzics.sanymom.framework;


import java.lang.annotation.*;

/**
 * @author ZhangChengJun
 * Date 2020/10/27.
 */
@Target(ElementType.METHOD) //注解放置的目标位置,METHOD是可注解在方法级别上
@Retention(RetentionPolicy.RUNTIME) //注解在哪个阶段执行
@Documented
public @interface OperLogCallAgv {
    String operModul() default ""; // 操作模块

    String operDesc() default "";  // 操作说明


}
