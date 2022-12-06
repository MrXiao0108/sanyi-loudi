package com.dzics.common.model.response;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
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
    @ExcelProperty(value = "用户账号")
    @ApiModelProperty(value = "登录账号")
    private String username;
    @ExcelProperty(value = "创建人", index = 1)
    @ApiModelProperty(value = "创建人")
    private String createBy;
    @ExcelProperty(value = "用户昵称", index = 2)
    @ApiModelProperty(value = "真实姓名")
    private String realname;
    @ExcelProperty(value = "所属站点", index = 3)
    @ApiModelProperty("站点名称")
    private String departName;
    @ExcelProperty(value = "所属站点编码", index = 4)
    @ApiModelProperty(value = "所属站点编码")
    private String orgCode;
    @ApiModelProperty(value = "状态(")
    @ExcelProperty(value = "状态", index = 5)
    private String statusName;
    @ExcelProperty(value = "创建时间", index = 6)
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "主键id")
    @ExcelIgnore
    private String userId;

    @ApiModelProperty(value = "头像")
    @ExcelIgnore
    private String avatar;

    @ApiModelProperty(value = "状态(1-正常,0-冻结)")
    @ExcelIgnore
    private Integer status;

    @ApiModelProperty(value = "当前使用站点编码")
    @ExcelIgnore
    private String useOrgCode;

    @ApiModelProperty(value = "更新人")
    @ExcelIgnore
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    @ExcelIgnore
    private Date updateTime;


    public UserListRes() {
    }
    public UserListRes(String userId, String username, String realname, String avatar, Integer status,
                       String createBy, Date createTime,
                       String updateBy, Date updateTime,String orgCode) {
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
