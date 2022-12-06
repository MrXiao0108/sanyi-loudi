package com.dzics.business.model.vo.detectordata.edit;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 修改产品检测配置参数
 *
 * @author ZhangChengJun
 * Date 2021/2/8.
 * @since
 */
@Data
public class EditProDuctTemp {
    @NotNull(message = "站点必选")
    private Long departId;

    @NotBlank(message = "产品必选")
    private String productNo;

    @ApiModelProperty("检测编辑信息")
    private List<DbProDuctileEditer> dzDetectTempVos;

    @ApiModelProperty("对比值设置")
    private List<DbProDuctileEditer> dbDetectTempVos;
}
