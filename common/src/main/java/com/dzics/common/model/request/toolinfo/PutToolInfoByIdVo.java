package com.dzics.common.model.request.toolinfo;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;

@Data
public class PutToolInfoByIdVo {

   @ApiModelProperty("刀具id")
   @NotNull(message = "刀具id不能为空")
   private Long id;
    @ApiModelProperty("更改后刀具编号")
    @NotNull(message = "更改后刀具编号不能为空")
    private Integer toolNo;
}
