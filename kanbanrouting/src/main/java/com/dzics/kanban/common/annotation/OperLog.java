package com.dzics.kanban.common.annotation;




import com.dzics.kanban.enums.OperType;

import java.lang.annotation.*;

/**
 * @author ZhangChengJun
 * Date 2020/10/27.
 */
@Target(ElementType.METHOD) //注解放置的目标位置,METHOD是可注解在方法级别上
@Retention(RetentionPolicy.RUNTIME) //注解在哪个阶段执行
@Documented
public @interface OperLog {
    String operModul() default ""; // 操作模块

    /**
     * （0其它 1新增 2修改 3删除）
     *
     * @return
     */
    OperType operType() default OperType.OTHER;  // 操作类型

    String operDesc() default "";  // 操作说明

    String operatorType() default "公共接口"; //接口类型
}
