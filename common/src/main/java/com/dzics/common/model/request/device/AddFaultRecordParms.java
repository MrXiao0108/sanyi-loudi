package com.dzics.common.model.request.device;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/9/28.
 * @since
 */
@Data
public class AddFaultRecordParms {
    @ApiModelProperty(value = "维修记录ID",required = true)
    private String repairId;
    /**
     * 产线ID
     */
    @ApiModelProperty(value = "产线ID", required = true)
    @NotBlank(message = "产线必选")
    private String lineId;

    @ApiModelProperty(required = true, value = "设备ID")
    @NotBlank(message = "设备ID必填")
    private String deviceId;
    /**
     * 故障类型
     */
    @ApiModelProperty(value = "故障类型", required = true)
    @NotBlank(message = "故障类型必填")
    private String faultType;

    @ApiModelProperty(value = "开始处理时间", required = true)
    @JsonFormat(pattern = "yyy-MM-dd hh:mm:ss")
    @DateTimeFormat(pattern = "yyy-MM-dd hh:mm:ss")
    private Date startHandleDate;

    @ApiModelProperty(value = "处理完成时间", required = true)
    @JsonFormat(pattern = "yyy-MM-dd hh:mm:ss")
    @DateTimeFormat(pattern = "yyy-MM-dd hh:mm:ss")
    private Date completeHandleDate;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @ApiModelProperty("维修详情")
    private List<AddFaultRecordParmsInner> parmsInners;
}
