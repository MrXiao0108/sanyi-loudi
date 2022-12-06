package com.dzics.kanban.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dzics.kanban.enums.OperTypeStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 登陆日志
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_login_log")
@ApiModel(value = "SysLoginLog对象", description = "登陆日志")
public class SysLoginLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
//    @JsonIgnore
    private Long id;

    @ApiModelProperty(value = "用户名称")
    @TableField("user_name")
    private String userName;

    @ApiModelProperty(value = "登录主机地址")
    @TableField("oper_ip")
    private String operIp;

    @ApiModelProperty(value = "登录地点")
    @TableField("login_location")
    private String loginLocation;

    @ApiModelProperty(value = "浏览器")
    @TableField("browser")
    private String browser;

    @TableField("operating_system")
    @ApiModelProperty("操作系统")
    private String operatingSystem;

    @ApiModelProperty(value = "成功 失败")
    @TableField("login_status")
    private OperTypeStatus loginStatus;

    @ApiModelProperty(value = "响应信息")
    @TableField("login_msg")
    private String loginMsg;

    @ApiModelProperty("登录时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField("org_code")
    @ApiModelProperty("系统编码")
    private String orgCode;

}
