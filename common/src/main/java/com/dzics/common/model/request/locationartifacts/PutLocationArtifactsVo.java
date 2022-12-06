package com.dzics.common.model.request.locationartifacts;

import com.dzics.common.model.request.DzDetectTempVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PutLocationArtifactsVo {
    @ApiModelProperty(value = "工位-工件关联关系ID",required = true)
    @NotNull(message = "工位-工件关联关系ID")
    private String workStationProductId;

    @ApiModelProperty(value = "工件ID",required = true)
    @NotNull(message ="工件ID必传")
    private String productId;

    @ApiModelProperty("检测项选择数据")
    List<DzDetectTempVo> dzDetectTempVos;
}
