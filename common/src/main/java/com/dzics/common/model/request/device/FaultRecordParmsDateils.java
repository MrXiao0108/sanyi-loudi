package com.dzics.common.model.request.device;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author ZhangChengJun
 * Date 2021/9/28.
 * @since
 */
@Data
public class FaultRecordParmsDateils {
    @ApiModelProperty(value = "维修记录ID",required = true)
    @NotBlank(message = "维修记录ID必填")
    private String repairId;



}
