package com.dzics.common.model.request.toolinfo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class AddDzToolGroupVo {

//   private String orderNo;
//   private String lineName;
//   private String equipmentNo;
//   private Long equipmentId;

    @ApiModelProperty("站点编码")
    @NotNull(message = "站点编码不能为空")
    private Long id;
    @ApiModelProperty("刀具组编号")
    @NotNull(message = "刀具组编号不能为空")
    private Integer groupNo;
    @ApiModelProperty("刀具编号数组")
    @NotNull(message = "刀具编号数组不能为空")
    private List<Integer> toolNoList;
}
