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
 * 设备故障维修单
 * </p>
 *
 * @author NeverEnd
 * @since 2021-09-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_repair_history_details")
@ApiModel(value="DzRepairHistoryDetails对象", description="设备故障维修单")
public class DzRepairHistoryDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "维修记录详情ID")
    @TableId(value = "repair_details_id", type = IdType.ASSIGN_ID)
    private String repairDetailsId;

    @TableField("repair_id")
    private String repairId;

    @ApiModelProperty(value = "故障位置")
    @TableField("fault_location")
    private String faultLocation;

    @ApiModelProperty(value = "故障描述")
    @TableField("fault_description")
    private String faultDescription;

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
