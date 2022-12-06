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
public class GetDeivceAlarmConfig extends PageLimit {
    @ApiModelProperty(value = "订单ID")
    private String orderId;

    @ApiModelProperty("产线ID")
    private String lineId;

    @ApiModelProperty("设备ID")
    private String deivceId;

    @ApiModelProperty("告警等级")
    private Integer alarmGrade;

    @ApiModelProperty("设备编号")
    private String  equipmentNo;

}
