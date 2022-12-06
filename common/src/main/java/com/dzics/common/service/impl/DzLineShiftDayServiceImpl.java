package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.DzEquipmentMapper;
import com.dzics.common.dao.DzLineShiftDayMapper;
import com.dzics.common.model.entity.DzEquipment;
import com.dzics.common.model.entity.DzLineShiftDay;
import com.dzics.common.service.DzLineShiftDayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 设备产线 每日 排班表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-19
 */
@SuppressWarnings("ALL")
@Slf4j
@Service
public class DzLineShiftDayServiceImpl extends ServiceImpl<DzLineShiftDayMapper, DzLineShiftDay> implements DzLineShiftDayService {
    @Autowired
    private DzLineShiftDayMapper dzLineShiftDayMapper;

    @Autowired
    private DzEquipmentMapper dzEquipmentMapper;

    @Override
    public List<DzLineShiftDay> getBc(List<Long> eqId) {
        return dzLineShiftDayMapper.getBc(eqId);
    }

    @Override
    public List<Long> getNotPb(LocalDate now) {
        return dzLineShiftDayMapper.getNotPb(now);
    }

    @Override
    public List<DzLineShiftDay> getlingshifudays(String lineNum, String deviceNum, String deviceType, String orderNumber, LocalDate nowLocalDate) {
        QueryWrapper<DzEquipment> wp1 = new QueryWrapper<>();
        wp1.eq("line_no", lineNum);
        wp1.eq("equipment_no", deviceNum);
        wp1.eq("equipment_type", deviceType);
        wp1.eq("order_no",orderNumber);
        DzEquipment dzEquipment = dzEquipmentMapper.selectOne(wp1);
        if(dzEquipment==null){
            log.error("设备不存在，订单号:{},产线序号:{},设备序号:{}，设备类型:{}",orderNumber,lineNum,deviceNum,deviceType);
            return new ArrayList<>();
        }
        QueryWrapper<DzLineShiftDay> wp = new QueryWrapper<>();
        wp.eq("line_no", lineNum);
        wp.eq("equipment_no", deviceNum);
        wp.eq("equipment_type", deviceType);
        wp.eq("work_data", nowLocalDate);
        wp.eq("order_no",orderNumber);
        wp.eq("eq_id",dzEquipment.getId());
        wp.orderByAsc("sort_no");
        return this.list(wp);

    }
}
