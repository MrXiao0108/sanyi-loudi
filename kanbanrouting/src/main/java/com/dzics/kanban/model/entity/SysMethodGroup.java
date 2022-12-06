package com.dzics.kanban.model.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 方法组表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-12-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_method_group")
@ApiModel(value="SysMethodGroup对象", description="方法组表")
public class SysMethodGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "组ID")
    @NotBlank(message = "组ID不能不传")
    @TableId(value = "method_group_id", type = IdType.ASSIGN_ID)
    private String methodGroupId;

    @NotBlank(message = "方法组名称不能为空")
    @ApiModelProperty(value = "方法组名称",required = true)
    @TableField("group_name")
    private String groupName;

    @ApiModelProperty(value = "组排序值")
    @TableField("sort_code")
    private BigDecimal sortCode;


}
