package com.dzics.kanban.model.request.kb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 订单参数信息
 *
 * @author ZhangChengJun
 * Date 2021/4/26.
 * @since
 */
@Data
public class GetOrderNoLineNo {
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
     * 缓存时长
     */
    @JsonIgnore
    private Integer cacheTime = 1;
}
