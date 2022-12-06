package com.dzics.common.model.response;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.dzics.common.aop.Dict;
import com.dzics.common.model.write.DeviceTypeConverter;
import com.dzics.common.model.write.TcpTypeConverter;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SysCmdTcpItemVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "数据类型状态值")
    @ApiModelProperty(value = "数据类型状态值")
    @TableField("device_item_value")
    private String deviceItemValue;
    @ExcelProperty(value = "描述")
    @ApiModelProperty(value = "描述（例如：关节坐标）")
    @TableField("tcp_description")
    private String tcpDescription;

    @ExcelIgnore
    @ApiModelProperty(value = "设备类型 （1 数控机床，2  ABB机器人，3检测设备）")
    @TableField("device_type")
    @Dict(dicDataSource = "dz_device_type")
    private Integer deviceType;
    @ExcelIgnore
    @ApiModelProperty(value = "tcp 名称")
    @TableField("tcp_name")
    private String tcpName;

    @ExcelIgnore
    @ApiModelProperty(value = "0数值类型；1状态值")
    @TableField("tcp_type")
    @Dict(dicDataSource = "dz_cmd_code")
    private Integer tcpType;

    @ExcelIgnore
    @ApiModelProperty(value = "tcp 指令值(例如：A501)")
    @TableField("tcp_value")
    private String tcpValue;
    @ExcelIgnore
    @TableField("cmd_name")
    private String cmdName;
    @ExcelIgnore
    @ApiModelProperty(value = "指令组")
    @TableField("group_type")
    private String groupType;
    @ExcelIgnore
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
}
