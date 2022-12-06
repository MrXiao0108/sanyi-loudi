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
 * 工件制作流程记录
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_working_flow_big")
@ApiModel(value="DzWorkingFlowBig对象", description="工件制作流程记录")
public class DzWorkingFlowBig implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "工序流程ID")
    @TableId(value = "process_flow_big_id", type = IdType.ASSIGN_ID)
    private String processFlowBigId;

    @ApiModelProperty(value = "工件二维码")
    @TableField("qr_code")
    private String qrCode;

    @ApiModelProperty(value = "订单Id")
    @TableField("order_id")
    private Long orderId;

    @ApiModelProperty(value = "产线ID")
    @TableField("line_id")
    private Long lineId;

    @ApiModelProperty(value = "时间")
    @TableField("work_time")
    private Date workTime;

    @TableField("work_date")
    private LocalDate workDate;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;


    /**
     * 班次名称
     */
    @TableField("work_name")
    private String workName;


}
