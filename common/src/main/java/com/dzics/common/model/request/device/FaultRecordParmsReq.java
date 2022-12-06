package com.dzics.common.model.request.device;

import com.dzics.common.model.request.base.SearchTimeBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ZhangChengJun
 * Date 2021/9/28.
 * @since
 */
@Data
public class FaultRecordParmsReq extends SearchTimeBase {
    /**
     * 故障台账单号
     */
    @ApiModelProperty("故障台账单号")
    private String checkNumber;
    /**
     * 产线ID
     */
    @ApiModelProperty("产线ID")
    private String lineId;
    /**
     * 故障类型
     */
    @ApiModelProperty("故障类型")
    private String faultType;
    /**
     * 设备编号
     */
    @ApiModelProperty("设备编号")
    private String equipmentNo;

}
