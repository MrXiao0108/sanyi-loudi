package com.dzics.common.model.entity;

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
 * 设备总生产数量
 * </p>
 *
 * @author NeverEnd
 * @since 2022-06-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_equipment_pro_total_signal")
@ApiModel(value="DzEquipmentProTotalSignal对象", description="设备总生产数量")
public class DzEquipmentProTotalSignal implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "设备ID")
    @TableField("device_id")
    private Long deviceId;

    @ApiModelProperty(value = "毛坯数量")
    @TableField("rough_num")
    private Long roughNum;

    @ApiModelProperty(value = "合格数量")
    @TableField("qualified_num")
    private Long qualifiedNum;

    @ApiModelProperty(value = "当前产量")
    @TableField("now_num")
    private Long nowNum;

    @ApiModelProperty(value = "不良品数量")
    @TableField("badness_num")
    private Long badnessNum;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
