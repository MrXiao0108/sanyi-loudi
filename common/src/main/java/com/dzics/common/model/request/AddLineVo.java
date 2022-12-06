package com.dzics.common.model.request;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel("新增产线vo")
@Data
public class AddLineVo {
    private static final long serialVersionUID = 1L;

//    @TableId(value = "id", type =IdType.ASSIGN_ID)
//    private Long id;

    @ApiModelProperty(value = "订单id")
    @NotNull(message = "请选择订单")
    @TableField("order_id")
    private Long orderId;

    @ApiModelProperty(value = "产线序号")
    @NotEmpty(message = "产线序号不能为空")
    @TableField("lineNo")
    private String lineNo;

    @ApiModelProperty(value = "产线编码")
    @NotEmpty(message = "产线编码不能为空")
    @TableField("line_code")
    private String lineCode;

    @ApiModelProperty(value = "产线名称")
    @NotEmpty(message = "产线名称不能为空")
    @TableField("line_name")
    private String lineName;

    @ApiModelProperty(value = "备注")
    @TableField("remarks")
    private String remarks;


    @ApiModelProperty(value = "排班对象集合")
    @NotNull(message = "每条产线至少有一个排班信息")
    private List<AddWorkShiftVo> workShiftVos;

    @ApiModelProperty(value = "产线类型(活塞杆，缸筒)")
    @NotBlank(message = "产线类型不能为空")
    @TableField("line_type")
    private String lineType;









}
