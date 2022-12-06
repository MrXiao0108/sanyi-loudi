package com.dzics.mqtt.service.impl;

import com.dzics.mqtt.model.entity.DzDataDevice;
import com.dzics.mqtt.dao.DzDataDeviceMapper;
import com.dzics.mqtt.service.DzDataDeviceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 数据采集设备表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-07-22
 */
@Service
@Slf4j
public class DzDataDeviceServiceImpl extends ServiceImpl<DzDataDeviceMapper, DzDataDevice> implements DzDataDeviceService {

    @Override
    public List<DzDataDevice> getByType(int sk) {
        List<DzDataDevice> byType = this.baseMapper.getByType(sk);
        log.info("设备类型：{},所有数控设备:{}", sk, byType);
        return byType;
    }
}
