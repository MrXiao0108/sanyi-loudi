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
 * 设备生产数量详情表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_equipment_pro_num_details_signal")
@ApiModel(value="DzEquipmentProNumDetailsSignal对象", description="设备生产数量详情表")
public class DzEquipmentProNumDetailsSignal implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type =IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "每日排班id")
    @TableField("day_id")
    private Long dayId;

    @ApiModelProperty(value = "设备类型")
    @TableField("device_type")
    private String deviceType;

    @ApiModelProperty(value = "订单编号")
    @TableField("order_no")
    private String orderNo;

    @ApiModelProperty(value = "产线序号")
    @TableField("line_no")
    private String lineNo;

    @ApiModelProperty(value = "设备序号")
    @TableField("equipment_no")
    private String equipmentNo;

    @ApiModelProperty(value = "型号")
    @TableField("model_number")
    private String modelNumber;

    @ApiModelProperty(value = "批次号")
    @TableField("batch_number")
    private String batchNumber;

    @ApiModelProperty(value = "生产数量")
    @TableField("work_num")
    private Long workNum;

    @ApiModelProperty(value = "生产总量")
    @TableField("total_num")
    private Long totalNum;

    @ApiModelProperty(value = "毛坯数量")
    @TableField("rough_num")
    private Long roughNum;

    @TableField("total_rough_num")
    private Long totalRoughNum;

    @ApiModelProperty(value = "合格数量")
    @TableField("qualified_num")
    private Long qualifiedNum;

    @TableField("total_qualified_num")
    private Long totalQualifiedNum;

    @ApiModelProperty(value = "不良品数量")
    @TableField("badness_num")
    private Long badnessNum;

    @ApiModelProperty(value = "不良品总数量")
    @TableField("total_badness_num")
    private Long totalBadnessNum;

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
