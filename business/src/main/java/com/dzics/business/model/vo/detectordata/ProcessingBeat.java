package com.dzics.business.model.vo.detectordata;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 加工节拍
 *
 * @author ZhangChengJun
 * Date 2021/6/2.
 * @since
 */
@Data
public class ProcessingBeat {
    @ApiModelProperty("设备ID")
    @NotEmpty(message = "请选择查看设备")
    private List<String> equipmentIdList;
}
