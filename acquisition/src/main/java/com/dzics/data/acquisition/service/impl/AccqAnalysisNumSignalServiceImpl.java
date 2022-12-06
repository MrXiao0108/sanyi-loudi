package com.dzics.data.acquisition.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.dao.DzEquipmentProNumMapper;
import com.dzics.common.enums.CmdStateClassification;
import com.dzics.common.model.custom.CmdTcp;
import com.dzics.common.model.custom.DzTcpDateID;
import com.dzics.common.model.custom.RabbitmqMessage;
import com.dzics.common.model.entity.DzEquipmentProNumSignal;
import com.dzics.common.model.entity.DzLineShiftDay;
import com.dzics.common.model.constant.SysConfigDepart;
import com.dzics.common.util.DateUtil;
import com.dzics.common.util.RedisKey;
import com.dzics.data.acquisition.config.redis.RedisPrefxKey;
import com.dzics.data.acquisition.model.PylseSignalValue;
import com.dzics.data.acquisition.service.AccqAnalysisNumSignalService;
import com.dzics.data.acquisition.service.AccqDzEqProDetailsSignalService;
import com.dzics.data.acquisition.service.CacheService;
import com.dzics.data.acquisition.util.RedisUtil;
import com.dzics.data.acquisition.util.TcpStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author ZhangChengJun
 * Date 2021/2/23.
 * @since
 */
@Service
@Slf4j
public class AccqAnalysisNumSignalServiceImpl implements AccqAnalysisNumSignalService {
    @Autowired
    private DzEquipmentProNumMapper dzEquipmentProNumMapper;
    @Autowired
    private TcpStringUtil tcpStringUtil;
    @Autowired
    @Lazy
    private CacheService cacheServiceSig;
    @Autowired
    private AccqDzEqProDetailsSignalService accqDzEqProDetailsSignalService;
    @Autowired
    private RedisUtil redisUtil;


    @Transactional(rollbackFor = Throwable.class)
    @Override
    public synchronized DzEquipmentProNumSignal queuePylseSignal(RabbitmqMessage cmd) {
        log.debug("脉冲信号：{}", cmd);
        Map<String, Object> map = tcpStringUtil.analysisCmdV2(cmd);
        if (CollectionUtils.isNotEmpty(map)) {
//            底层设备上传时间时间
            Long senDate = (Long) map.get(CmdStateClassification.DATA_STATE_TIME.getCode());
//            分类唯一属性值
            DzTcpDateID dzTcpDateId = (DzTcpDateID) map.get(CmdStateClassification.TCP_ID.getCode());
            if (dzTcpDateId == null) {
                log.error("分类唯一属性值不存在：DzTcpDateID：{}", dzTcpDateId);
                return null;
            }
            String lineNum = dzTcpDateId.getProductionLineNumber();
            String deviceType = dzTcpDateId.getDeviceType();
            String deviceNum = dzTcpDateId.getDeviceNumber();
            String orderNumber = dzTcpDateId.getOrderNumber();
            List<CmdTcp> cmdTcps = (List<CmdTcp>) map.get(CmdStateClassification.PYLSE_SIGNAL.getCode());
//       当前发送数量
            CmdTcp nowDzDq = cmdTcps.get(0);
            PylseSignalValue signalValue = getSingValue(nowDzDq);
            if (signalValue == null) {
                return null;
            }
            if (signalValue.getSigFlag() == 1) {

//            默认跟新班次生产数据
                boolean falg = true;
//            定义当前时间 当前日期
                Date nowDate = new Date(senDate);
                LocalTime localTime = DateUtil.dataToLocalTime(nowDate).withNano(0);
                int hour = localTime.getHour();
                LocalDate nowLocalDate = DateUtil.dataToLocalDate(nowDate);
//            产品类型
                String productType = StringUtils.isEmpty(signalValue.getProductType()) ? "" : signalValue.getProductType();
//            产品批次
                String batchNumber = StringUtils.isEmpty(signalValue.getBatchCode()) ? "" : signalValue.getBatchCode();
//            产品编码
                String modelNumber = StringUtils.isEmpty(signalValue.getProductCode()) ? "" : signalValue.getProductCode();
                redisUtil.set(RedisPrefxKey.FEISHI_INDEX_KB + lineNum + "&" + deviceNum, modelNumber);
                redisUtil.set(RedisPrefxKey.FEISHI_INDEX_KB_PRODUCT_TYPE + lineNum + "&" + deviceNum, productType);
                int quantity1 = signalValue.getQuantity1();
                int quantity2 = signalValue.getQuantity2();
                int quantity3 = signalValue.getQuantity3();
                if (quantity1 != 0 || quantity2 != 0 || quantity3 != 0) {
//            获取设备该设备每次增加的值
                    String systemConfig=cacheServiceSig.getSystemConfigDepart();
                    Integer value = cacheServiceSig.getTypeLingEqNoDeviceSignalValue(orderNumber, lineNum, deviceType, deviceNum);
                    if (!systemConfig.equals(SysConfigDepart.SANY)) {
                        if (quantity1 != 0) {
                            quantity1 = value;
                        }
                    }
                    if (quantity2 != 0) {
                        quantity2 = value;
                    }
//            数据系统编码
                    String orgCode = cacheServiceSig.getDeviceOrgCode(lineNum, deviceNum, deviceType, orderNumber);
//         查看当前班次记录是否存在，存在则返回当前班次数据
                    DzEquipmentProNumSignal dzEqProNum = null;
                    //            当前班次定义
                    DzLineShiftDay lineShiftDays = cacheServiceSig.getLingShifuDay(lineNum, deviceNum, deviceType, orderNumber, nowLocalDate, localTime);
                    if (lineShiftDays != null) {
//                    TODO 如果有批次号,型号区分需要二次修改额外处理
//                    查看当前班次记录是否存在，存在则返回当前班次数据
                        dzEqProNum = cacheServiceSig.getDzDayEqProNumSig(lineShiftDays.getId(), productType, batchNumber, modelNumber, hour);
                        if (dzEqProNum == null) {
                            dzEqProNum = new DzEquipmentProNumSignal();
                            dzEqProNum.setWorkData(nowLocalDate);
                            dzEqProNum.setWorkYear(nowLocalDate.getYear());
                            dzEqProNum.setWorkMouth(nowLocalDate.getYear() + "-" + (nowLocalDate.getMonth().getValue() > 10 ? nowLocalDate.getMonth().getValue() : "0" + nowLocalDate.getMonth().getValue()));
                            dzEqProNum.setDayId(lineShiftDays.getId());
                            dzEqProNum.setProductType(productType);
                            dzEqProNum.setBatchNumber(batchNumber);
                            dzEqProNum.setModelNumber(modelNumber);
                            dzEqProNum.setOrderNo(orderNumber);
                            dzEqProNum.setLineNo(lineNum);
                            dzEqProNum.setRoughNum(0L);
                            dzEqProNum.setQualifiedNum(0L);
                            dzEqProNum.setNowNum(0L);
                            dzEqProNum.setTotalNum(0L);
                            dzEqProNum.setBadnessNum(0L);
                            dzEqProNum.setOrgCode(orgCode);
                            dzEqProNum.setDelFlag(false);
                            dzEqProNum.setWorkHour(hour);
                            falg = false;
                        }
                    } else {
//                    当前班次数据不存在时，这里返回，相当于现在存储的是有班次之前的数量值，一直到有班次是，中间的差额数量会计算到下次有班次的的记录中。
//                    如果当前班次数量不存在时间过长，或生产数量一个周期则数据会错误。
                        log.warn("当前排班数据不存在丢弃数据：lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, cmd);
                        return null;
                    }
//                  上次设备数据
                    Long dayId = dzEqProNum.getDayId();
//                  合格数量  当前班次，总计
                    Long q2 = Long.valueOf(quantity2); //传过来合格数量
                    Long dq2 = q2 + dzEqProNum.getQualifiedNum(); // 传过来的数量+上次班次的生产数量
                    dzEqProNum.setQualifiedNum(dq2);
//                  投入数量 毛坯数量, 当前班次，总计
                    Long q1 = Long.valueOf(quantity1); // 传过的毛坯数量
                    Long dqq1 = q1 + dzEqProNum.getRoughNum(); // 传过的毛坯数量 + 上次班次毛坯数量 = 需要更新的毛坯数量
                    dzEqProNum.setRoughNum(dqq1); // 设置 更新的毛坯数量
//                  不良品数量 、当前班次不良品数量、不良品总数
                    Long q3 = Long.valueOf(quantity3); // 传过来的不良品
                    Long dqq3 = q3 + dzEqProNum.getBadnessNum(); //传过来的不良品 + 上次班次的不良品数量 = 更新后的不良品数量
                    dzEqProNum.setBadnessNum(dqq3); // 设置 更新后的毛坯数量
//                   产出数量 = 不良品 + 合格品
                    //                  重新计算后产出数量 = 当前班次不良品数量  + 当前班次的合格品数量
                    Long hgq3 = dqq3.longValue() + dq2.longValue();
                    dzEqProNum.setNowNum(hgq3); // 设置 当前班次更新后的数量
                    if (falg) {
                        DzEquipmentProNumSignal dzEquipmentProNumSignal = cacheServiceSig.updateDzEqProNumSignal(dzEqProNum);
                    } else {
                        Long typeLingEqNo = cacheServiceSig.getTypeLingEqNoId(deviceNum, lineNum, deviceType, orderNumber);
                        dzEqProNum.setEquimentId(typeLingEqNo);
                        DzEquipmentProNumSignal dzEquipmentProNumSignal = cacheServiceSig.saveDzEqProNumSignal(dzEqProNum);
                     }
//                  缓存当前数据记录值
                    log.debug("保存当前数据详情& 设置当前生产数量缓存 :lineNum:{},deviceNum:{},deviceType:{}", lineNum, deviceNum, deviceType);
                    dzEqProNum.setCmdTcp((CmdTcp) map.get(CmdStateClassification.CMD_ROB_WORKPIECE_TOTAL.getCode()));
                    dzEqProNum.setSendSignalTime(senDate);
                    return dzEqProNum;
                } else {
                    log.warn("接收到脉冲信号：所有数量为0；sig：{}", signalValue);
                }
            } else {
                log.warn("脉冲sigflag不是1；sigflag：{}", signalValue.getSigFlag());
            }
        } else {
            log.warn("处理脉冲信号数据解析Map为为空：data：{}", map);
        }
        return null;
    }


    /**
     * [F01,,,0,1,0,0,1]
     * 产品类型，产品批次号，产品编码
     * 产品数量1 投入数量
     * 产品数量2 产出数量
     * 产品数量3 不良品数量
     * 产品数量4 预留
     * A810|[ZX01,,,1,0,0,0,1]
     * A810|[产品类型,产品批次号,产品编码,1,0,0,0,1]
     * B810|[SY01,,,0,1,0,0,1]
     * @param nowDzDq
     * @return
     */
    @Override
    public PylseSignalValue getSingValue(CmdTcp nowDzDq) {
        String deviceItemValue = nowDzDq.getDeviceItemValue();
        String[] sig = deviceItemValue.split(",");
        if (sig.length != 8) {
            log.error("脉冲信号值长度错误：data:{}", nowDzDq);
            return null;
        }
        PylseSignalValue signalValue = new PylseSignalValue();
        signalValue.setProductType(sig[0]);
        signalValue.setBatchCode(sig[1]);
        signalValue.setProductCode(sig[2]);
        signalValue.setQuantity1(Integer.valueOf(sig[3]));
        signalValue.setQuantity2(Integer.valueOf(sig[4]));
        signalValue.setQuantity3(Integer.valueOf(sig[5]));
        signalValue.setQuantity4(Integer.valueOf(sig[6]));
        signalValue.setSigFlag(Integer.valueOf(sig[7]));
        return signalValue;
    }

    @Override
    public synchronized boolean queuePylseSignalCheck(RabbitmqMessage rabbitmqMessage) {
        String deviceType = rabbitmqMessage.getDeviceType();
        String orderCode = rabbitmqMessage.getOrderCode();
        String clientId = rabbitmqMessage.getClientId();
        String deviceCode = rabbitmqMessage.getDeviceCode();
        String queueName = rabbitmqMessage.getQueueName();
        String lineNo = rabbitmqMessage.getLineNo();
        String message = rabbitmqMessage.getMessage();
        String timestamp = rabbitmqMessage.getTimestamp();
        try {
            RabbitmqMessage msg = (RabbitmqMessage) redisUtil.get(deviceType + orderCode + clientId + deviceCode + queueName + lineNo);
            if (msg != null) {
                if (message.equals(msg.getMessage())) {
                    log.debug("脉冲触发检测：上次时间：{}", msg.getTimestamp());
                    long dateUp = DateUtil.stringDateToformatDate(msg.getTimestamp()).getTime();
                    log.debug("脉冲触发检测：dateUp：{}", dateUp);

                    log.debug("脉冲触发检测：当前触发时间：{}", timestamp);
                    long dateNow = DateUtil.stringDateToformatDate(timestamp).getTime();
                    log.debug("脉冲触发检测：dateNow：{}", dateNow);
                    long difference = dateNow - dateUp;
                    log.debug("脉冲触发检测：difference：{}", difference);
                    if (difference > 400) {
                        redisUtil.set(deviceType + orderCode + clientId + deviceCode + queueName + lineNo, rabbitmqMessage, 3600);
                        return true;
                    } else {
                        return false;
                    }
                } else {
//                    验证通过
                    redisUtil.set(deviceType + orderCode + clientId + deviceCode + queueName + lineNo, rabbitmqMessage, 3600);
                    return true;
                }
            } else {
                redisUtil.set(deviceType + orderCode + clientId + deviceCode + queueName + lineNo, rabbitmqMessage, 3600);
                return true;
            }
        } catch (Throwable e) {
            log.error("校验数据错误 rabbitmqMessage：{}", rabbitmqMessage);
            redisUtil.set(deviceType + orderCode + clientId + deviceCode + queueName + lineNo, rabbitmqMessage, 3600);
            return true;
        }

    }

    @Override
    public void setRedisSignalValue(Long equimentId, Long sendSignalTime) {
        sendSignalTime = System.currentTimeMillis();
        List<Long> list = redisUtil.lGet(RedisKey.FREQUENCY_MIN + equimentId, 0, -1);
        if (CollectionUtils.isNotEmpty(list)) {
            if (list.size() >= 10) {
                for (int i = 0; i < 10; i++) {
                    if (i < 9) {
                        int j = i + 1;
                        Long lGetIndex = (Long) redisUtil.lGetIndex(RedisKey.FREQUENCY_MIN + equimentId, j);
                        redisUtil.lUpdateIndex(RedisKey.FREQUENCY_MIN + equimentId, i, lGetIndex);
                    } else {
                        redisUtil.lUpdateIndex(RedisKey.FREQUENCY_MIN + equimentId, i, sendSignalTime);
                    }
                }
                return;
            } else {
                redisUtil.lSet(RedisKey.FREQUENCY_MIN + equimentId, sendSignalTime);
                return;
            }
        } else {
            redisUtil.lSet(RedisKey.FREQUENCY_MIN + equimentId, sendSignalTime);
            return;
        }
    }

    @Override
    public synchronized Long compensate(DzEquipmentProNumSignal signal) {
        CmdTcp cmdTcp = signal.getCmdTcp();
        if (cmdTcp != null) {
            Long equimentId = signal.getEquimentId();
            Long comTime = (Long) redisUtil.get(RedisKey.PulseCompensation + equimentId);
            long currentTimeMillis = System.currentTimeMillis();
            if (comTime == null) {
                log.info("进行补偿订单:{},产线：{},设备ID：{},收到总数：{}，计算时间：{}", signal.getOrderNo(), signal.getLineNo(), signal.getEquimentId(), cmdTcp.getDeviceItemValue(), DateUtil.getDateStr(new Date()));
//                进行补偿的数值
                Long compensate = compensate(cmdTcp, equimentId);
                return compensate;
            } else {
//              半个小时间隔
                comTime = comTime.longValue() + (1800000);
                if (currentTimeMillis >= comTime.longValue()) {
//                   进行补偿的数值
                    log.info("进行补偿订单:{},产线：{},设备ID：{},收到总数：{}，计算时间：{}", signal.getOrderNo(), signal.getLineNo(), signal.getEquimentId(), cmdTcp.getDeviceItemValue(), DateUtil.getDateStr(new Date()));
                    Long compensate = compensate(cmdTcp, equimentId);
                    return compensate;
                }
            }
        }
        return 0L;
    }


    private Long compensate(CmdTcp cmdTcp, Long equimentId) {
        //            进行补偿  获取当前总数  计算差值
        Long sumData = dzEquipmentProNumMapper.getSumData("dz_equipment_pro_num_signal", equimentId);
        redisUtil.set(RedisKey.PulseCompensation + equimentId, System.currentTimeMillis());
        if (sumData != null) {
            String device_item_value = cmdTcp.getDeviceItemValue();
            if (!StringUtils.isEmpty(device_item_value)) {
//                    上发总数
                Long aLong = Long.valueOf(device_item_value);
                Long difference = aLong - sumData;
                if (difference.longValue() > 4 && difference.longValue() < 14) {
//                     需要补偿的数值必须大于4,差值大于4多余的数值进行补偿,一次补偿是2的倍数
                    Long res = (difference - 4) / 2 * 2;
                    log.info("设备ID：{}，收到数量：{}，计算总数：{},补偿数量：{}", equimentId, aLong, sumData, res);
                    return res;
                } else {
                    log.warn("设备ID:{} ,收到数量：{}，计算总数：{}，补偿值 :{} 不在 4-14 范围内不补偿", equimentId, aLong, sumData, difference);
                }
            }
        } else {
            log.warn("进行值补偿时根据设备id统计设备生产数量值不存在 equimentId:{}", equimentId);
        }
        return 0L;
    }
}
