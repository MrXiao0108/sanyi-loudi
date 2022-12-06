package com.dzics.common.model.request.kb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 订单参数信息
 *
 * @author ZhangChengJun
 * Date 2021/4/26.
 * @since
 */
@Data
public class GetOrderNoLineNo {

    @ApiModelProperty("分析时长")
    private Integer timeAnalysis;
    /**
     * 订单序号
     */
    @ApiModelProperty("订单号")
    @NotNull(message = "订单号必传")
    private String orderNo;

    /**
     * 产线序号
     */
    @ApiModelProperty("产线号")
    @NotNull(message = "产线号必传")
    private String lineNo;

    /**
     * 设备类型  1, "检测设备"   2, "机床"    3, "机器人" 4, "相机" 6, "工件位置设备" 7, "AGV"
     */
    private String deviceType = "";
    /**
     * 缓存时长
     */
    @JsonIgnore
    private Integer cacheTime = 1;

    private String dataMessage;

    /**
     * 需要查询的检测数据的机床号
     */
    private List<String> machineNo;

    /**
     * 需要查询的天数
     */
    private Integer dayNumber = 7;

    @ApiModelProperty("站点编码")
    private String orgCode;
}
