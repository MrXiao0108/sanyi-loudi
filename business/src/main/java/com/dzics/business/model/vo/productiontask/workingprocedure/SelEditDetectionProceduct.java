package com.dzics.business.model.vo.productiontask.workingprocedure;

import com.dzics.common.model.request.DzDetectTempVo;
import com.dzics.common.model.response.commons.Products;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 新增工序下的工件绑定
 *
 * @author ZhangChengJun
 * Date 2021/5/18.
 * @since
 */
@Data
public class SelEditDetectionProceduct {

    @ApiModelProperty("工序-工件关联关系ID")
    private String workProcedProductId;

    @ApiModelProperty("工件信息")
    private Products product;

    @ApiModelProperty("检测项选择数据")
    List<DzDetectTempVo> dzDetectTempVos;

}
