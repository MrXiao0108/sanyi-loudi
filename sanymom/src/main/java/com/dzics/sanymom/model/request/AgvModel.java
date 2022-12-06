package com.dzics.sanymom.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Classname AgvModel
 * @Description 描述
 * @Date 2022/2/22 18:42
 * @Created by NeverEnd
 */
@Data
public class AgvModel {
    @ApiModelProperty("运行模式 1 自动 0 手动")
    private Integer rm;
}
