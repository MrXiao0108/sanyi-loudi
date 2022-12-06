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
 * 
 * </p>
 *
 * @author NeverEnd
 * @since 2021-12-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("wms_order_config")
@ApiModel(value="WmsOrderConfig对象", description="")
public class WmsOrderConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "config_order_id", type = IdType.ASSIGN_ID)
    private String configOrderId;

    @ApiModelProperty(value = "RFID信息")
    @TableField("rfid")
    private String rfid;

    @ApiModelProperty(value = "订单号")
    @TableField("order_num")
    private String orderNum;

    @ApiModelProperty(value = "物料号")
    @TableField("material_code")
    private String materialCode;

    @TableField("order_status")
    @ApiModelProperty("是否完成")
    private Boolean orderStatus;
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


}
