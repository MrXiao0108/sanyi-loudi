package com.dzics.common.model.response.productiontask.workingProcedure;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ZhangChengJun
 * Date 2021/5/18.
 * @since
 */
@Data
public class SelProcedureProduct {
    @ApiModelProperty("工序名称")
    private String workName;
    @ApiModelProperty("工序编号")
    private String workCode;
    @ApiModelProperty("工件名称")
    private String productName;
    @ApiModelProperty("工件编号")
    private String productNo;
    @ApiModelProperty("工序-工件关联关系ID")
    private String workProcedProductId;
}
