package com.dzics.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
 * @since 2021-12-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("wms_callframe_history")
@ApiModel(value="WmsCallframeHistory对象", description="")
public class WmsCallframeHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "req_id", type = IdType.ASSIGN_ID)
    @JsonIgnore
    private String reqId;

    @ApiModelProperty("发送时间")
    @TableField("sending_time")
    private Date sendingTime;

    @ApiModelProperty("响应时间")
    @TableField("response_time")
    private Date responseTime;

    @ApiModelProperty(value = "接口类型 CallFrame 叫框接料  LocationRequest  机械手放货位置申请 OrderCompleted 订单完成信号 PutCompleted 放料完成")
    @TableField("Interface_type")
    private String interfaceType;

    @ApiModelProperty(value = "RFID信息")
    @TableField("rfid")
    @JsonIgnore
    private String rfid;

    @ApiModelProperty(value = "订单号")
    @TableField("orderNum")
    private String ordernum;

    @ApiModelProperty(value = "物料号")
    @TableField("materialCode")
    private String materialcode;

    @ApiModelProperty(value = "接收状态")
    @TableField("res_status")
    private Boolean resStatus;

    @ApiModelProperty(value = "结果描述")
    @TableField("res_message")
    private String resMessage;

    @ApiModelProperty(value = "下料点")
    @TableField("res_station")
    private String resStation;

    @ApiModelProperty(value = "是否允许放料")
    @TableField("res_allowed")
    @JsonIgnore
    private Boolean resAllowed;


}
