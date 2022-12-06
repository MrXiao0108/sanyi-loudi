package com.dzics.common.model.response.locationartifacts;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LocationArtifactsDo {
    @ApiModelProperty("工位名称")
    private String stationName;
    @ApiModelProperty("工位编号")
    private String stationCode;
    @ApiModelProperty("排序码")
    private String sortCode;
    @ApiModelProperty("工件名称")
    private String productName;
    @ApiModelProperty("工件编号")
    private String productNo;
    @ApiModelProperty("工位-工件关联关系ID")
    private String workStationProductId;
}
