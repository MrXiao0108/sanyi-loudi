package com.dzics.data.acquisition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.common.model.entity.MomOrderQrCode;
import com.dzics.common.model.entity.MonOrder;
import com.dzics.common.service.MomOrderQrCodeService;
import com.dzics.common.util.RedisKey;
import com.dzics.data.acquisition.service.AccOrderQrCodeService;
import com.dzics.data.acquisition.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccOrderQrCodeServiceImpl implements AccOrderQrCodeService {
    @Autowired
    private MomOrderQrCodeService momOrderQrCodeService;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public Integer bandMomOrderQrCode(String qrcode, MonOrder startOrder, String orderCode, String lineNo) {
        if (startOrder == null) {
            return 0;
        }
        String proTaskOrderId = startOrder.getProTaskOrderId();
        String wiporderno = startOrder.getWiporderno();
        boolean falg = false;
        try {
//            绑定存储记录
            MomOrderQrCode qrCode = new MomOrderQrCode();
            qrCode.setProTaskOrderId(proTaskOrderId);
            qrCode.setProductCode(qrcode);
            qrCode.setMomOrdeGuid(wiporderno);
            qrCode.setDelFlag(false);
            qrCode.setCreateBy("SYS");
            qrCode.setOrderNo(orderCode);
            qrCode.setLineNo(lineNo);
            momOrderQrCodeService.save(qrCode);
            falg = true;
        } catch (Throwable throwable) {
            log.error("二维码绑定订单错误: qrcode: {},orderLine: {},ERROR: {}", qrcode, startOrder, throwable.getMessage(), throwable);
        }
        if (falg) {
            try {
//            根据工件二维码，订单号， 产线号，缓存订单信息
                redisUtil.set(RedisKey.cacheService_getMomOrderNoProducBarcode + qrcode + orderCode + lineNo, startOrder, 3600 * 12);
            } catch (Throwable throwable) {
                log.error("缓存订单信息错误 --> 根据工件二维码: {} , 订单号: {} , 产线号: {}, 缓存订单信息: {} ", qrcode, orderCode, lineNo, startOrder);
            }
//            获取绑定次数作为计数
            try {
                Object orderSum = redisUtil.get(RedisKey.cacheService_getMomOrderNoProducBarcode_SUM + proTaskOrderId);
                if (orderSum != null) {
                    Integer sum = (Integer) orderSum + 1;
                    redisUtil.set(RedisKey.cacheService_getMomOrderNoProducBarcode_SUM + proTaskOrderId, sum, 3600 * 12);
                    return sum;
                } else {
                    QueryWrapper<MomOrderQrCode> wp = new QueryWrapper<>();
                    wp.eq("pro_task_order_id", proTaskOrderId);
                    Integer sum = momOrderQrCodeService.count(wp);
                    redisUtil.set(RedisKey.cacheService_getMomOrderNoProducBarcode_SUM + proTaskOrderId, sum);
                    return sum;
                }
            } catch (Throwable throwable) {
                log.error("获取订单工件制作数量错误: 订单:{} ,产线: {} ,订单信息: {} ", orderCode, lineNo, startOrder, throwable);
                return 0;
            }
        }
        return 0;
    }
}
