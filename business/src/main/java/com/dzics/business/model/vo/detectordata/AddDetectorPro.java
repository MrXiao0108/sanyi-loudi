package com.dzics.business.model.vo.detectordata;

import com.dzics.common.model.request.DzDetectTempVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 新增检测项参数
 *
 * @author ZhangChengJun
 * Date 2021/2/5.
 * @since
 */
@Data
public class AddDetectorPro {
    /**
     * 产线号
     */
    @ApiModelProperty("产线编号")
    @NotBlank(message = "产线编号必传")
    private String lineNo;

    /**
     * 订单号
     */
    @ApiModelProperty("订单编号")
    @NotBlank(message = "订单编号必传")
    private String orderNo;

    /**
     * 订单ID
     */
    @ApiModelProperty("订单ID")
    @NotBlank(message = "订单ID必传")
    private String orderId;
    /**
     * 产线ID
     */
    @ApiModelProperty("产线ID")
    @NotBlank(message = "产线ID必传")
    private String lineId;

    @ApiModelProperty("站点ID")
    @NotNull(message = "站点必选")
    private String departId;

    @ApiModelProperty("产品编号")
    @NotBlank(message = "产品必选")
    private String productNo;

    @NotEmpty(message = "数据检测项必填")
    List<DzDetectTempVo> detectionTemplates;
}
