package com.dzics.common.model.request;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PudLineVo {
    @NotNull(message = "请选择产线")
    @NotNull(message = "订单id不能为空")
    private Long orderId;

    @NotNull(message = "产线id不能为空")
    private Long id;

    @ApiModelProperty(value = "产线序号")
    @NotEmpty(message = "产线序号必填")
    private String lineNo;

    @ApiModelProperty(value = "产线编码")
    @NotEmpty(message = "产线编码不能为空")
    private String lineCode;

    @ApiModelProperty(value = "产线名称")
    @NotEmpty(message = "产线名称必填")
    @TableField("line_name")
    private String lineName;

    @ApiModelProperty(value = "备注")
    private String remarks;


    @ApiModelProperty(value = "排班对象集合")
    @NotNull(message = "每条产线至少有一个排班信息")
    private List<AddWorkShiftVo> workShiftVos;

    @ApiModelProperty(value = "产线类型(活塞杆，缸筒)")
    @NotEmpty(message = "产线类型不能为空")
    private String lineType;
}
