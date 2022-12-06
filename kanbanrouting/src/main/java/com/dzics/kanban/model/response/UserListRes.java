package com.dzics.kanban.model.response;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 用户列表数据返回
 *
 * @author ZhangChengJun
 * Date 2021/1/12.
 * @since
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user")
@ApiModel(value = "SysUser对象", description = "用户表")
public class UserListRes {


    @ApiModelProperty(value = "主键id")
    private Long userId;

    @ApiModelProperty(value = "登录账号")
    private String username;

    @ApiModelProperty(value = "真实姓名")
    private String realname;


    @ApiModelProperty(value = "头像")
    private String avatar;

    public UserListRes() {
    }

    @ApiModelProperty(value = "状态(1-正常,0-冻结)")
    private Integer status;

    @ApiModelProperty(value = "当前使用站点编码")
    private String useOrgCode;

    @ApiModelProperty(value = "所属站点编码")
    private String orgCode;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
    @ApiModelProperty("站点名称")
    private String departName;

    public UserListRes(Long userId, String username, String realname, String avatar, Integer status,
                       String createBy, Date createTime,
                       String updateBy, Date updateTime, String orgCode) {
        this.userId = userId;
        this.username = username;
        this.realname = realname;
        this.avatar = avatar;
        this.status = status;
        this.useOrgCode = useOrgCode;
        this.createBy = createBy;
        this.createTime = createTime;
        this.updateBy = updateBy;
        this.updateTime = updateTime;
        this.orgCode = orgCode;
    }
}
