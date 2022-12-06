package com.dzics.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.business.service.BusinessEquipmentProNumService;
import com.dzics.business.service.cache.DzDetectionTemplCache;
import com.dzics.common.dao.*;
import com.dzics.common.enums.UserIdentityEnum;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.entity.DzProduct;
import com.dzics.common.model.entity.DzProductionLine;
import com.dzics.common.model.entity.SysUser;
import com.dzics.common.model.constant.FinalCode;
import com.dzics.common.model.request.ProductionVo;
import com.dzics.common.model.request.plan.SelectEquipmentProductionDetailsVo;
import com.dzics.common.model.request.plan.SelectEquipmentProductionVo;
import com.dzics.common.model.request.plan.SelectProductionDetailsVo;
import com.dzics.common.model.response.*;
import com.dzics.common.model.response.plan.SelectEquipmentProductionDetailsDo;
import com.dzics.common.model.response.plan.SelectEquipmentProductionDo;
import com.dzics.common.model.response.plan.SelectProductionDetailsDo;
import com.dzics.common.service.SysUserServiceDao;
import com.dzics.common.util.PageLimit;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
@Slf4j
@Service
public class BusinessEquipmentProNumServiceImpl implements BusinessEquipmentProNumService {
    @Autowired
    SysUserServiceDao sysUserServiceDao;
    @Autowired
    DzEquipmentProNumMapper dzEquipmentProNumMapper;
    @Autowired
    DzDetectionTemplCache dzDetectionTemplCache;
    @Autowired
    DzProductionLineMapper dzProductionLineMapper;
    @Autowired
    DzProductMapper dzProductMapper;
    @Autowired
    DzEquipmentProNumSignalMapper dzEquipmentProNumSignalMapper;
    @Autowired
    DzEquipmentMapper dzEquipmentMapper;
    @Autowired
    DzOrderMapper dzOrderMapper;

    @Override
    public Result list(String sub, PageLimit pageLimit, SelectProductionDetailsVo selectProductionDetailsVo) {
        String tableKey = ((RunDataModel) dzDetectionTemplCache.systemRunModel(null).getData()).getTableName();
        selectProductionDetailsVo.setTableKey(tableKey);//动态填充需要查询的table
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue() && byUserName.getUseOrgCode().equals(FinalCode.DZ_USE_ORG_CODE)) {
            byUserName.setUseOrgCode(null);
        }
        selectProductionDetailsVo.setOrderNo(null);
        if (!StringUtil.isEmpty(selectProductionDetailsVo.getLineId())) {
            DzProductionLine dzProductionLine = dzProductionLineMapper.selectById(selectProductionDetailsVo.getLineId());
            if (dzProductionLine != null) {
                selectProductionDetailsVo.setOrderNo(dzProductionLine.getOrderNo());
            }
        }
        QueryWrapper<DzProductionLine> wrapper = new QueryWrapper<>();
        wrapper.select("statistics_equiment_id", "order_no", "line_name", "line_no");
        wrapper.isNotNull("statistics_equiment_id");
        List<DzProductionLine> dzProductionLines = dzProductionLineMapper.selectList(wrapper);
        if (dzProductionLines.size() == 0) {
            return new Result(CustomExceptionType.OK, new ArrayList<>(), 0);
        }
        selectProductionDetailsVo.setOrgCode(byUserName.getUseOrgCode());
        PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        List<SelectProductionDetailsDo> list = dzEquipmentProNumMapper.list(selectProductionDetailsVo);
        PageInfo<SelectProductionDetailsDo> info = new PageInfo<>(list);
        List<SelectProductionDetailsDo> list1 = info.getList();
        for (int i = 0; i < list1.size(); i++) {
            SelectProductionDetailsDo selectProductionDetailsDo = list1.get(i);
            for (DzProductionLine dzProductionLine : dzProductionLines) {
                if (dzProductionLine.getOrderNo().equals(selectProductionDetailsDo.getOrderNo()) &&
                        dzProductionLine.getLineNo().equals(selectProductionDetailsDo.getLineNo())
                ) {
                    list1.get(i).setLineName(dzProductionLine.getLineName());
                }
            }
            if (selectProductionDetailsDo.getProductName() == null) {//产线名称为空，产品id不存在
                String productType = selectProductionDetailsDo.getProductType();
                List<DzProduct> dzProducts = dzProductMapper.selectList(new QueryWrapper<DzProduct>().eq("product_name", productType).eq("depart_id", selectProductionDetailsDo.getDepartId()));
                if (dzProducts.size() == 1) {
                    String productNo = dzProducts.get(0).getProductNo();
                    String productName = dzProducts.get(0).getProductName();
                    list1.get(i).setProductNo(productNo);
                    list1.get(i).setProductName(productName);
                } else if (dzProducts.size() == 0) {
                    list1.get(i).setProductNo(FinalCode.DZ_PRODUCT_NO);
                    list1.get(i).setProductName(FinalCode.DZ_PRODUCT_NAME);

                }
            }

        }
        return new Result(CustomExceptionType.OK, list1, info.getTotal());
    }

    @Override
    public Result listProductionEquipment(String sub, PageLimit pageLimit, SelectEquipmentProductionVo selectProductionDetailsVo) {
        String tableKey = ((RunDataModel) dzDetectionTemplCache.systemRunModel(null).getData()).getTableName();
        selectProductionDetailsVo.setTableKey(tableKey);//动态填充需要查询的table
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue() && byUserName.getUseOrgCode().equals(FinalCode.DZ_USE_ORG_CODE)) {
            byUserName.setUseOrgCode(null);
        }
        selectProductionDetailsVo.setOrgCode(byUserName.getUseOrgCode());
        PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        List<SelectEquipmentProductionDo> list = dzEquipmentProNumMapper.listProductionEquipment(selectProductionDetailsVo);
        PageInfo<SelectEquipmentProductionDo> info = new PageInfo<>(list);
        return new Result(CustomExceptionType.OK, info.getList(), info.getTotal());
    }

    @Override
    public Result listProductionEquipmentDetails(String sub, SelectEquipmentProductionDetailsVo obj) {
        String tableKey = ((RunDataModel) dzDetectionTemplCache.systemRunModel(null).getData()).getTableName();
        List<SelectEquipmentProductionDetailsDo> list = dzEquipmentProNumMapper.listProductionEquipmentDetails(obj.getEquimentId(), obj.getWorkDate(), tableKey);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getProductName() == null || "".equals(list.get(i).getProductName())) {
                list.get(i).setProductName(FinalCode.DZ_PRODUCT_NAME);
            }
        }
        return new Result(CustomExceptionType.OK, list);
    }

    @Override
    public Result listProductionTime(String sub, ProductionVo productionVo) {
        //判断前段查询时是否传输了设备id，有则直接进行查询，没有则后端处理全查
        String tableKey = ((RunDataModel) dzDetectionTemplCache.systemRunModel(null).getData()).getTableName();
        List<DevcieNameId> devcieNameIdList = dzEquipmentMapper.getByIds(productionVo.getLineId());
        List<Long> ids = devcieNameIdList.stream().map(s -> s.getDeviceId()).collect(Collectors.toList());
        //查询出生产记录的详细时间段及数量
        String localDate = productionVo.getStartTime();
        if (StringUtils.isEmpty(localDate)) {
            localDate = LocalDate.now().toString();
        }
        List<HourToday> hourTodays = dzEquipmentProNumSignalMapper.selectTodayByHour(tableKey, localDate, ids);
        Map<Long, Map<Integer, Integer>> xx = getMapHourTodays(hourTodays);
        ProductionDo productionDo = new ProductionDo();
        List<String> name = new ArrayList<>();
        List<DevcieNameHourSum> series = new ArrayList<>();
        for (DevcieNameId devcieNameId : devcieNameIdList) {
            String deviceName = devcieNameId.getDeviceName();
            name.add(deviceName);
            Long deviceId = devcieNameId.getDeviceId();
            DevcieNameHourSum devcieNameHourSum = new DevcieNameHourSum();
            devcieNameHourSum.setName(deviceName);
            devcieNameHourSum.setType("line");
            Map<Integer, Integer> map = xx.get(deviceId);
            List<Integer> data = new ArrayList<>();
            for (int i = 0; i < 24; i++) {
                if (map != null) {
                    Integer integer = map.get(i);
                    if (integer != null) {
                        data.add(integer);
                    } else {
                        data.add(0);
                    }
                } else {
                    data.add(0);
                }

            }
            devcieNameHourSum.setData(data);
            series.add(devcieNameHourSum);
        }
        productionDo.setSeries(series);
        productionDo.setLegend(name);
        return new Result(CustomExceptionType.OK, productionDo);
    }

    private Map<Long, Map<Integer, Integer>> getMapHourTodays(List<HourToday> hourTodays) {
        Map<Long, Map<Integer, Integer>> longMapMap = new HashMap<>();
        for (HourToday hourToday : hourTodays) {
            Long deviceId = hourToday.getDeviceId();
            Integer hour = hourToday.getHour();
            int sumToday = hourToday.getSumToday().intValue();
            Map<Integer, Integer> integerHourTodayMap = longMapMap.get(deviceId);
            if (CollectionUtils.isNotEmpty(integerHourTodayMap)) {
                integerHourTodayMap.put(hour, sumToday);
            } else {
                integerHourTodayMap = new HashMap<>();
                integerHourTodayMap.put(hour, sumToday);
                longMapMap.put(deviceId, integerHourTodayMap);
            }
        }
        return longMapMap;
    }
}
