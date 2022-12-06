package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.DzEquipmentProNumSignalMapper;
import com.dzics.common.model.entity.DzEquipmentProNumSignal;
import com.dzics.common.service.DzEquipmentProNumSignalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 班次生产记录表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-23
 */
@Service
@Slf4j
public class DzEquipmentProNumSignalServiceImpl extends ServiceImpl<DzEquipmentProNumSignalMapper, DzEquipmentProNumSignal> implements DzEquipmentProNumSignalService {

    @Override
    public DzEquipmentProNumSignal getByDayId(Long id, String productType, String batchNumber, String modelNumber, int hour) {
        QueryWrapper<DzEquipmentProNumSignal> wp = new QueryWrapper<>();
        wp.eq("day_id", id);
        wp.eq("product_type", productType);
        wp.eq("batch_number", batchNumber);
        wp.eq("model_number", modelNumber);
        wp.eq("work_hour", hour);
        wp.orderByDesc("create_time");
        List<DzEquipmentProNumSignal> lists = this.list(wp);
        if (CollectionUtils.isNotEmpty(lists)) {
            if (lists.size() > 1) {
                log.warn("一天同一个班次在班次生产记录表dz_equipment_pro_num存在多条记录：size:{}", lists.size());
            }
            return lists.get(0);
        }
        return null;
    }

    @Override
    public void updateDzEqProNum(DzEquipmentProNumSignal dzEquipmentProNumSignal) {
        dzEquipmentProNumSignal.setUpdateTime(new Date());
        QueryWrapper<DzEquipmentProNumSignal> wp = new QueryWrapper<>();
        wp.eq("day_id", dzEquipmentProNumSignal.getDayId());
        wp.eq("product_type", dzEquipmentProNumSignal.getProductType());
        wp.eq("batch_number", dzEquipmentProNumSignal.getBatchNumber());
        wp.eq("model_number", dzEquipmentProNumSignal.getModelNumber());
        wp.eq("work_hour", dzEquipmentProNumSignal.getWorkHour());
        this.update(dzEquipmentProNumSignal, wp);
    }

    @Override
    public DzEquipmentProNumSignal saveDzEqProNum(DzEquipmentProNumSignal dzEquipmentProNumSignal) {
        dzEquipmentProNumSignal.setCreateTime(new Date());
        save(dzEquipmentProNumSignal);
        return dzEquipmentProNumSignal;
    }

    @Override
    public Long getEquimentIdDayProNum(Long id, LocalDate nowDay, String tableKey) {
        Long sumDay = baseMapper.getEquimentIdDayProNum(id, nowDay, tableKey);
        return sumDay == null ? 0 : sumDay;
    }
}
