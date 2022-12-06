package com.dzics.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 工序物料表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mom_order_path_material")
@ApiModel(value="MomOrderPathMaterial对象", description="工序物料表")
public class MomOrderPathMaterial implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "物料ID")
    @TableId(value = "material_id", type = IdType.ASSIGN_ID)
    private String materialId;

    /**
     * 订单Id
     */
    @TableField("mom_order_id")
    private String momOrderId;

    @ApiModelProperty(value = "工序ID")
    @TableField("working_procedure_id")
    private String workingProcedureId;

    @ApiModelProperty(value = "组件物料")
    @TableField("MaterialNo")
    private String materialno;

    @ApiModelProperty(value = "组件物料描述")
    @TableField("MaterialName")
    private String materialname;

    @ApiModelProperty(value = "组件物料简码")
    @TableField("MaterialAlias")
    private String materialalias;

    @ApiModelProperty(value = "组件数量")
    @TableField("Quantity")
    private Integer quantity;

    @TableField("ReserveNo")
    private String reserveno;

    @TableField("ReserveLineNo")
    private String reservelineno;

    @TableField("Unit")
    private String unit;


}
