package com.dzics.sanymom.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author ZhangChengJun
 * Date 2022/1/11.
 * @since
 */
@Data
public class OpenWork {
    @ApiModelProperty(value = "true 开启 false 关闭")
    @NotNull(message = "选择 1 开启 0 关闭")
    private Integer openClose;
}
