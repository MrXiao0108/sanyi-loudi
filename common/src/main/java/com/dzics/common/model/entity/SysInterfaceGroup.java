package com.dzics.common.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 接口组
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_interface_group")
@ApiModel(value = "SysInterfaceGroup对象", description = "接口组")
public class SysInterfaceGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "组id")
    @TableId(value = "group_id", type = IdType.ASSIGN_ID)
    private String groupId;

    @ApiModelProperty(value = "接口组名称", required = true)
    @TableField("group_name")
    @NotNull(message = "组名称必填")
    private String groupName;

    @ApiModelProperty(value = "接口组编码 唯一", required = true)
    @TableField("group_code")
    @NotNull(message = "组编码必填")
    private String groupCode;


}
