package com.dzics.business.model.vo.alarm;

import com.dzics.common.util.PageLimit;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ZhangChengJun
 * Date 2021/6/21.
 * @since
 */
@Data
public class GetGiveAlarmConfig extends PageLimit  {
    @ApiModelProperty(value = "订单ID")
    private String orderId;
    @ApiModelProperty(value = "传输带名称")
    private String bandName;
}
