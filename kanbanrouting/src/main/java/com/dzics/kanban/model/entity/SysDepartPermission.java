package com.dzics.kanban.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_depart_permission")
@ApiModel(value="SysDepartPermission对象", description="")
public class SysDepartPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "站点id")
    @TableField("depart_id")
    private Long departId;

    @ApiModelProperty(value = "权限id")
    @TableField("permission_id")
    private Long permissionId;

    @ApiModelProperty(value = "删除状态（0，正常，1已删除）")
    @TableField("del_flag")
    private Boolean delFlag;

    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private String createBy;

    @ApiModelProperty(value = "创建日期")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    private String updateBy;

    @ApiModelProperty(value = "更新日期")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    public SysDepartPermission(Long departId, Long permissionId, Boolean delFlag, String createBy) {
        this.departId = departId;
        this.permissionId = permissionId;
        this.delFlag = delFlag;
        this.createBy = createBy;
    }
}
