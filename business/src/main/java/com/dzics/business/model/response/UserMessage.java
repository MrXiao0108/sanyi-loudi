package com.dzics.business.model.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.dzics.business.model.vo.depart.ResDepart;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ZhangChengJun
 * Date 2021/1/7.
 */
@Data
public class UserMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "主键id")
    private String userId;

    @ApiModelProperty(value = "登录账号")
    private String username;

    @ApiModelProperty(value = "真实姓名")
    private String realname;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "状态(1-正常,0-冻结)")
    private Integer status;

    @ApiModelProperty(value = "归属站点信息")
    private ResDepart sysDepart;

    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty("1大正用户 2直属大正站点用户,3 其他用户")
    private Integer userIdentity;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty("归属站点")
    private String affiliationDepartId;

    @ApiModelProperty("归属站点编码")
    private String orgCode;

    @ApiModelProperty(value = "当前使用站点编码")
    private String useOrgCode;

    @ApiModelProperty("所属角色")
    private String roleName;
}
