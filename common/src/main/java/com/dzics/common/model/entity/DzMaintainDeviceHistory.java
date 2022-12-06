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
 * 保养历史记录
 * </p>
 *
 * @author NeverEnd
 * @since 2021-09-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_maintain_device_history")
@ApiModel(value="DzMaintainDeviceHistory对象", description="保养历史记录")
public class DzMaintainDeviceHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "maintain_history_id", type = IdType.ASSIGN_ID)
    private String maintainHistoryId;

    @ApiModelProperty(value = "保养设备配置ID")
    @TableField("maintain_id")
    private String maintainId;

    @ApiModelProperty(value = "设备保养时间")
    @TableField("maintain_date")
    private Date maintainDate;

    @ApiModelProperty(value = "保养人账号")
    @TableField("username")
    private String username;

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
