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
 * 工序检测项目配置表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_work_detection_template")
@ApiModel(value = "DzWorkDetectionTemplate对象", description = "工序检测项目配置表")
public class DzWorkDetectionTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "work_detection_template_id", type = IdType.ASSIGN_ID)
    private String workDetectionTemplateId;

    @ApiModelProperty("工序-工件关联关系表 id")
    @TableField("work_proced_product_id")
    private String workProcedProductId;

    @ApiModelProperty("产品检测项配置id")
    @TableField("detection_id")
    private Long detectionId;

    @ApiModelProperty(value = "产品检测配置模板组id")
    @TableField("detection_group_id")
    private String detectionGroupId;

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
