package com.dzics.common.model.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.dzics.common.aop.Dict;
import com.dzics.common.model.write.DeviceTypeConverter;
import com.dzics.common.model.write.TcpTypeConverter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * tcp 指令标识表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_cmd_tcp")
@ApiModel(value="SysCmdTcp对象", description="tcp 指令标识表")
public class SysCmdTcp implements Serializable {

    private static final long serialVersionUID = 1L;
    @ExcelProperty(value = "设备类型",converter = DeviceTypeConverter.class)
    @ApiModelProperty(value = "设备类型 （1 数控机床，2  ABB机器人，3检测设备）")
    @TableField("device_type")
    @Dict(dicDataSource = "dz_device_type")
    private Integer deviceType;
    @ExcelProperty(value = "指令名称")
    @ApiModelProperty(value = "tcp 名称")
    @TableField("tcp_name")
    private String tcpName;

    @ExcelProperty(value = "指令类型",converter = TcpTypeConverter.class)
    @ApiModelProperty(value = "0数值类型；1状态值")
    @TableField("tcp_type")
    @Dict(dicDataSource = "dz_cmd_code")
    private Integer tcpType;

    @ExcelProperty(value = "指令值")
    @ApiModelProperty(value = "tcp 指令值(例如：A501)")
    @TableField("tcp_value")
    private String tcpValue;
    @ExcelProperty(value = "描述")
    @ApiModelProperty(value = "描述（例如：关节坐标）")
    @TableField("tcp_description")
    private String tcpDescription;


    @ExcelIgnore
    @ApiModelProperty(value = "数据类型状态值")
    @TableField("device_item_value")
    private String deviceItemValue;
    @ExcelIgnore
    @TableField("cmd_name")
    private String cmdName;
    @ExcelIgnore
    @ApiModelProperty(value = "指令组")
    @TableField("group_type")
    private String groupType;
    @ExcelIgnore
    @TableId(value = "id", type =IdType.AUTO)
    private Long id;
}
