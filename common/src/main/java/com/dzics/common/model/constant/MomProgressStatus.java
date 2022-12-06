package com.dzics.common.model.constant;

import lombok.Data;

/**
 * Mom订单状态
 *
 * @author ZhangChengJun
 * Date 2021/6/11.
 * @since
 */
@Data
public class MomProgressStatus {
    /**
     * 订单已下达
     */
    public static final String DOWN = "110";

    /**
     * 订单进行中
     */
    public static final String LOADING = "120";

    /**
     * 订单已完工
     */
    public static final String SUCCESS = "130";

    /**
     * 订单已删除
     */
    public static final String DELETE = "170";


    /**
     * 订单已强制关闭
     */
    public static final String CLOSE = "150";

    /**
     * 订单暂停
     */
    public static final String STOP = "160";


//

    public static final String OperationResultSucess= "2";
    public static final String OperationResultLoading = "1";
}
