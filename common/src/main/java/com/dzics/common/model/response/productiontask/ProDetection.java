package com.dzics.common.model.response.productiontask;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 检测设置表格数据
 *
 * @author ZhangChengJun
 * Date 2021/2/5.
 * @since
 */
@Data
public class ProDetection<T> implements Serializable {
    @ApiModelProperty("表头")
    private List<Map<String,Object>> tableColumn;
    @ApiModelProperty("行数据")
    private T tableData;
}
