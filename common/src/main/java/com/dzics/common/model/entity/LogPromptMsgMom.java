package com.dzics.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author NeverEnd
 * @since 2022-01-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("log_prompt_msg_mom")
@ApiModel(value = "LogPromptMsgMom对象", description = "")
public class LogPromptMsgMom implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "log_id", type = IdType.ASSIGN_ID)
    @JsonIgnore
    private String logId;

    /**
     * 调用MOM 请求ID
     */
    @TableField("req_id")
    private String reqId;

    @TableField("task_id")
    private String taskId;

    @ApiModelProperty(value = "日志类型 ")
    @TableField("req_type")
    @JsonIgnore
    private String reqType;

    /**
     * 内部同组ID
     */
    @TableField("inner_group_id")
    @ApiModelProperty("内部同组ID")
    private String innerGroupId;

    @ApiModelProperty(value = "组ID")
    @TableField("group_id")
    @JsonIgnore
    private String groupId;

    @TableField("wip_order_no")
    private String wipOrderNo;

    @ApiModelProperty(value = "订单")
    @TableField("order_no")
    @JsonIgnore
    private String orderNo;

    @ApiModelProperty(value = "产线")
    @TableField("line_no")
    @JsonIgnore
    private String lineNo;

    @ApiModelProperty(value = "等级")
    @TableField("grade")
    @JsonIgnore
    private Integer grade;

    @ApiModelProperty(value = "简介")
    @TableField("brief")
    @JsonIgnore
    private String brief;

    @ApiModelProperty(value = "接口名称")
    @TableField("details")
    private String details;

    @ApiModelProperty(value = "调用方法")
    @TableField("invok_method")
    @JsonIgnore
    private String invokMethod;

    @ApiModelProperty(value = "调用返回异常信息")
    @TableField("invok_return")
    private String invokReturn;

    @ApiModelProperty(value = "调用参数")
    @TableField("invok_parm")
    private String invokParm;

    @ApiModelProperty(value = "调用路径")
    @TableField("invok_point_model")
    @JsonIgnore
    private String invokPointModel;

    @ApiModelProperty(value = "请求料点编码")
    @TableField("point_code")
    private String pointCode;

    @ApiModelProperty(value = "开始时间")
    @TableField("start_time")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    @TableField("end_time")
    private Date endTime;

    @ApiModelProperty(value = "调用耗时")
    @TableField("invok_cost")
    private BigDecimal invokCost;

    @ApiModelProperty(value = "调用状态 Y 成功 N失败")
    @TableField("invok_status")
    private String invokStatus;

    @ApiModelProperty(value = "创建日期")
    @TableField("create_date")
    private LocalDate createDate;

    @ApiModelProperty(value = "机构编码")
    @TableField("org_code")
    @JsonIgnore
    private String orgCode;

    @ApiModelProperty(value = "删除状态(0正常 1删除 )")
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
    @JsonIgnore
    private Date updateTime;


}
