package com.dzics.common.model.frid;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * FRDI 扫码记录
 */
@Data
public class FridJson {
    /**
     * mom 订单号
     */
    @ApiModelProperty(value = "mom 订单号")
    private String momOrderNo;

    /**
     * 物料号
     */
    @ApiModelProperty(value = "物料号")
    private String materialNo;

    /**
     * 工序号
     */
    @ApiModelProperty(value = "工序号")
    private String workNo;

    /**
     * 料框编号
     */
    @ApiModelProperty(value = "料框编号")
    private String palletNo;

    /**
     * 物料数量
     */
    @ApiModelProperty(value = "物料数量")
    private String prodCount;
    /**
     * 扫描时间
     */
    private String scanningTime;

    private String orderNo;
    private String lineNo;
}
