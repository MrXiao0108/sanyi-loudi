package com.dzics.common.model.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 上报完成记录
 * </p>
 *
 * @author NeverEnd
 * @since 2021-06-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mom_progress_feedback_log")
@ApiModel(value="MomProgressFeedbackLog对象", description="请求报工日志记录")
public class MomProgressFeedbackLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "feedback_sucess_id", type = IdType.ASSIGN_ID)
    private String feedbackSucessId;

    @ApiModelProperty("工序流程详情ID")
    @TableField("process_flow_id")
    private String processFlowId;

    @ApiModelProperty(value = "请求ID")
    @TableField("reqId")
    private String reqid;

    @ApiModelProperty(value = "系统编码")
    @TableField("reqSys")
    private String reqsys;

    @ApiModelProperty(value = "工厂编号 固定值")
    @TableField("Facility")
    private String facility;

    @ApiModelProperty(value = "订单号")
    @TableField("WipOrderNo")
    private String wiporderno;

    @ApiModelProperty(value = "顺序号")
    @TableField("SequenceNo")
    private String sequenceno;

    @ApiModelProperty(value = "工序号")
    @TableField("OprSequenceNo")
    private String oprsequenceno;

    @ApiModelProperty(value = "工位")
    @TableField("WorkStation")
    private String workstation;

    /**
     * 实际开始时间
     */
    @ApiModelProperty(value = "实际开始时间")
    @TableField("ActualStartDate")
    private Date actualstartdate;

    /**
     * 时间完工时间
     */
    @ApiModelProperty(value = "时间完工时间")
    @TableField("ActualCompleteDate")
    private Date actualcompletedate;

    /**
     * 报工类型 0：开工；1：完工
     */
    @ApiModelProperty(value = "报工类型 0：开工；1：完工")
    @TableField("ProgressType")
    private String progresstype;

    @ApiModelProperty(value = "产品物料号")
    @TableField("ProductNo")
    private String productno;

    @ApiModelProperty(value = "产品二维码")
    @TableField("SerialNo")
    private String serialno;

    @ApiModelProperty(value = "合格件数")
    @TableField("Quantity")
    private BigDecimal quantity;

    @ApiModelProperty(value = "不合格件数")
    @TableField("NGQuantity")
    private BigDecimal ngquantity;

    @ApiModelProperty(value = "请求发送时间")
    @TableField("send_time")
    private Date sendTime;

    @ApiModelProperty(value = "发送请求状态码")
    @TableField("status_code")
    private Integer statusCode;

    /**
     * 是否报工成功 根据报工返回的状态字段
     */
    @TableField("res_mom_code")
    private Boolean resMomCode;

    @ApiModelProperty(value = "机构编码")
    @TableField("org_code")
    private String orgCode;

    @ApiModelProperty(value = "删除状态(0-正常,1-已删除)")
    @TableField("del_flag")
    private Boolean delFlag;

    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
