package com.dzics.common.model.request.device.maintain;

import com.dzics.common.model.response.device.maintain.MaintainRecordDetails;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 新增保养记录
 *
 * @author ZhangChengJun
 * Date 2021/9/29.
 * @since
 */
@Data
public class AddMaintainRecord {
    @ApiModelProperty(value = "保养设备ID", required = true)
    @NotBlank(message = "保养设备ID必填")
    private String maintainId;

    @ApiModelProperty(value = "保养项、内容", required = true)
    List<MaintainRecordDetails> recordDetails;
}
