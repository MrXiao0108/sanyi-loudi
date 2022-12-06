package com.dzics.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 工序表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_working_procedure")
@ApiModel(value = "DzWorkingProcedure对象", description = "工序表")
public class DzWorkingProcedure implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "working_procedure_id", type = IdType.ASSIGN_ID)
    private String workingProcedureId;

    @ApiModelProperty("站点id")
    @TableField("depart_id")
    private Long departId;

    @ApiModelProperty(value = "订单id")
    @TableField("order_id")
    private Long orderId;

    @ApiModelProperty(value = "产线id")
    @TableField("line_id")
    private Long lineId;


    /**
     * 排序码
     */
    @TableField("sort_code")
    private Integer sortCode;

    @ApiModelProperty(value = "工序编码")
    @TableField("work_code")
    private String workCode;

    @ApiModelProperty(value = "工序名称")
    @TableField("work_name")
    private String workName;

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
