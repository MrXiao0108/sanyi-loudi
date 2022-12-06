package com.dzics.common.model.response;

import lombok.Data;

/**
 * 设备订单产线 设备 Id 等信息
 *
 * @author ZhangChengJun
 * Date 2021/6/2.
 * @since
 */
@Data
public class EquimentOrderLineId {
    private Long id;
    private String equipmentNo;
    private Integer equipmentType;
    private String orderNo;
    private String lineNo;
    private String lineId;
    private String equipmentName;
}
