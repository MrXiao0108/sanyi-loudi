package com.dzics.common.model.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.dzics.common.model.write.DeviceTypeConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 通信日志
 * </p>
 *
 * @author NeverEnd
 * @since 2021-03-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_communication_log")
@ApiModel(value = "SysCommunicationLog对象", description = "通信日志")
public class SysCommunicationLog implements Serializable {

    private static final long serialVersionUID = 1L;


    @ExcelProperty("消息编号")
    @TableField("MessageId")
    private String messageid;
    @ExcelProperty("队列名称")
    @TableField("QueueName")
    private String queuename;
    @ExcelProperty("机器人编号")
    @TableField("ClientId")
    private String clientid;

    @ExcelProperty("订单编号")
    @TableField("OrderCode")
    private String ordercode;
    @ExcelProperty("产线序号")
    @TableField("LineNo")
    private String lineno;

    @ExcelProperty(value = "设备类型",converter = DeviceTypeConverter.class)
    @TableField("DeviceType")
    private String devicetype;

    @ExcelProperty("设备编码")
    @TableField("DeviceCode")
    private String devicecode;
    @ExcelProperty("消息")
    @TableField("Message")
    private String message;
    @ExcelProperty("时间")
    @TableField("Timestamp")
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date timestamp;


    @ExcelIgnore
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date createTime;
    /**
     * 检验是否通过 true 通过 false 未通过
     */
    @ExcelIgnore
    @TableField("`check`")
    private Boolean check;
    @ExcelIgnore
    @ApiModelProperty(value = "key")
    @TableId(value = "communication_key", type = IdType.ASSIGN_ID)
    private String communicationKey;
}
