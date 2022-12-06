package com.dzics.common.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 订单工序组工序组
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mom_order_path")
@ApiModel(value = "MomOrderPath对象", description = "订单工序组工序组")
public class MomOrderPath implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "working_procedure_id", type = IdType.ASSIGN_ID)
    private String workingProcedureId;

    @ApiModelProperty(value = "mom 订单 id")
    @TableField("mom_order_id")
    private String momOrderId;

    /**
     * 工序号
     */
    @ApiModelProperty(value = "工序号")
    @TableField("OprSequenceNo")
    private String oprsequenceno;

    @ApiModelProperty(value = "顺序号 用来定义总装线与分装线的关系，如果没有分装线，给的是000000")
    @TableField("SequenceNo")
    private String sequenceno;

    @ApiModelProperty(value = "工序名称")
    @TableField("OprSequenceName")
    private String oprsequencename;

    @ApiModelProperty(value = "工序类型 自制/外协")
    @TableField("OprSequenceType")
    private String oprsequencetype;

    @ApiModelProperty(value = "计划开始时间")
    @TableField("ScheduledStartDate")
    private Date scheduledstartdate;

    @ApiModelProperty(value = "计划结束时间")
    @TableField("ScheduledCompleteDate")
    private Date scheduledcompletedate;

    @ApiModelProperty(value = "工作中心")
    @TableField("WorkCenter")
    private String workcenter;

    @ApiModelProperty(value = "工作中心描述")
    @TableField("WorkCenterName")
    private String workcentername;

    @ApiModelProperty(value = "工位")
    @TableField("WorkStation")
    private String workstation;

    @ApiModelProperty(value = "工位描述")
    @TableField("WorkStationName")
    private String workstationname;

    @ApiModelProperty(value = "工序状态 110已下达 120已派工 121派工失败 125已接收 130进行中 135暂停 140已完工 150已报工 155报工失败 160取消报工 170已删除")
    @TableField("ProgressStatus")
    private Integer progressstatus;

    @ApiModelProperty(value = "工序数量 工序产出物的数量")
    @TableField("Quantity")
    private Integer quantity;

    @TableField(exist = false)
    private List<MomOrderPathMaterial> momOrderPathMaterials;

}
