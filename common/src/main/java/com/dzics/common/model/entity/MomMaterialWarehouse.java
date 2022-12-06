package com.dzics.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 物料仓库
 * </p>
 *
 * @author NeverEnd
 * @since 2022-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mom_material_warehouse")
@ApiModel(value="MomMaterialWarehouse对象", description="物料仓库")
public class MomMaterialWarehouse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "订单号")
    @TableField("order_no")
    private String orderNo;

    @ApiModelProperty(value = "产线号")
    @TableField("line_no")
    private String lineNo;

    @ApiModelProperty(value = "物料编码")
    @TableField("material_no")
    private String materialNo;


    @ApiModelProperty(value = "可用物料数量")
    @TableField("quantity")
    private Long quantity;

    @ApiModelProperty(value = "在途中物料数量")
    @TableField("load_quantity")
    private Long loadQuantity;

    @ApiModelProperty(value = "总下发物料数量")
    @TableField("total_quantity")
    private Long totalQuantity;

    @ApiModelProperty(value = "已叫料完成物料数量")
    @TableField("sucess_quantity")
    private Long sucessQuantity;

    @ApiModelProperty(value = "总扣除物料数量,取消订单时扣除")
    @TableField("total_deduct")
    private Long totalDeduct;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
