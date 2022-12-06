package com.dzics.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
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
 * @since 2022-01-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("log_prompt_msg")
@ApiModel(value = "LogPromptMsg对象", description = "")
public class LogPromptMsg implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "log_id", type = IdType.ASSIGN_ID)
    private String logId;

    @ApiModelProperty("0 未处理 1 已处理")
    @TableField("handle")
    @JsonIgnore
    private Integer handle;
    @TableField("req_type")
    @ApiModelProperty("日志类型")
    @JsonIgnore
    private String reqType;

    @TableField("group_id")
    @ApiModelProperty("同组ID")
    private String groupId;
    /**
     * 调用参数
     */
    @ApiModelProperty("请求参数")
    @TableField("invok_parm")
    private String invokParm;
    /**
     * 调用方法
     */
    @TableField("invok_method")
    @JsonIgnore
    private String invokMethod;
    /**
     * 调用返回异常信息
     */
    @TableField("invok_return")
    private String invokReturn;
    /**
     * 调用路径
     */
    @TableField("invok_point_model")
    @JsonIgnore
    private String invokPointModel;

    @TableField("point_code")
    @ApiModelProperty("料点编码")
    private String pointCode;
    /**
     * 开始时间
     */
    @TableField("start_time")
    @ApiModelProperty("开始时间")
    private Date startTime;
    /**
     * 结束时间
     */
    @TableField("end_time")
    @ApiModelProperty("结束时间")
    private Date endTime;
    /**
     * 调用耗时
     */
    @TableField("invok_cost")
    @ApiModelProperty("调用耗时")
    private BigDecimal invokCost;
    /**
     * 调用状态 Y 成功 N失败
     */
    @TableField("invok_status")
    @ApiModelProperty("Y 成功 N失败")
    private String invokStatus;

    @TableField("order_no")
    @JsonIgnore
    private String orderNo;

    @TableField("line_no")
    @JsonIgnore
    private String lineNo;


    @ApiModelProperty("错误次数")
    @TableField("errors_nums")
    private Integer errorsNums;

    @ApiModelProperty(value = "等级")
    @TableField("grade")
    @JsonIgnore
    private Integer grade;

    @ApiModelProperty(value = "简介")
    @TableField("brief")
    @JsonIgnore
    private String brief;

    @ApiModelProperty(value = "详情")
    @TableField("details")
    private String details;

    @ApiModelProperty(value = "日期")
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
    private Date updateTime;


    /**
     * 当前生产订单id
     */
    @TableField("pro_task_order_id")
    @JsonIgnore
    private String proTaskOrderId;
    /**
     * 叫料订单id
     */
    @TableField("call_material_pro_task_order_id")
    @JsonIgnore
    private String callMaterialProTaskOrderId;

    /**
     *生产任务订单号
     */
    @TableField("wip_order_no")
    @ApiModelProperty("生产任务订单号")
    private String wipOrderNo;

    /**
     *叫料订单号
     */
    @TableField("call_material_wip_order_no")
    @JsonIgnore
    private String callMaterialWipOrderNo;
}
