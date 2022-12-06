package com.dzics.common.model.response.workStation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xnb
 * @date 2021年11月05日 13:18
 */
@Data
public class GetWorkStationDo {
    @ApiModelProperty("产线Id")
    private String lineId;
    @ApiModelProperty("工位Id")
    private String stationId;
    @ApiModelProperty("工位名称")
    private String stationName;
}
