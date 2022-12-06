package com.dzics.common.model.entity;

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
 * 系统运行模式
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_config")
@ApiModel(value="SysConfig对象", description="系统运行模式")
public class SysConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    @TableField("tableName")
    private String tablename;

    @TableField("planDay")
    private String planday;

    @TableField("runDataModel")
    private String rundatamodel;

    @ApiModelProperty(value = "1系统运行模式")
    @TableField("type_config")
    private String typeConfig;

    @ApiModelProperty(value = "配置值 运行模式中 1 脉冲 ，0 累计数量")
    @TableField("config_value")
    private String configValue;


}
