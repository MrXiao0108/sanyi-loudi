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
 * 保养设备配置
 * </p>
 *
 * @author NeverEnd
 * @since 2021-09-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_maintain_device")
@ApiModel(value="DzMaintainDevice对象", description="保养设备配置")
public class DzMaintainDevice implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "maintain_id", type = IdType.ASSIGN_ID)
    private String maintainId;

    @ApiModelProperty(value = "管理设备ID")
    @TableField("device_id")
    private Long deviceId;

    @ApiModelProperty("产线ID")
    @TableField("line_id")
    private Long lineId;

    @ApiModelProperty(value = "出厂日期")
    @TableField("date_of_production")
    private LocalDate dateOfProduction;

    @ApiModelProperty(value = "上一次保养日期")
    @TableField("maintain_date_before")
    private LocalDate maintainDateBefore;

    @ApiModelProperty(value = "下一次保养日期")
    @TableField("maintain_date_after")
    private LocalDate maintainDateAfter;

    @ApiModelProperty("单位倍数,一个单位的基础倍数。例如 单位是年，xx 年1 次，")
    @TableField("multiple")
    private Integer multiple;

    @ApiModelProperty("次数")
    @TableField("frequency")
    private Integer frequency;

    @ApiModelProperty(value = "单位 年 月 周")
    @TableField("unit")
    private String unit;

    @ApiModelProperty(value = "机构编码")
    @TableField("org_code")
    private String orgCode;

    @ApiModelProperty(value = "删除状态(0正常 1删除 )")
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
