package com.dzics.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 待报工记录
 * </p>
 *
 * @author NeverEnd
 * @since 2022-05-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mom_order_completed")
@ApiModel(value="MomOrderCompleted对象", description="待报工记录")
public class MomOrderCompleted implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "生产订单任务id")
    @TableField("pro_task_id")
    private String proTaskId;

    @ApiModelProperty(value = "订单号")
    @TableField("wip_order_no")
    private String wipOrderNo;

    @TableField("order_id")
    private Long orderId;

    @TableField("line_id")
    private Long lineId;

    @ApiModelProperty(value = "工位id")
    @TableField("station_id")
    private String stationId;

    @ApiModelProperty(value = "产品物料号")
    @TableField("product_no")
    private String productNo;

    @ApiModelProperty(value = "1进开工 2出完工")
    @TableField("outInput_type")
    private String outinputType;

    @ApiModelProperty(value = "报工工位编号")
    @TableField("dz_station_code")
    private String dzStationCode;

    @ApiModelProperty(value = "报工工位编号")
    @TableField("dz_station_code_spare")
    private String dzStationCodeSpare;

    @ApiModelProperty(value = "工件制作流程记录 id")
    @TableField("process_flow_id")
    private String processFlowId;

    @ApiModelProperty(value = "开始时间")
    @TableField("start_time")
    private Date startTime;

    @ApiModelProperty(value = "完成时间")
    @TableField("complete_time")
    private Date completeTime;

    @ApiModelProperty(value = "二维码")
    @TableField("qr_code")
    private String qrCode;

    /**
     * 出料工位标识 1 是出料位置，0 不是出料位置
     */
    @TableField("out_flag")
    private String outFlag;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 临时设置日志使用 在报工完成后
     */
    @TableField(exist = false)
    private String logs;

    @TableField(exist = false)
    private String groupId;

    /**
     * 检测记录
     */
    @TableField(exist = false)
    private Map<String, Object> map;

    /**
     * 订单号
     */
    @TableField(exist = false)
    private String orderNo;
    /**
     * 产线号
     */
    @TableField(exist = false)
    private String lineNo;

    @TableField(exist = false)
    private String wipOrderNoReportWork;
}
