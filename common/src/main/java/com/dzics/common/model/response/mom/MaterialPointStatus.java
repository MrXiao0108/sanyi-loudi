package com.dzics.common.model.response.mom;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 料点状态
 *
 * @author ZhangChengJun
 * Date 2022/1/24.
 * @since
 */
@Data
public class MaterialPointStatus {

    @ApiModelProperty("料点编码")
    private String pointCode;

    @ApiModelProperty("料点状态")
    private String status;

    @ApiModelProperty("物料数量")
    private Integer quantity;
}
