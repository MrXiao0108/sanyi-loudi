package com.dzics.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalTime;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author NeverEnd
 * @since 2021-11-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_time_change_handle")
@ApiModel(value="DzTimeChangeHandle对象", description="")
public class DzTimeChangeHandle implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "change_id", type = IdType.ASSIGN_ID)
    private String changeId;

    @ApiModelProperty(value = "检测时间,检测到变化的日期")
    @TableField("detection_time")
    private Date detectionTime;

    /**
     * 上次更新监控设备状态变化时间
     */
    @TableField("up_local_date")
    private Date upLocalDate;
    /**
     * 执行顺序
     */
    @TableField("key_sort")
    private Long keySort;

    @ApiModelProperty(value = "检测日期,检测到变化的日期")
    @TableField("detection_date")
    private LocalDate detectionDate;

    @ApiModelProperty(value = "检测时分秒,检测到变化的时分秒")
    @TableField("detection_local_time")
    private LocalTime detectionLocalTime;

    @ApiModelProperty(value = "设备类型")
    @TableField("equipment_type")
    private Integer equipmentType;

    @ApiModelProperty(value = "订单")
    @TableField("order_no")
    private String orderNo;

    @ApiModelProperty(value = "产线")
    @TableField("line_no")
    private String lineNo;

    @ApiModelProperty(value = "设备编号")
    @TableField("equipment_no")
    private String equipmentNo;

    @ApiModelProperty(value = "设备ID")
    @TableField("device_id")
    private Long deviceId;

    /**
     * 1作业 2：待机 3：故障 4：关机
     */
    @ApiModelProperty(value = "设备状态")
    @TableField("work_state")
    private Integer workState;

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
