package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.dao.DzEquipmentMapper;
import com.dzics.common.service.AverageBeatService;
import com.dzics.common.service.DzEquipmentProNumSignalService;
import com.dzics.common.service.DzEquipmentRunTimeService;
import com.dzics.common.util.RedisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/6/4.
 */
@Service
@Slf4j
public class AverageBeatServiceImpl implements AverageBeatService {
    @Autowired
    private DzEquipmentMapper dzEquipmentMapper;
    @Autowired
    private DzEquipmentRunTimeService runTimeService;
    @Autowired
    private DzEquipmentProNumSignalService proNumSignalService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public String getDeviceIdAverageBeat(List<Long> times, Long id) {
        try {
            if (CollectionUtils.isEmpty(times)) {
                return "0";
            } else {
                if (times.size() == 1) {
                    return "0";
                }
                Long endT = times.get(times.size() - 1);
                Long stT = times.get(0);
                long currentTimeMillis = System.currentTimeMillis();
                if (endT.longValue() + (60000L) < currentTimeMillis) {
                    redisTemplate.delete(RedisKey.FREQUENCY_MIN + id);
                    return "0";
                }
//            相差秒数
                BigDecimal timeEnd = new BigDecimal(endT);
                BigDecimal timeStart = new BigDecimal(stT);
//            毫秒时间差 换算为 秒
                BigDecimal subtract = ((timeEnd.subtract(timeStart)).divide(new BigDecimal(1000), 6, BigDecimal.ROUND_HALF_UP)).setScale(6, BigDecimal.ROUND_HALF_UP);
//            每秒产多少个
                BigDecimal divide = (new BigDecimal(times.size()).divide(subtract, 6, BigDecimal.ROUND_HALF_UP)).setScale(6, BigDecimal.ROUND_HALF_UP);
//            计算60s 生产多少个
                BigDecimal multiply = divide.multiply(new BigDecimal(60)).setScale(2, BigDecimal.ROUND_HALF_UP);
                return multiply.toString();
            }
        } catch (Throwable throwable) {
            log.error("计算平均节拍错误：{}", throwable.getMessage(), throwable);
            return "-999.99";
        }


    }

    /**
     * 单位分钟
     *
     * @param id
     * @param equipmentNo
     * @param equipmentType
     * @param orderNo
     * @param lineNo
     * @param tableKey
     * @return
     */
    public String getDeviceIdAverageBeat(Long id, String equipmentNo, Integer equipmentType,
                                         String orderNo, String lineNo, String tableKey) {
        LocalDate nowDay = LocalDate.now();
        BigDecimal runTime = runTimeService.getDayRunTime(orderNo, lineNo, equipmentNo, equipmentType, nowDay);
        if (runTime.compareTo(new BigDecimal(60)) < 0) {
            runTime = new BigDecimal(60);
        }
        BigDecimal runTimeMinute = runTime.divide(new BigDecimal(60), 0, BigDecimal.ROUND_HALF_UP);
        //           获取当设备当日生产数量
        Long proNum = proNumSignalService.getEquimentIdDayProNum(id, nowDay, tableKey);
        BigDecimal divide = new BigDecimal(proNum).divide(runTimeMinute, 1, BigDecimal.ROUND_HALF_UP);
        return divide.toString();
    }
}
