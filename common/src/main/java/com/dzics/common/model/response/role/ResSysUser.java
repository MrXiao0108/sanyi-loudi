package com.dzics.common.model.response.role;

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
public class ResSysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private String id;

    @ApiModelProperty(value = "登录账号")
    private String username;

    @ApiModelProperty(value = "真实姓名")
    private String realname;

    @ApiModelProperty(value = "密码")
    @JsonIgnore
    private String password;

    @ApiModelProperty(value = "md5密码盐")
    @JsonIgnore
    private String salt;
    /**
     * 用户秘钥
     */
    @JsonIgnore
    private String secret;

    /**
     * 刷新tonken的秘钥
     */
    @JsonIgnore
    private String refSecret;


    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "生日")
    @JsonIgnore
    private Date birthday;

    @ApiModelProperty(value = "性别(0-默认未知,1-男,2-女)")
//    @JsonIgnore
    private Integer sex;

    @ApiModelProperty(value = "电子邮件")
//    @JsonIgnore
    private String email;

    @ApiModelProperty(value = "电话")
//    @JsonIgnore
    private String phone;

    @JsonIgnore
    private String orgCode;

    @ApiModelProperty("1大正用户 2其他用户")
    private Integer userIdentity;

    @ApiModelProperty("归属站点")
    private String affiliationDepartId;

    @ApiModelProperty(value = "状态(1-正常,0-冻结)")
    private Integer status;

    @ApiModelProperty(value = "当前使用站点编码")
    private String useOrgCode;

    @ApiModelProperty(value = "工号，唯一键")
    @JsonIgnore
    private String workNo;

    @ApiModelProperty(value = "删除状态(0-正常,1-已删除)")
    @JsonIgnore
    private Boolean delFlag;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
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
