package com.dzics.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import java.time.LocalDate;
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
 * 工件制作流程记录
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_working_flow")
@ApiModel(value="DzWorkingFlow对象", description="工件制作流程记录")
public class DzWorkingFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "工序流程详情ID")
    @TableId(value = "process_flow_id", type = IdType.ASSIGN_ID)
    private String processFlowId;

    @ApiModelProperty(value = "产线ID")
    @TableField("line_id")
    private Long lineId;

    @ApiModelProperty(value = "订单id")
    @TableField("order_id")
    private Long orderId;

    @ApiModelProperty(value = "工序ID")
    @TableField("working_procedure_id")
    private String workingProcedureId;

    @ApiModelProperty(value = "工位ID")
    @TableField("station_id")
    private String stationId;

    @ApiModelProperty(value = "生产任务订单Id")
    @TableField("pro_task_id")
    private String proTaskId;

    @ApiModelProperty(value = "工件二维码")
    @TableField("qr_code")
    private String qrCode;

    @ApiModelProperty(value = "唯一订单号")
    @TableField("workpiece_code")
    private String workpieceCode;

    @ApiModelProperty(value = "开始时间")
    @TableField("start_time")
    private Date startTime;

    @ApiModelProperty(value = "生产开始 0未上报 ,1 已上报 ,3上报异常")
    @TableField("start_reporting_status")
    private String startReportingStatus;

    @ApiModelProperty(value = "生产开始 上报次数")
    @TableField("start_reporting_frequency")
    private Integer startReportingFrequency;

    @ApiModelProperty(value = "完成时间")
    @TableField("complete_time")
    private Date completeTime;

    @ApiModelProperty(value = "生产完成  0未上报 ,1 已上报 ,3上报异常")
    @TableField("complete_reporting_status")
    private String completeReportingStatus;

    @ApiModelProperty(value = "生产完成 上报次数")
    @TableField("complete_reporting_frequency")
    private Integer completeReportingFrequency;

    @ApiModelProperty(value = "备注")
    @TableField("remarks")
    private String remarks;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField("work_date")
    private LocalDate workDate;

    @ApiModelProperty("生产开始上报时间")
    @TableField("start_ropert_time")
    private Date startRopertTime;
    @ApiModelProperty("生产完成上报时间")
    @TableField("complete_ropert_time")
    private Date completeRopertTime;

}
