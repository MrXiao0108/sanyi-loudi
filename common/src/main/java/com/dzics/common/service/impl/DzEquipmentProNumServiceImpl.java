package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.DzEquipmentMapper;
import com.dzics.common.dao.DzEquipmentProNumMapper;
import com.dzics.common.model.custom.LineNumberTotal;
import com.dzics.common.model.custom.MachiningNumTotal;
import com.dzics.common.model.custom.SocketProQuantity;
import com.dzics.common.model.entity.DzEquipmentProNum;
import com.dzics.common.model.constant.SysConfigDepart;
import com.dzics.common.service.DzEquipmentProNumService;
import com.dzics.common.service.DzLineShiftDayService;
import com.dzics.common.service.DzProductionLineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 设备生产数量表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
@Service
@Slf4j
public class DzEquipmentProNumServiceImpl extends ServiceImpl<DzEquipmentProNumMapper, DzEquipmentProNum> implements DzEquipmentProNumService {

    @Autowired
    private DzProductionLineService dzProductionLineService;

    @Autowired
    private DzEquipmentProNumService dzEquipmentProNumService;

    @Autowired
    private DzEquipmentProNumMapper proNumMapper;
    @Autowired
    private DzEquipmentMapper dzEquipmentMapper;
    @Autowired
    private DzLineShiftDayService dzLineShiftDayService;

    @Override
    public void arrange() {
        dzProductionLineService.list();
    }

    @Override
    public DzEquipmentProNum getByDayId(Long id, String productType, String batchNumber, String modelNumber, int hour) {
        QueryWrapper<DzEquipmentProNum> wp = new QueryWrapper<>();
        wp.eq("day_id", id);
        wp.eq("product_type", productType);
        wp.eq("batch_number", batchNumber);
        wp.eq("model_number", modelNumber);
        wp.eq("work_hour", hour);
        wp.orderByDesc("create_time");
        List<DzEquipmentProNum> list = dzEquipmentProNumService.list(wp);
        if (CollectionUtils.isNotEmpty(list)) {
            if (list.size() > 1) {
                log.warn("一天同一个班次在班次生产记录表dz_equipment_pro_num存在多条记录：size:{}", list.size());
            }
            return list.get(0);
        }
        return null;
    }

    @Override
    public DzEquipmentProNum getDzEquipmentProNum(Long id) {
        QueryWrapper<DzEquipmentProNum> wp = new QueryWrapper<>();
        wp.eq("day_id", id);
        wp.orderByDesc("create_time");
        List<DzEquipmentProNum> lists = this.list(wp);
        if (CollectionUtils.isNotEmpty(lists)) {
            if (lists.size() == 1) {
                return lists.get(0);
            } else if (lists.size() > 1) {
                long roughNum = lists.stream().mapToLong(DzEquipmentProNum::getRoughNum).sum();
                long badnessNum = lists.stream().mapToLong(DzEquipmentProNum::getBadnessNum).sum();
                long nowNum = lists.stream().mapToLong(DzEquipmentProNum::getNowNum).sum();
                DzEquipmentProNum res = new DzEquipmentProNum();
                res.setRoughNum(roughNum);
                res.setBadnessNum(badnessNum);
                res.setNowNum(nowNum);
                return res;
            }
        }
        return null;
    }

    @Override
    public LineNumberTotal getLineSumQuantity(LocalDate now, Long eqId, String tableKey, String systemConfig) {
        LineNumberTotal eqIdData;
        if (systemConfig.equals(SysConfigDepart.SANY)) {
            eqIdData = proNumMapper.getLineSumQuantity(now, eqId, tableKey);
//            eqIdData = proNumMapper.getLineSumQuantityWorkShitf(now, eqId, tableKey);
        } else {
            eqIdData = proNumMapper.getLineSumQuantity(now, eqId, tableKey);
        }
        if (eqIdData != null) {
            eqIdData.setEquimentId(eqId != null ? eqId.toString() : "");
        }
        return eqIdData;
    }

    @Override
    public List<SocketProQuantity> getInputOutputDefectiveProducts(String tableKey, LocalDate now, String orderNo, String lineNo) {
        List<Long> deviceIds = dzEquipmentMapper.getByOrderNoLineNo(orderNo, lineNo);
        List<SocketProQuantity> socketProQuantityList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(deviceIds)) {
            List<SocketProQuantity> list = proNumMapper.getInputOutputDefectiveProducts(tableKey, deviceIds, now);
            Map<String, SocketProQuantity> map = new HashMap<>();
            for (SocketProQuantity socketProQuantity : list) {
                String equimentId = socketProQuantity.getEquimentId();
                SocketProQuantity quantity = map.get(equimentId);
                if (quantity != null) {
                    Long badnessNum = quantity.getBadnessNum() + socketProQuantity.getBadnessNum();
                    Long nowNum = quantity.getNowNum() + socketProQuantity.getNowNum();
                    Long roughNum = quantity.getRoughNum() + socketProQuantity.getRoughNum();
                    quantity.setBadnessNum(badnessNum);
                    quantity.setNowNum(nowNum);
                    quantity.setRoughNum(roughNum);
                    map.put(equimentId,quantity);
                } else {
                    map.put(equimentId, socketProQuantity);
                }

            }
            for (Map.Entry<String, SocketProQuantity> stringSocketProQuantityEntry : map.entrySet()) {
                socketProQuantityList.add(stringSocketProQuantityEntry.getValue());
            }
        }
        return socketProQuantityList;
    }


    @Override
    public List<MachiningNumTotal> getEqIdData(LocalDate now, List<String> collect, String tableKey) {
//        获取总产列表
        List<MachiningNumTotal> machiningNumTotals = proNumMapper.getEqIdDataTotal(collect);
//        获取日产列表
        List<MachiningNumTotal> eqIdData = proNumMapper.getEqIdData(now, collect, tableKey);
        for (MachiningNumTotal machiningNumTotal : machiningNumTotals) {
            for (MachiningNumTotal eqIdDatum : eqIdData) {
                if (machiningNumTotal.getEquimentId().equals(eqIdDatum.getEquimentId())) {
                    machiningNumTotal.setDayNum(eqIdDatum.getDayNum());
                    break;
                }
            }
        }
        return machiningNumTotals;

    }
}
