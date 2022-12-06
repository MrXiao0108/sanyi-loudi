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
 * 工序-工件关联关系表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_working_procedure_product")
@ApiModel(value="DzWorkingProcedureProduct对象", description="工序-工件关联关系表")
public class DzWorkingProcedureProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "work_proced_product_id", type = IdType.ASSIGN_ID)
    private String workProcedProductId;

    @ApiModelProperty(value = "工序id")
    @TableField("working_procedure_id")
    private String workingProcedureId;

    @ApiModelProperty(value = "工件id")
    @TableField("product_id")
    private Long productId;

}
