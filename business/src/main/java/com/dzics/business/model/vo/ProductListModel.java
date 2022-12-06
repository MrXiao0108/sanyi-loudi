package com.dzics.business.model.vo;

import com.dzics.common.util.PageLimit;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 产品列表
 *
 * @author ZhangChengJun
 * Date 2021/7/5.
 * @since
 */
@Data
public class ProductListModel extends PageLimit {
    private String productName;
    @ApiModelProperty("产品类型")
    private String lineType;
}
