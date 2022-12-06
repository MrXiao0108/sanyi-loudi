package com.dzics.common.model.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.dzics.common.enums.OperTypeStatus;
import com.dzics.common.model.write.LoginStatusConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
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


    @ExcelProperty("用户名称")
    @ApiModelProperty(value = "用户名称")
    @TableField("user_name")
    private String userName;
    @ExcelProperty(value = "操作状态",converter = LoginStatusConverter.class)
    @ApiModelProperty(value = "成功 失败")
    @TableField("login_status")
    private OperTypeStatus loginStatus;
    @ExcelProperty("登录主机地址")
    @ApiModelProperty(value = "登录主机地址")
    @TableField("oper_ip")
    private String operIp;
    @ExcelProperty("浏览器")
    @ApiModelProperty(value = "浏览器")
    @TableField("browser")
    private String browser;
    @ExcelProperty("操作系统")
    @TableField("operating_system")
    @ApiModelProperty("操作系统")
    private String operatingSystem;
    @ExcelProperty("登录时间")
    @ApiModelProperty("登录时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ExcelIgnore
    @ApiModelProperty(value = "登录地点")
    @TableField("login_location")
    private String loginLocation;
    @ExcelIgnore
    @ApiModelProperty(value = "响应信息")
    @TableField("login_msg")
    private String loginMsg;
    @ExcelIgnore
    @TableField("org_code")
    @ApiModelProperty("系统编码")
    private String orgCode;
    @ExcelIgnore
    @TableId(value = "id", type =IdType.AUTO)
//    @JsonIgnore
    private Long id;
}
