package com.dzics.common.model.request.order;

import com.dzics.common.util.PageLimit;
import lombok.Data;

/**
 * 订单列表参数
 *
 * @author ZhangChengJun
 * Date 2021/7/5.
 * @since
 */
@Data
public class OrderParmsModel extends PageLimit {
    String orderNo;
    String departName;
}
