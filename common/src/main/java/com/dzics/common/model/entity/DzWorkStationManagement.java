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
 * 工位表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_work_station_management")
@ApiModel(value="DzWorkStationManagement对象", description="工位表")
public class DzWorkStationManagement implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "工位id")
    @TableId(value = "station_id", type = IdType.ASSIGN_ID)
    private String stationId;

    /**
     * 订单ID
     */
    @ApiModelProperty("订单Id")
    @TableField("order_id")
    private Long orderId;

    /**
     * 产线Id
     */
    @TableField("line_id")
    @ApiModelProperty("产线Id")
    private Long lineId;

    @ApiModelProperty(value = "工序id")
    @TableField("working_procedure_id")
    private String workingProcedureId;

    @ApiModelProperty(value = "工位名称")
    @TableField("station_name")
    private String stationName;

    @TableField("dz_station_code")
    private String dzStationCode;
    /**
     * 看板 1展示0不展示 改工位
     */
    @ApiModelProperty("看板 1展示0不展示 ")
    @TableField("on_off")
    private Integer onOff;

    @ApiModelProperty(value = "排序码")
    @TableField("sort_code")
    private Integer sortCode;

    @ApiModelProperty(value = "是否NG工位")
    @TableField("ng_code")
    private String  ngCode;
    /**
     * 合并工位标志 merge_code
     */
    @TableField("merge_code")
    private String mergeCode;

    @TableField("out_flag")
    @ApiModelProperty(value = "出料位置标记 1 是出料位置，0 不是出料位置")
    private String outFlag;

    @ApiModelProperty(value = "工位编号")
    @TableField("station_code")
    private String stationCode;



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
