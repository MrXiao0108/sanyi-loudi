package com.dzics.common.model.response;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.baomidou.mybatisplus.annotation.TableField;
import com.dzics.common.model.entity.DzProduct;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ContentRowHeight(30)
@Data
public class DzProductDo extends DzProduct {
    @ApiModelProperty(value = "站点名称")
    @ExcelProperty("归属站点")
    private String departName;
}
