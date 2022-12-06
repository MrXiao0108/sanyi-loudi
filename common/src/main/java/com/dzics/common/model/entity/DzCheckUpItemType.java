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
 * 巡检类型绑定
 * </p>
 *
 * @author NeverEnd
 * @since 2021-09-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_check_up_item_type")
@ApiModel(value="DzCheckUpItemType对象", description="巡检类型绑定")
public class DzCheckUpItemType implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "check_type_id", type =IdType.ASSIGN_ID)
    private String checkTypeId;

    @ApiModelProperty(value = "设备类型(1检测设备,2机床,3机器人)")
    @TableField("device_type")
    private Integer deviceType;

    @ApiModelProperty(value = "巡检设备类型ID")
    @TableField("check_item_id")
    private String checkItemId;

    @ApiModelProperty(value = "字典值表ID")
    @TableField("dict_item_id")
    private Long dictItemId;

    @ApiModelProperty(value = "字典编码")
    @TableField("dict_code")
    private String dictCode;

    @ApiModelProperty(value = "是否选中")
    @TableField("checked")
    private Boolean checked;

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
