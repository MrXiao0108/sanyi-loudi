package com.dzics.common.model.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.dzics.common.aop.Dict;
import com.dzics.common.model.entity.SysCmdTcp;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SelectCmdTcpVo {


    @TableId(value = "id", type =IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id ")
    private Long id;

    @ApiModelProperty(value = "tcp 指令名称")
    private String tcpName;

    @ApiModelProperty(value = "tcp 指令值(例如：A501 )")
    private String tcpValue;

    @ApiModelProperty(value = "0数值类型；1状态值")
    private Integer tcpType;

    @ApiModelProperty(value = "描述")
    private String tcpDescription;

    @ApiModelProperty(value = "1 数控机床，2  ABB机器人，3检测设备(添加修改不能为空)")
    private Integer deviceType;


}
