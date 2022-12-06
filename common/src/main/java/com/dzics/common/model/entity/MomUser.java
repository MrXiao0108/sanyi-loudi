package com.dzics.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>
 * 
 * </p>
 *
 * @author NeverEnd
 * @since 2022-01-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mom_user")
@ApiModel(value="MomUser对象", description="")
public class MomUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户主键")
    @TableId(value = "user_id", type = IdType.ASSIGN_ID)
    private String userId;

    @ApiModelProperty(value = "当前登录的订单号")
    @TableField(value = "login_order_no",updateStrategy = FieldStrategy.IGNORED)
    @JsonIgnore
    private String loginOrderNo;

    @ApiModelProperty(value = "当前登录的产线")
    @TableField(value = "login_line_no",updateStrategy = FieldStrategy.IGNORED)
    @JsonIgnore
    private String loginLineNo;

    @ApiModelProperty(value = "员工号")
    @TableField("employee_no")
    private String employeeNo;

    @ApiModelProperty(value = "员工姓名")
    @TableField("employee_name")
    private String employeeName;

    @ApiModelProperty(value = "最后更新 数据操作类型 增：Insert  删：Delete  改：Update")
    @TableField("emp_status")
    @JsonIgnore
    private String empStatus;

    @ApiModelProperty(value = "预留参数1")
    @TableField("paramRsrv1")
    @JsonIgnore
    private String paramrsrv1;

    @ApiModelProperty(value = "预留参数2")
    @TableField("paramRsrv2")
    @JsonIgnore
    private String paramrsrv2;

    @ApiModelProperty(value = "预留参数3")
    @TableField("paramRsrv3")
    @JsonIgnore
    private String paramrsrv3;

    @ApiModelProperty(value = " 1 已经登录,NULL 以退出")
    @TableField(value = "login_state",updateStrategy = FieldStrategy.IGNORED)
    @JsonIgnore
    private Boolean loginState;

    @ApiModelProperty(value = "删除状态(0-正常,1-已删除)")
    @TableField("del_flag")
    @JsonIgnore
    private Boolean delFlag;

    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    @JsonIgnore
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonIgnore
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    @JsonIgnore
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
