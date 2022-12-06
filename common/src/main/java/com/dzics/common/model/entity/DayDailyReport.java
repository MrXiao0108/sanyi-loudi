package com.dzics.common.model.entity;

import java.math.BigDecimal;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.dzics.common.enums.EquiTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 日产报表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-06-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("day_daily_report")
@ApiModel(value="DayDailyReport对象", description="日产报表")
public class DayDailyReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "day_report_form_id", type = IdType.ASSIGN_ID)
    private String dayReportFormId;

    @ApiModelProperty(value = "产线名称")
    @TableField("lineName")
    private String linename;

    @ApiModelProperty(value = "设备类型")
    @TableField("equipmentType")
    private EquiTypeEnum equipmentType;

    @ApiModelProperty(value = "设备编码")
    @TableField("equipmentCode")
    private String equipmentcode;

    @ApiModelProperty(value = "设备名称")
    @TableField("equipmentName")
    private String equipmentname;

    @ApiModelProperty(value = "班次名称")
    @TableField("workName")
    private String workname;

    @ApiModelProperty(value = "班次时间范围")
    @TableField("time_range")
    private String timeRange;

    @ApiModelProperty(value = "班次日期")
    @TableField("workData")
    private LocalDate workdata;

    @ApiModelProperty(value = "成品数量 =产出数量 = 当前产量")
    @TableField("nowNum")
    private Long nownum;

    @ApiModelProperty(value = "毛坯数量")
    @TableField("roughNum")
    private Long roughnum;

    @ApiModelProperty(value = "合格数量")
    @TableField("qualifiedNum")
    private Long qualifiednum;

    @ApiModelProperty(value = "不良品数量")
    @TableField("badnessNum")
    private Long badnessnum;

    /**
     * 产出率
     */
    @ApiModelProperty(value = "产出率")
    @TableField("output_rate")
    private BigDecimal outputRate;

    /**
     * 合格率
     */
    @ApiModelProperty(value = "合格率")
    @TableField("pass_rate")
    private BigDecimal passRate;

    @ApiModelProperty(value = "设备id")
    @TableField("equimentId")
    private Long equimentid;

    @ApiModelProperty(value = "产线id")
    @TableField("lineId")
    private Long lineid;

    @ApiModelProperty(value = "产线序号")
    @TableField("lineNo")
    private String lineno;

    @ApiModelProperty(value = "订单号")
    @TableField("orderNo")
    private String orderno;

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
