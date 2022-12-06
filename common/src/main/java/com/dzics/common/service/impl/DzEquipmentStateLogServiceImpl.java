package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.common.model.entity.DzEquipmentStateLog;
import com.dzics.common.dao.DzEquipmentStateLogMapper;
import com.dzics.common.service.DzEquipmentStateLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 设备运行状态记录表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-20
 */
@Service
public class DzEquipmentStateLogServiceImpl extends ServiceImpl<DzEquipmentStateLogMapper, DzEquipmentStateLog> implements DzEquipmentStateLogService {

    @Override
    public void delEquimentLog(Integer i) {
        long time = System.currentTimeMillis() - ((long)i * (24 * 3600 * 1000));
        Date date = new Date(time);
        QueryWrapper<DzEquipmentStateLog> wrapper = new QueryWrapper<>();
        wrapper.le("create_time", date);
        remove(wrapper);
    }
}
