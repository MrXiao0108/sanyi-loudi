package com.dzics.mqtt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.model.entity.DzDataCollection;
import com.dzics.mqtt.dao.DzDataCollectionMapper;
import com.dzics.mqtt.service.DzDataCollectionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-07-22
 */
@Service
public class DzDataCollectionServiceImpl extends ServiceImpl<DzDataCollectionMapper, DzDataCollection> implements DzDataCollectionService {


    @Override
    public List<DzDataCollection> getDeviceIdDzDataColl(List<Long> deviceIds) {
        QueryWrapper<DzDataCollection> wp = new QueryWrapper<>();
        wp.in("device_id", deviceIds);
        List<DzDataCollection> dataCollections = list(wp);
        return dataCollections;
    }
}
