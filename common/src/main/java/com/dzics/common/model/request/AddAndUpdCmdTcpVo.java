package com.dzics.common.model.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class AddAndUpdCmdTcpVo {
    @TableId(value = "id", type =IdType.AUTO)
    @ApiModelProperty(value = "id (修改不能为空)")
    private Long id;

    @ApiModelProperty(value = "tcp 指令名称(添加修改不能为空)")
    @NotEmpty(message = "tcp 指令名称不能为空")
    private String tcpName;

    @ApiModelProperty(value = "tcp 指令值(例如：A501 添加修改不能为空)")
    @NotEmpty(message = "tcp 指令值不能为空")
    private String tcpValue;

    @ApiModelProperty(value = "0数值类型；1状态值(添加修改不能为空)")
    @NotNull(message = "指令类型不能为空")
    @Min(value = 0,message = "指令类型,不能小于0")
    @Max(value = 1,message = "指令类型,不能大于1")
    private Integer tcpType;

    @ApiModelProperty(value = "描述（例如：关节坐标 添加修改不能为空）")
    @NotEmpty(message = "指令描述不能为空")
    private String tcpDescription;

    @ApiModelProperty(value = "指令所属设备类型:1 数控机床，2  ABB机器人，3检测设备(添加修改不能为空)")
    @NotNull(message = "指令所属设备类型不能为空")
    @Min(value = 1,message = "指令所属设备类型,不能小于1")
    @Max(value = 3,message = "指令所属设备类型,不能大于3")
    private Integer deviceType;
}
