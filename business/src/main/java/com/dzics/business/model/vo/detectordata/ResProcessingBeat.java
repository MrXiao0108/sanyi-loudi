package com.dzics.business.model.vo.detectordata;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 加工节拍
 *
 * @author ZhangChengJun
 * Date 2021/6/2.
 * @since
 */
@Data
public class ResProcessingBeat {
    @ApiModelProperty("生产数量个/分钟")
    private List<String> xAxis;
    @ApiModelProperty("设备名称")
    private List<String> yName;
}
