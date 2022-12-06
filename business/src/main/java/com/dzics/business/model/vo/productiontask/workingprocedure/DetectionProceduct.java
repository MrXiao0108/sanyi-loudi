package com.dzics.business.model.vo.productiontask.workingprocedure;

import com.dzics.common.model.request.DzDetectTempVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 新增工序下的工件绑定
 *
 * @author ZhangChengJun
 * Date 2021/5/18.
 * @since
 */
@Data
public class DetectionProceduct {
    @ApiModelProperty("工序ID")
    @NotNull(message = "工序ID必传")
    private String workingProcedureId;

    @ApiModelProperty("工件ID")
    @NotNull(message = "工件ID必传")
    private String productId;

    @ApiModelProperty("检测项选择数据")
    List<DzDetectTempVo> dzDetectTempVos;

}
