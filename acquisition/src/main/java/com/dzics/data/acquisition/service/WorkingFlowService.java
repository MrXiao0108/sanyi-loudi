package com.dzics.data.acquisition.service;

import com.dzics.common.model.custom.OrderIdLineId;
import com.dzics.common.model.custom.RabbitmqMessage;
import com.dzics.common.model.entity.DzWorkStationManagement;
import com.dzics.common.model.custom.ReqWorkQrCodeOrder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;

/**
 * 工作流程处理类
 *
 * @author ZhangChengJun
 * Date 2021/5/19.
 * @since
 */
@Service
public interface WorkingFlowService {

    /**
     * 处理工件位置数据
     *
     * @param rabbitmqMessage
     */
    ReqWorkQrCodeOrder processingData(RabbitmqMessage rabbitmqMessage);

    ReqWorkQrCodeOrder workFlow(OrderIdLineId orderIdLineId, DzWorkStationManagement saMt, String timestamp, String qrCode, String outInputType, String deviceCode, DzWorkStationManagement workStationSpare);

    /**
     * 不存在该二维码则插入
     *
     * @param qrCode
     * @param nowLocalDate
     * @param orderId
     * @param lineId
     * @param orderNo
     * @param lienNo
     */
    void saveBig(String qrCode, LocalDate nowLocalDate, Date workTime, Long orderId, Long lineId, String orderNo, String lienNo);
}
