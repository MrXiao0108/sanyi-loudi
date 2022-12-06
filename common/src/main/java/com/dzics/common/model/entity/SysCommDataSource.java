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
 * 上传源数据
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_comm_data_source")
@ApiModel(value = "SysCommDataSource对象", description = "上传源数据")
public class SysCommDataSource implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type =IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "解析数据id")
    @TableField("data_id")
    private Long dataId;

    @TableField("command")
    private String command;

    @ApiModelProperty(value = "机构编码")
    @TableField("org_code")
    private String orgCode = "das3qedr23eas";

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

    public SysCommDataSource(String command, Date date) {
        this.createTime = date;
        this.command = command;
    }

    public SysCommDataSource() {
    }
}
