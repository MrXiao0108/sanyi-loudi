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
 * 待报工订单有序的是
 * </p>
 *
 * @author NeverEnd
 * @since 2022-06-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_work_report_history")
@ApiModel(value="DzWorkReportHistory对象", description="待报工订单有序的是")
public class DzWorkReportHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "订单ID")
    @TableField("pro_task_order_id")
    private String proTaskOrderId;

    @ApiModelProperty(value = "MOM订单号")
    @TableField("wip_order_no")
    private String wipOrderNo;

    @TableField("ok_ng")
    private String okNg;
    @TableField("qrCode")
    private String qrcode;

    @ApiModelProperty(value = "订单ID")
    @TableField("order_id")
    private Long orderId;

    @ApiModelProperty(value = "产线ID")
    @TableField("line_id")
    private Long lineId;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
