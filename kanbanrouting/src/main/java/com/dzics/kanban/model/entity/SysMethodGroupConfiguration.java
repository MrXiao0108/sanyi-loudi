package com.dzics.kanban.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 方法和方法组关系表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-12-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_method_group_configuration")
@ApiModel(value="SysMethodGroupConfiguration对象", description="方法和方法组关系表")
public class SysMethodGroupConfiguration implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "关系ID")
    @TableId(value = "group_configuration_id", type = IdType.ASSIGN_ID)
    private String groupConfigurationId;

    @ApiModelProperty(value = "方法ID")
    @TableField("method_id")
    private String methodId;

    @ApiModelProperty(value = "方法组ID")
    @TableField("group_id")
    private String groupId;


}
