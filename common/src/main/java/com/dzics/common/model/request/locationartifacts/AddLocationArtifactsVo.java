package com.dzics.common.model.request.locationartifacts;

import com.dzics.common.model.request.DzDetectTempVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class AddLocationArtifactsVo {
    @ApiModelProperty("工位ID")
    @NotNull(message = "工位ID必传")
    private String workingStationId;

    @ApiModelProperty("工件ID")
    @NotNull(message = "工件ID必传")
    private String productId;

    @ApiModelProperty("检测项选择数据")
    List<DzDetectTempVo> dzDetectTempVos;
}
