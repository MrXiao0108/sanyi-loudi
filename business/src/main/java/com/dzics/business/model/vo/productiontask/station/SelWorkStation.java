package com.dzics.business.model.vo.productiontask.station;

import com.dzics.common.util.PageLimit;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 查询获取工位参数
 *
 * @author ZhangChengJun
 * Date 2021/5/18.
 * @since
 */
@Data
public class SelWorkStation extends PageLimit {

    /**
     * 产线id
     */
    @ApiModelProperty("产线ID")
    private String lineId;

    @ApiModelProperty("订单ID")
    private String orderId;

    @ApiModelProperty("工序编号")
    private String workCode;

    @ApiModelProperty(value = "工位编号")
    private String stationCode;

}
