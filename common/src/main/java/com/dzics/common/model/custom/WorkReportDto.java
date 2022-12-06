package com.dzics.common.model.custom;

import lombok.Data;

/**
 * @Classname WorkReportDto
 * @Description 触发报工的DTO
 * @Date 2022/5/20 17:29
 * @Created by NeverEnd
 */
@Data
public class WorkReportDto {
    /**
     * 1 OK
     * 0 NG
     */
    private Integer outOk;
    /**
     * 二维码
     */
    private String qrCode;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 产线号
     */
    private String lineNo;

    private Long orderId;

    private Long lineId;

    private String groupId;
}
