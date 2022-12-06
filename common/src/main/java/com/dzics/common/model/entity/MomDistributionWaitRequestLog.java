package com.dzics.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 工序间配送请求日志记录
 * </p>
 *
 * @author NeverEnd
 * @since 2021-06-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mom_distribution_wait_request_log")
@ApiModel(value="MomDistributionWaitRequestLog对象", description="工序间配送请求日志记录")
public class MomDistributionWaitRequestLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "workpiece_distribution_id",type = IdType.ASSIGN_ID)
    private String workpieceDistributionId;

    @ApiModelProperty(value = "请求ID")
    @TableField("reqId")
    private String reqid;

    @ApiModelProperty(value = "系统编码 中控系统代号")
    @TableField("reqSys")
    private String reqsys;

    @ApiModelProperty(value = "工厂编号")
    @TableField("Facility")
    private String facility;

    @ApiModelProperty(value = "操作编码 0：下料点请求空料框，1：下料点移出满料框，2：焊接请求分拣集配来料，3：上料点空料框回缓存区")
    @TableField("reqType")
    private String reqtype;

    @ApiModelProperty(value = "料框类型 reqType为1时必须")
    @TableField("palletType")
    private String pallettype;

    @ApiModelProperty(value = "料框编码")
    @TableField("palletNo")
    private String palletno;

    @ApiModelProperty(value = "投料点编码")
    @TableField("sourceNo")
    private String sourceno;

    @ApiModelProperty(value = "需求时间 需求AGV到达时间")
    @TableField("requireTime")
    private Date requiretime;

    @ApiModelProperty(value = "发送时间  中控发送时间")
    @TableField("sendTime")
    private Date sendtime;

    @ApiModelProperty(value = "物料清单  jsonArray[]")
    @TableField("materialList")
    private String materiallist;

    @ApiModelProperty(value = "请求发送时间")
    @TableField("send_time")
    private Date sendTime;

    @ApiModelProperty(value = "1 发送成功 0 发送失败")
    @TableField("status_code")
    private Integer statusCode;

    @ApiModelProperty(value = "工序间配送 1 成功 0 错误")
    @TableField("res_mom_code")
    private Boolean resMomCode;

    @ApiModelProperty("响应内容")
    @TableField("res_msg")
    private String resMsg;

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
