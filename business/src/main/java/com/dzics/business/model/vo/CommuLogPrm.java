package com.dzics.business.model.vo;

import com.dzics.common.model.request.base.SearchTimeBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 通信日志参数
 *
 * @author ZhangChengJun
 * Date 2021/3/8.
 * @since
 */
@Data
public class CommuLogPrm extends SearchTimeBase {
    @ApiModelProperty("队列名称")
    private String queuename;
    @ApiModelProperty("订单编号")
    private String ordercode;
    @ApiModelProperty("产线序号")
    private String lineno;
    @ApiModelProperty("设备类型")
    private String devicetype;
    @ApiModelProperty("设备编码")
    private String devicecode;


}
