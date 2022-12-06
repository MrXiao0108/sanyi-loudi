package com.dzics.data.acquisition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.common.dao.DzEquipmentProNumMapper;
import com.dzics.common.model.entity.DzEquipmentProNum;
import com.dzics.common.service.DzEquipmentProNumService;
import com.dzics.data.acquisition.service.AcqDzEqProDataService;
import com.dzics.data.acquisition.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author ZhangChengJun
 * Date 2021/1/20.
 * @since
 */
@Slf4j
@Service
public class AcqDzEqProDataServiceImpl implements AcqDzEqProDataService {
    @Autowired
    private DzEquipmentProNumService proNumService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private DzEquipmentProNumMapper dzEquipmentProNumMapper;

    @Override
    public DzEquipmentProNum saveDzEqProNum(DzEquipmentProNum deviceNum) {
        deviceNum.setCreateTime(new Date());
        dzEquipmentProNumMapper.insert(deviceNum);
        return deviceNum;
    }

    @Override
    public void updateDzEqProNum(DzEquipmentProNum dzEqProNum) {
        dzEqProNum.setUpdateTime(new Date());
        QueryWrapper<DzEquipmentProNum> wp = new QueryWrapper<>();
        wp.eq("day_id", dzEqProNum.getDayId());
        wp.eq("product_type", dzEqProNum.getProductType());
        wp.eq("batch_number", dzEqProNum.getBatchNumber());
        wp.eq("model_number", dzEqProNum.getModelNumber());
        wp.eq("work_hour", dzEqProNum.getWorkHour());
        proNumService.update(dzEqProNum, wp);
    }
}
