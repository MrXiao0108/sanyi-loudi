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
 * 工位-工件关联关系表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_working_station_product")
@ApiModel(value="DzWorkingStationProduct对象", description="工位-工件关联关系表")
public class DzWorkingStationProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "work_station_product_id", type = IdType.ASSIGN_ID)
    private String workStationProductId;

    @ApiModelProperty(value = "工位id")
    @TableField("working_station_id")
    private String workingStationId;

    @ApiModelProperty(value = "工件id")
    @TableField("product_id")
    private Long productId;


}
