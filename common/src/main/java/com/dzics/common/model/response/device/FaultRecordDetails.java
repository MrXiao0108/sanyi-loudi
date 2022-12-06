package com.dzics.common.model.response.device;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/9/28.
 * @since 设备故障记录详情
 */
@Data
public class FaultRecordDetails {
    @ApiModelProperty("修单详情列表")
    List<FaultRecordDetailsInner> detailsInner;
    @ApiModelProperty("备注")
    private String remarks;
    @ApiModelProperty("开始处理时间")
    private String startHandleDate;
    @ApiModelProperty("处理完成时间")
    private String completeHandleDate;
}
