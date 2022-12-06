package com.dzics.business.model.vo.detectordata.check;

import com.dzics.common.model.request.DBDetectTempVo;
import com.dzics.common.model.request.DzDetectTempVo;
import com.dzics.common.model.response.ProductParm;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 检测项返回参数
 *
 * @author ZhangChengJun
 * Date 2021/2/6.
 * @since
 */
@Data
public class DetectionTemplateParm implements Serializable {
    @ApiModelProperty("检测项数据")
    private List<DzDetectTempVo> dzDetectTempVos;
    @ApiModelProperty("对比检测项数据")
    private List<DBDetectTempVo> dbDetectTempVos;

    @ApiModelProperty("站点信息")
    private Object departs;

    @ApiModelProperty("已选择的站点id")
    private Long departId;

    @ApiModelProperty("产品信息")
    private List<ProductParm> products;

    @ApiModelProperty("产品已选择编号")
    private String productNo;

    /**
     * 订单Id
     */
    @ApiModelProperty("订单Id")
    private String orderId;

    @ApiModelProperty("产线类型")
    private String lineType;
}
