package com.dzics.kanban.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user")
@ApiModel(value = "SysUser对象", description = "用户表")
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "登录账号")
    @TableField("username")
    private String username;

    @ApiModelProperty(value = "真实姓名")
    @TableField("realname")
    private String realname;

    @ApiModelProperty(value = "密码")
    @TableField("password")
    @JsonIgnore
    private String password;

    @ApiModelProperty(value = "md5密码盐")
    @TableField("salt")
    @JsonIgnore
    private String salt;
    /**
     * 用户秘钥
     */
    @TableField("secret")
    @JsonIgnore
    private String secret;

    /**
     * 刷新tonken的秘钥
     */
    @TableField("ref_secret")
    @JsonIgnore
    private String refSecret;


    @ApiModelProperty(value = "头像")
    @TableField("avatar")
    private String avatar;

    @ApiModelProperty(value = "生日")
    @TableField("birthday")
    @JsonIgnore
    private Date birthday;

    @ApiModelProperty(value = "性别(0-默认未知,1-男,2-女)")
    @TableField("sex")
//    @JsonIgnore
    private Integer sex;

    @ApiModelProperty(value = "电子邮件")
    @TableField("email")
//    @JsonIgnore
    private String email;

    @ApiModelProperty(value = "电话")
    @TableField("phone")
//    @JsonIgnore
    private String phone;

    @TableField("org_code")
    @JsonIgnore
    private String orgCode;

    @ApiModelProperty("1大正用户 2其他用户")
    @TableField("user_identity")
    private Integer userIdentity;

    @ApiModelProperty("归属站点")
    @TableField("affiliation_depart_id")
    private Long affiliationDepartId;

    @ApiModelProperty(value = "状态(1-正常,0-冻结)")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "当前使用站点编码")
    @TableField("use_org_code")
    private String useOrgCode;

    @ApiModelProperty(value = "工号，唯一键")
    @TableField("work_no")
    @JsonIgnore
    private String workNo;

    @ApiModelProperty(value = "删除状态(0-正常,1-已删除)")
    @TableField("del_flag")
    @JsonIgnore
    private Boolean delFlag;

    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    @ApiModelProperty(value = "角色名称")
    @TableField(exist = false)
    private String roleName;

    /**
     * 归属站点编码
     */
    @TableField(exist = false)
    private String code;
}
