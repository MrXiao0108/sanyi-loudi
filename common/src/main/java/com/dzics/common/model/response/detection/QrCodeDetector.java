package com.dzics.common.model.response.detection;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
public class QrCodeDetector {
    @ApiModelProperty(value = "head数据")
    private DetectorHead head;

    @ApiModelProperty(value = "data数据")
    private List<DetectorDate> date;

}
