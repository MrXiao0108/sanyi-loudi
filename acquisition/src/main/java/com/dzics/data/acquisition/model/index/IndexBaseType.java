package com.dzics.data.acquisition.model.index;

import com.dzics.common.enums.IndexEnumDataType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 首页socket数据类型定义
 *
 * @author ZhangChengJun
 * Date 2021/3/4.
 * @since
 */
@Data
public class IndexBaseType<T> {
    private IndexEnumDataType type;
    @ApiModelProperty("设备信息")
    T data;
}
