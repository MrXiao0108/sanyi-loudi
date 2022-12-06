package com.dzics.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.dzics.common.model.custom.CmdTcp;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 班次生产记录表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_equipment_pro_num_signal")
@ApiModel(value="DzEquipmentProNumSignal对象", description="班次生产记录表")
public class DzEquipmentProNumSignal implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type =IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "每日排班id")
    @TableField("day_id")
    private Long dayId;

    @ApiModelProperty(value = "订单编号")
    @TableField("order_no")
    private String orderNo;

    @ApiModelProperty(value = "产线序号")
    @TableField("line_no")
    private String lineNo;

    @ApiModelProperty(value = "设备id")
    @TableField("equiment_id")
    private Long equimentId;

    @ApiModelProperty(value = "批次号")
    @TableField("batch_number")
    private String batchNumber;

    @ApiModelProperty(value = " 产品类型")
    @TableField("product_type")
    private String productType;

    @ApiModelProperty(value = " 产品编码")
    @TableField("model_number")
    private String modelNumber;

    @ApiModelProperty(value = "毛坯数量")
    @TableField("rough_num")
    private Long roughNum;

    /**
     * 合格数量
     */
    @ApiModelProperty(value = "合格数量")
    @TableField("qualified_num")
    private Long qualifiedNum;

    @ApiModelProperty(value = "当前产量")
    @TableField("now_num")
    private Long nowNum;

    @ApiModelProperty(value = "生产总量")
    @TableField("total_num")
    private Long totalNum;

    @ApiModelProperty(value = "不良品数量")
    @TableField("badness_num")
    private Long badnessNum;

    @ApiModelProperty(value = "班次日期")
    @TableField("work_data")
    private LocalDate workData;

    @TableField("work_year")
    private Integer workYear;

    @TableField("work_mouth")
    private String workMouth;

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

    @ApiModelProperty("小时")
    @TableField("work_hour")
    private Integer workHour;

    /**
     * 指令信息
     */
    @TableField(exist = false)
    private CmdTcp cmdTcp;
    /**
     * 接收消息时间
     */
    @TableField(exist = false)
    private Long sendSignalTime;
}
