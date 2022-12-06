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
 * 产品组件物料表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-08-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_material")
@ApiModel(value="DzMaterial对象", description="产品组件物料表")
public class DzMaterial implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId(value = "material_id", type = IdType.ASSIGN_ID)
    private String materialId;

    @ApiModelProperty(value = "产品id")
    @TableField("product_id")
    private String productId;

    @ApiModelProperty(value = "组件物料简码")
    @TableField("material_alias")
    private String materialAlias;

    @ApiModelProperty(value = "组件物料号")
    @TableField("material_no")
    private String materialNo;

    @ApiModelProperty(value = "组件物料描述")
    @TableField("component_materials_dec")
    private String componentatMerialsDec;

    @ApiModelProperty(value = "机构编码")
    @TableField("org_code")
    private String orgCode;

    @ApiModelProperty(value = "删除状态(0正常 1删除 )")
    @TableField("del_flag")
    private Boolean delFlag;

    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "数量")
    @TableField(exist = false)
    private Integer quantity=0;


}
