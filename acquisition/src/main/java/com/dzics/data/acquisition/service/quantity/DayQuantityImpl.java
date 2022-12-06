package com.dzics.data.acquisition.service.quantity;

import com.dzics.common.dao.DzEquipmentProNumSignalMapper;
import com.dzics.common.model.statistics.DeviceMakeTotalDto;
import com.dzics.common.model.statistics.MakeQuantity;
import com.dzics.common.util.RedisKey;
import com.dzics.data.acquisition.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * @Classname DayTotalQuantityImpl
 * @Description 设备生产日产量实现
 * @Date 2022/6/9 10:49
 * @Created by NeverEnd
 */
@Service
@Slf4j
public class DayQuantityImpl {
    @Autowired
    private DzEquipmentProNumSignalMapper proNumSignalMapper;
    @Autowired
    private RedisUtil<MakeQuantity> redisUtil;

    /**
     * @param parms 获取产量条件
     * @return 设备生产日产量
     */
    public MakeQuantity getProductionQuantity(DeviceMakeTotalDto parms) {
        String tableName = parms.getTableName();
        Long deviceId = parms.getDeviceId();
        LocalDate localDate = parms.getWorkDate();
        MakeQuantity number = proNumSignalMapper.getDeviceProNumber(tableName, deviceId, localDate);
        return number;
    }
}
