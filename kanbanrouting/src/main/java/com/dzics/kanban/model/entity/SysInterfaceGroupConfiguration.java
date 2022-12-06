package com.dzics.kanban.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 接口和接口组关系
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_interface_group_configuration")
@ApiModel(value="SysInterfaceGroupConfiguration对象", description="接口和接口组关系")
public class SysInterfaceGroupConfiguration implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "接口组配置id")
    @TableId(value = "configuration_id", type = IdType.ASSIGN_ID)
    private String configurationId;

    @ApiModelProperty(value = "组id")
    @TableField("group_id")
    private String groupId;

    @ApiModelProperty(value = "接口id")
    @TableField("interface_id")
    private String interfaceId;

    @ApiModelProperty(value = "缓存时长（单位 秒）")
    @TableField("cache_duration")
    private Integer cacheDuration;

    public SysInterfaceGroupConfiguration( String groupId, String interfaceId,Integer cacheDuration) {
        this.groupId = groupId;
        this.interfaceId = interfaceId;
        this.cacheDuration = cacheDuration;
    }

    public SysInterfaceGroupConfiguration() {
    }
}
