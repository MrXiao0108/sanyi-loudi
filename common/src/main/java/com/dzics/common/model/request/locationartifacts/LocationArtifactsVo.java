package com.dzics.common.model.request.locationartifacts;

import com.dzics.common.util.PageLimit;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LocationArtifactsVo extends PageLimit {
    @ApiModelProperty(value = "工位ID",required = true)
    @NotNull(message = "工位Id必传")
    private String workingStationId;

    @ApiModelProperty("工件编号")
    private String productNo;
}
