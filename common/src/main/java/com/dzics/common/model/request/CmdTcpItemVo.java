package com.dzics.common.model.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CmdTcpItemVo {
    @TableId(value = "id", type =IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id (添加操作时,该值为父级指令的id,做修改操作时,该值为当前节点的id)")
    @NotNull(message = "id不能为空")
    private Long id;

    @ApiModelProperty(value = "指令值 描述（指令值描述,如连接状态指令值:联机,脱机）")
    @NotEmpty(message = "指令值描述不能为空")
    private String tcpDescription;

    @ApiModelProperty(value = "指令值")
    @NotEmpty(message = "指令值述不能为空")
    private String deviceItemValue;
}
