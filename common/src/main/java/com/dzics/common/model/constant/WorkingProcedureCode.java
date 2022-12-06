package com.dzics.common.model.constant;

import lombok.Data;

/**
 * 工序状态变量
 *
 * @author ZhangChengJun
 * Date 2021/5/20.
 * @since
 */
@Data
public class WorkingProcedureCode {
    /**
     *  0 未开始
     */
    public final static int NOT = 0;
    /**
     *  1 进入
     */
    public final static int IN = 1;
    /**
     * 2 完成，
     */
    public final static int OUT = 2;
    /**
     * 3 异常
     */
    public final static int ERR = 3;
}
