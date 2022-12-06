package com.dzics.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 料点编码
 * </p>
 *
 * @author NeverEnd
 * @since 2021-11-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mom_material_point")
@ApiModel(value = "MomMaterialPoint对象", description = "料点编码")
public class MomMaterialPoint implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "material_point_id", type = IdType.ASSIGN_ID)
    private String materialPointId;


    @ApiModelProperty(value = "订单ID")
    @TableField("order_id")
    private Long orderId;

    @ApiModelProperty(value = "产线ID")
    @TableField("line_id")
    private Long lineId;

    @ApiModelProperty(value = "订单编码")
    @TableField("order_no")
    private String orderNo;

    @ApiModelProperty(value = "产线序号")
    @TableField("line_no")
    private String lineNo;

    @ApiModelProperty(value = "岛外编码")
    @TableField("external_code")
    private String externalCode;

    @ApiModelProperty("投料点区域")
    @TableField("external_region")
    private String externalRegion;

    @ApiModelProperty("线路节点")
    @TableField("line_node")
    private String lineNode;



    /**
     * 料点模式
     */
    @ApiModelProperty("料点模式, NG （NG物料） TL (退库) ,SL (上料)")
    @TableField("point_model")
    private String pointModel;

    /**
     * 是否终点工序
     */
    @TableField("next_point")
    private Boolean nextPoint;

    @ApiModelProperty(value = "岛内编码例如 A ,B ,C...")
    @TableField("in_island_code")
    private String inIslandCode;

    @ApiModelProperty("绑定工位ID")
    @TableField("station_id")
    private String stationId;

    @ApiModelProperty(value = "备注")
    @TableField("remarks_text")
    private String remarksText;

    @ApiModelProperty(value = "删除状态(0-正常,1-已删除)")
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


}
