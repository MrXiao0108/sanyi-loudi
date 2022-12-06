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
 *
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user_depart")
@ApiModel(value = "SysUserDepart对象", description = "")
public class SysUserDepart implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type =IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户id")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty(value = "站点id")
    @TableField("depart_id")
    private Long departId;

    @ApiModelProperty(value = "站点编码")
    @TableField("org_code")
    private String orgCode;

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

    public SysUserDepart(Long userId, Long departId, String orgCode, Boolean delFlag, String createBy) {
        this.userId = userId;
        this.departId = departId;
        this.orgCode = orgCode;
        this.delFlag = delFlag;
        this.createBy = createBy;
    }
    public SysUserDepart(Long userId, Long departId, String orgCode, Boolean delFlag, String createBy,String updateBy,Date updateTime) {
        this.userId = userId;
        this.departId = departId;
        this.orgCode = orgCode;
        this.delFlag = delFlag;
        this.createBy = createBy;
        this.updateBy = updateBy;
        this.updateTime = updateTime;
    }
    public SysUserDepart() {
    }
}
