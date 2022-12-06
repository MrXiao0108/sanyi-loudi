package com.dzics.data.acquisition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.dao.DzEquipmentMapper;
import com.dzics.common.dao.DzProductDetectionTemplateMapper;
import com.dzics.common.model.custom.*;
import com.dzics.common.model.entity.*;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.RunDataModel;
import com.dzics.common.model.response.timeanalysis.TimeAnalysisCmd;
import com.dzics.common.service.*;
import com.dzics.common.util.RedisKey;
import com.dzics.data.acquisition.service.*;
import com.dzics.data.acquisition.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/1/27.
 * @since
 */
@Service
@Slf4j
public class CacheServiceImpl implements CacheService {
    @Autowired
    private MomOrderService momOrderService;
    @Autowired
    private MomOrderQrCodeService momOrderQrCodeService;
    @Autowired
    private AccqDzEqProNumDetailsService accqDzEqProNumDetailsService;
    @Autowired
    private AccqDzEqProDetailsSignalService accqDzEqProDetailsSignalService;
    @Autowired
    private AccqDzEquipmentService dzEquipmentService;
    @Autowired
    private DzLineShiftDayService dzLineShiftDayService;
    @Autowired
    private DzEquipmentProNumService proNumService;
    @Autowired
    private DzEquipmentProNumSignalService proNumSignalService;
    @Autowired
    private AcqDzEqProDataService accqDzEqProDataService;
    @Autowired
    private AccqDzEquipmentService accqDzEquipmentService;
    @Autowired
    private DzProductDetectionTemplateMapper detectionTemplateMapper;

    @Autowired
    private DzProductService dzProductService;
    @Autowired
    private DzProductionLineService dzProductionLineService;

    @Autowired
    DzEquipmentMapper dzEquipmentMapper;
    @Autowired
    private AccEquipmentRunTimeService accEquipmentRunTimeService;

    @Autowired
    private AccDayShutDownTimesService accDayShutDownTimesService;

    @Autowired
    private DzWorkStationManagementService dzWorkStationManagementService;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    SysDictItemService sysDictItemService;


    @Autowired
    private AccDeviceTimeAnalysisService accDeviceTimeAnalysisService;

    /**
     * 当前时间是否在 开始时间之后和 结束时间之前
     *
     * @param now 当前时间
     * @param st  开始时间
     * @param en  结束时间
     * @return
     */
    public static boolean compareSection(LocalTime now, LocalTime st, LocalTime en) {
        if (now.isAfter(st)) {

        }
        if (now.isAfter(st) && now.isBefore(en)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param lineNum     产线序号
     * @param deviceNum   设备序号
     * @param deviceType  设备类型
     * @param orderNumber
     * @return
     */


    @Override
    public String getDeviceOrgCode(String lineNum, String deviceNum, String deviceType, String orderNumber) {
        DzEquipment typeLingEqNo = dzEquipmentService.getTypeLingEqNo(deviceNum, lineNum, deviceType, orderNumber);
        if (typeLingEqNo != null) {
            return typeLingEqNo.getOrgCode();
        }
        return "unknown";
    }


    @Override
    public UpValueDevice getUpValueDevice(String lineNum, String deviceNum, String deviceType, String orderNumber) {
        UpValueDevice upValueDevice = accqDzEqProNumDetailsService.getupsavenumlinenuty(lineNum, deviceNum, deviceType, orderNumber);
        if (upValueDevice == null) {
//                    上次数据赋值默认0
            upValueDevice = new UpValueDevice();
            upValueDevice.setWorkNum(0L);
            upValueDevice.setTotalNum(0L);
            upValueDevice.setQualifiedNum(0L);
            upValueDevice.setTotalQualifiedNum(0L);
            upValueDevice.setRoughNum(0L);
            upValueDevice.setTotalRoughNum(0L);
            upValueDevice.setBadnessNum(0L);
            upValueDevice.setTotalBadnessNum(0L);
            log.warn("上次班次生产数据不存在,设置默认值:lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, upValueDevice);
        }
        return upValueDevice;
    }


    @Override
    public UpValueDevice saveUpValueDevice(String lineNum, String deviceNum, String deviceType, String orderNumber, UpValueDevice nowValueDevice) {
        return nowValueDevice;
    }

    @Override
    public DzLineShiftDay getLingShifuDay(String lineNum, String deviceNum, String deviceType, String orderNumber, LocalDate nowLocalDate, LocalTime localTime) {

        List<DzLineShiftDay> dzLineShiftDays = this.getLingShifuDays(lineNum, deviceNum, deviceType, orderNumber, nowLocalDate);
        if (CollectionUtils.isNotEmpty(dzLineShiftDays)) {
            for (DzLineShiftDay day : dzLineShiftDays) {
                LocalTime startTime = day.getStartTime();
                LocalTime endTime = day.getEndTime();
                if (localTime.compareTo(startTime) == 0) {
                    return day;
                }
                //判断班次  开始时间和结束时间是否在同一天
                // 结束时间大于开始时间  则为同一天  反之则为跨天
                if (endTime.compareTo(startTime) == 1) {
                    if (localTime.compareTo(startTime) == 1 && localTime.compareTo(endTime) == -1) {
                        return day;
                    }
                } else {
                    if (localTime.compareTo(endTime) == -1 || localTime.compareTo(startTime) == 1) {

                        //当前时间已经属于当前班次的第二天了
                        if (localTime.compareTo(endTime) == -1) {
                            List<DzLineShiftDay> dzLineShiftDays2 = this.getLingShifuDays(lineNum, deviceNum, deviceType, orderNumber, nowLocalDate.plusDays(-1));
                            for (DzLineShiftDay day2 : dzLineShiftDays2) {
                                if (day2.getEndTime().compareTo(day2.getStartTime()) == -1) {
                                    if (localTime.compareTo(day2.getEndTime()) == -1 || localTime.compareTo(day2.getStartTime()) == 1) {
                                        return day2;
                                    }
                                }


                            }
                        } else {
                            return day;
                        }
                    }
                }
            }
            log.error("查询设备当日排班数据存在,没有在时间范围内：lineNum: {},deviceNum: {},deviceType: {},nowLocalDate:{} ,localTime:{} 失败",
                    lineNum, deviceNum, deviceType, nowLocalDate);
        } else {
            log.error("查询设备当日排班不存在：orderNumber:{},lineNum: {},deviceNum: {},deviceType: {},nowLocalDate:{} 失败",
                    orderNumber, lineNum, deviceNum, deviceType, nowLocalDate);
        }
        return null;
    }


    @Override
    public List<DzLineShiftDay> getLingShifuDays(String lineNum, String deviceNum, String deviceType, String orderNumber, LocalDate nowLocalDate) {
        List<DzLineShiftDay> dzLineShiftDays = dzLineShiftDayService.getlingshifudays(lineNum, deviceNum, deviceType, orderNumber, nowLocalDate);
        return dzLineShiftDays;
    }

    @Override
    public DzEquipmentProNum updateDzEqProNum(DzEquipmentProNum dzEquipmentProNum) {
        accqDzEqProDataService.updateDzEqProNum(dzEquipmentProNum);
        return dzEquipmentProNum;
    }

    @Override
    public DzEquipmentProNum saveDzEqProNum(DzEquipmentProNum dzEqProNum) {
        return accqDzEqProDataService.saveDzEqProNum(dzEqProNum);
    }

    @Override
    public DzEquipment getTypeLingEqNo(String deviceNum, String lineNum, String deviceTypeStr, String orderNumber) {
        return accqDzEquipmentService.getTypeLingEqNo(deviceNum, lineNum, deviceTypeStr, orderNumber);
    }

    @Override
    public DzEquipment updateByLineNoAndEqNo(DzEquipment upDzDqState) {
        int i = accqDzEquipmentService.updateByLineNoAndEqNo(upDzDqState);
        return upDzDqState;
    }

    @Override
    public DzEquipment getTypeLingEqNoPush(String deviceNum, String lineNum, String deviceTypeStr, String orderNumber) {
        DzEquipment typeLingEqNo = accqDzEquipmentService.getTypeLingEqNo(deviceNum, lineNum, deviceTypeStr, orderNumber);
        typeLingEqNo.setB527(typeLingEqNo.getCleanTime());
        return typeLingEqNo;
    }

    @Override
    public DzEquipment updateByLineNoAndEqNoPush(DzEquipment upDzDqState) {
        return upDzDqState;
    }

    @Override
    public CmdTcp getUpRunState(String lineNum, String deviceNum, String deviceTypeStr, String orderNumber, CmdTcp nowDzDq) {
        QueryWrapper<DzEquipment> wrapper = new QueryWrapper();
        wrapper.eq("order_no", orderNumber);
        wrapper.eq("line_no", lineNum);
        wrapper.eq("equipment_no", deviceNum);
        wrapper.eq("equipment_type", deviceTypeStr);
        DzEquipment dzEquipment = dzEquipmentMapper.selectOne(wrapper);
        if (dzEquipment != null) {
            nowDzDq.setDeviceItemValue(dzEquipment.getRunStatusValue() != null ? dzEquipment.getRunStatusValue().toString() : null);
            nowDzDq.setTcpDescription(dzEquipment.getRunStatus());
            nowDzDq.setTcpValue(getTcpByDeviceTypeRunState(deviceTypeStr));
        }
        return nowDzDq;
    }

    @Override
    public CmdTcp upDateUpRunState(String lineNum, String deviceNum, String deviceTypeStr, String orderNumber, CmdTcp nowDzDq) {
        return nowDzDq;
    }

    @Override
    public Long upDownSum(String lineNum, String deviceNum, String deviceTypeStr, String orderNumber) {
        DzEquipment typeLingEqNo = accqDzEquipmentService.getTypeLingEqNo(deviceNum, lineNum, deviceTypeStr, orderNumber);
        return typeLingEqNo != null ? (typeLingEqNo.getDownSum() == null ? 0L : typeLingEqNo.getDownSum()) : null;
    }

    @Override
    public Long upDateDownSum(String lineNum, String deviceNum, String deviceTypeStr, String orderNumber, Long dowmSum) {
        return dowmSum;
    }

    @Override
    public Long upDownSumTime(String lineNum, String deviceNum, String deviceTypeStr, String orderNumber) {
        DzEquipment typeLingEqNo = accqDzEquipmentService.getTypeLingEqNo(deviceNum, lineNum, deviceTypeStr, orderNumber);
        return typeLingEqNo != null ? (typeLingEqNo.getDownTime() == null ? 0L : typeLingEqNo.getDownTime()) : null;
    }

    @Override
    public Long upDateDownTime(String lineNum, String deviceNum, String deviceTypeStr, String orderNumber, Long downTime) {
        return downTime;
    }

    @Override
    public List<ProductTemp> getDzProDetectIonTemp(String productNo) {
        return detectionTemplateMapper.getDzProDetectIonTemp(productNo);
    }

    @Override
    public Long getTypeLingEqNoId(String deviceNum, String lineNum, String deviceType, String orderNumber) {
        DzEquipment typeLingEqNo = dzEquipmentService.getTypeLingEqNo(deviceNum, lineNum, deviceType, orderNumber);
        if (typeLingEqNo != null) {
            return typeLingEqNo.getId();
        }
        return -99999L;
    }

    @Override
    public DzEquipmentProNumSignal getDzDayEqProNumSig(Long id, String productType, String batchNumber, String modelNumber, int hour) {
        DzEquipmentProNumSignal signal = proNumSignalService.getByDayId(id, productType, batchNumber, modelNumber, hour);
        return signal;
    }

    @Override
    public DzEquipmentProNumSignal updateDzEqProNumSignal(DzEquipmentProNumSignal dzEquipmentProNumSignal) {
        proNumSignalService.updateDzEqProNum(dzEquipmentProNumSignal);
        return dzEquipmentProNumSignal;
    }

    @Override
    public DzEquipmentProNumSignal saveDzEqProNumSignal(DzEquipmentProNumSignal dzEquipmentProNumSignal) {
        return proNumSignalService.saveDzEqProNum(dzEquipmentProNumSignal);
    }

    @Override
    public UpValueDeviceSignal getUpValueDeviceSignal(String lineNum, String deviceNum, String deviceType, String orderNumber, Long dayId) {
        UpValueDeviceSignal upValueDevice = accqDzEqProDetailsSignalService.getUpSaveDdNumLinNuTy(lineNum, deviceNum, deviceType, orderNumber, dayId);
        if (upValueDevice == null) {
//                    上次数据赋值默认0
            upValueDevice = new UpValueDeviceSignal();
            upValueDevice.setWorkNum(0L);
            upValueDevice.setTotalNum(0L);
            upValueDevice.setQualifiedNum(0L);
            upValueDevice.setTotalQualifiedNum(0L);
            upValueDevice.setRoughNum(0L);
            upValueDevice.setTotalRoughNum(0L);
            upValueDevice.setBadnessNum(0L);
            upValueDevice.setTotalBadnessNum(0L);
            log.warn("上次班次生产数据不存在,设置默认值:lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, upValueDevice);
        }
        return upValueDevice;
    }

    @Override
    public UpValueDeviceSignal saveUpValueDeviceSignal(String lineNum, String deviceNum, String deviceType, String orderNumber, Long dayId, UpValueDeviceSignal upValueDevice) {
        return upValueDevice;
    }

    @Override
    public Result systemRunModel(String sub) {
        RunDataModel runDataModel = sysConfigService.systemRunModel();
        if (runDataModel == null) {
            runDataModel.setTableName("dz_equipment_pro_num");
            runDataModel.setPlanDay("dz_production_plan_day");
            runDataModel.setRunDataModel("数量累计模式");
        }
        return Result.OK(runDataModel);
    }

    @Override
    public WorkNumberName getProductNo(String modelNumber) {
        WorkNumberName productNo = dzProductService.getProductNo(modelNumber);
        if (productNo == null) {
            productNo = new WorkNumberName();
            productNo.setProductName("默认产品");
            productNo.setModelNumber("-9999.999");
        }
        return productNo;
    }

    @Override
    public Long lineId(String orderNo, String lineNo) {
        QueryWrapper<DzProductionLine> wp = new QueryWrapper<>();
        wp.select("id");
        wp.eq("line_no", lineNo);
        wp.eq("order_no", orderNo);
        DzProductionLine one = dzProductionLineService.getOne(wp);
        if (one != null) {
            return one.getId();
        }
        return null;
    }


    @Override
    public DzEquipmentProNum getDzDayEqProNum(Long id, String productType, String batchNumber, String modelNumber, int hour) {
        DzEquipmentProNum dzEquipmentProNum = proNumService.getByDayId(id, productType, batchNumber, modelNumber, hour);
        return dzEquipmentProNum;
    }


    @Override
    public DzEquipmentProNum getDzEquipmentProNum(Long id) {
        DzEquipmentProNum dzEquipmentProNum = proNumService.getDzEquipmentProNum(id);
        return dzEquipmentProNum;
    }

    @Override
    public CmdTcp getUpAlarmRecpRd(String lineNum, String deviceNum, String deviceTypeStr, String orderNumber, CmdTcp nowDzDq) {
        QueryWrapper<DzEquipment> wrapper = new QueryWrapper();
        wrapper.eq("order_no", orderNumber);
        wrapper.eq("line_no", lineNum);
        wrapper.eq("equipment_no", deviceNum);
        wrapper.eq("equipment_type", deviceTypeStr);
        DzEquipment dzEquipment = dzEquipmentMapper.selectOne(wrapper);
        if (dzEquipment != null) {
            nowDzDq.setDeviceItemValue(dzEquipment.getAlarmStatusValue().toString());
            nowDzDq.setTcpDescription(dzEquipment.getAlarmStatus());
            nowDzDq.setTcpValue(getTcpByDeviceType(deviceTypeStr));
        }
        return nowDzDq;
    }

    @Override
    public CmdTcp upDateUpAlremState(String lineNum, String deviceNum, String deviceTypeStr, String orderNumber, CmdTcp nowDzDq) {
        return nowDzDq;
    }

    @Override
    public DzEquipmentRunTime getRunTimeRecord(String orderNumber, String lineNum, String deviceNum, Integer deviceType) {
        return accEquipmentRunTimeService.getRunTimeRecord(orderNumber, lineNum, deviceNum, deviceType);
    }

    @Override
    public DzEquipmentRunTime updateRunTimeRecord(DzEquipmentRunTime dzEquipmentRunTime) {
        return accEquipmentRunTimeService.updateRunTimeRecord(dzEquipmentRunTime);
    }

    @Override
    public DzEquipmentRunTime insertRunTimeRecord(DzEquipmentRunTime dzEquipmentRunTime) {
        return accEquipmentRunTimeService.insertRunTimeRecord(dzEquipmentRunTime);
    }

    @Override
    public WorkNumberName getProductType(String productType) {
        return dzProductService.getProductType(productType);
    }

    @Override
    public DzDayShutDownTimes getDayShoutDownTime(String lineNum, String deviceNum, Integer deviceType, String orderNumber, LocalDate nowLocalDate) {
        DzDayShutDownTimes dzDayShutDownTimes = accDayShutDownTimesService.getDayShoutDownTime(lineNum, deviceNum, deviceType, orderNumber, nowLocalDate);
        return dzDayShutDownTimes;
    }

    @Override
    public DzDayShutDownTimes updateByIdDzDayShutDownTimes(DzDayShutDownTimes dzDayShutDownTimes) {
        boolean updateById = accDayShutDownTimesService.updateById(dzDayShutDownTimes);
        if (updateById) {
            return dzDayShutDownTimes;
        }
        return null;
    }

    @Override
    public DzWorkStationManagement getStationId(String deviceCode, Long orderId, Long lineId) {
        DzWorkStationManagement dzWorkStationManagement = dzWorkStationManagementService.getWorkStationCode(deviceCode, orderId, lineId);
        return dzWorkStationManagement;
    }

    @Override
    public OrderIdLineId getOrderNoLineNoId(String orderCode, String lineNo) {
        return dzProductionLineService.getOrderNoAndLineNo(orderCode, lineNo);
    }

    @Override
    public OrderIdLineId getOrderIdLineId(Long orderId, Long lineId) {
        return dzProductionLineService.getOrderIdLineId(orderId, lineId);
    }

    @Override
    public Integer getTypeLingEqNoDeviceSignalValue(String orderNumber, String lineNum, String deviceType, String deviceNum) {
        return dzEquipmentService.getDeviceSignalvalue(orderNumber, lineNum, deviceType, deviceNum);
    }

    @Override
    public Long getDeviceId(String orderCode, String lineNo, String deviceCode, String deviceType) {
        QueryWrapper<DzEquipment> wrapper = new QueryWrapper();
        wrapper.eq("order_no", orderCode);
        wrapper.eq("line_no", lineNo);
        wrapper.eq("equipment_no", deviceCode);
        wrapper.eq("equipment_type", deviceType);
        DzEquipment dzEquipment = dzEquipmentMapper.selectOne(wrapper);
        if (dzEquipment != null) {
            return dzEquipment.getId();
        }
        return null;
    }

    @Override
    public MonOrder getMomOrderNoProducBarcode(String producBarcode, String orderNumber, String lineNum) {
        Object moder = redisUtil.get(RedisKey.cacheService_getMomOrderNoProducBarcode + producBarcode + orderNumber + lineNum);
        if (moder == null) {
            QueryWrapper<MomOrderQrCode> wp = new QueryWrapper<>();
            wp.eq("product_code", producBarcode);
            wp.eq("order_no", orderNumber);
            wp.eq("line_no", lineNum);
            MomOrderQrCode one = momOrderQrCodeService.getOne(wp);
            if (one == null) {
                log.warn("二维码没有绑定订单: 二维码: {}, 订单号: {}, 产线号: {}", producBarcode, orderNumber, lineNum);
                return null;
            }
            MonOrder byId = momOrderService.getById(one.getProTaskOrderId());
            redisUtil.set(RedisKey.cacheService_getMomOrderNoProducBarcode + producBarcode + orderNumber + lineNum, byId, 15 * 60);
            return byId;
        } else {
            MonOrder monOrder = (MonOrder) moder;
            return monOrder;
        }
    }

    @Override
    public String getSystemConfigDepart() {
        try {
            SysDictItem dict_code = sysDictItemService.getOne(new QueryWrapper<SysDictItem>().eq("dict_code", "sys_depart"));
            if (dict_code == null) {
                return "SANY";
            }
            return dict_code.getItemText();
        } catch (Exception e) {
            return "SANY";
        }
    }

    @Autowired
    private DzDataCollectionService dzDataCollectionService;

    @Override
    public TimeAnalysisCmd getIotTableDeviceState(Long deviceId) {
        return dzDataCollectionService.getDeviceId(deviceId);
    }

    @Override
    public TimeAnalysisCmd updateIotTableDeviceState(TimeAnalysisCmd dzDataCollection) {
        return dzDataCollection;
    }

    @Override
    public Date robTimeAnalysis(String type) {
        Date date = accDeviceTimeAnalysisService.getUpdateTimeDesc();
        return date;
    }

    @Override
    public Date updateRobTimeAnalysis(String type, Date nowDate) {
        return nowDate;
    }

    @Override
    public String getSystemConfigModel() {
        try {
            SysDictItem dict_code = sysDictItemService.getOne(new QueryWrapper<SysDictItem>().eq("dict_code", "deployment_mode"));
            if (dict_code == null) {
                return "single";
            }
            return dict_code.getItemText();
        } catch (Throwable e) {
            log.error("获取系统部署模式异常:{}", e.getMessage(), e);
            return "single";
        }
    }

    @Override
    public void delNowOrder() {

    }

    @Override
    public Date updateRobTimeAnalysisAlarm(String type, Date date) {
        return date;
    }

    @Override
    public DzWorkStationManagement getStationIdMergeCode(Long orderId, Long lineId, String deviceCode, String mergeCode) {
        DzWorkStationManagement dzWorkStationManagement = dzWorkStationManagementService.getStationIdMergeCode(mergeCode,deviceCode, orderId, lineId);
        return dzWorkStationManagement;
    }


    public String getTcpByDeviceTypeRunState(String deviceTypeStr) {
        if ("2".equals(deviceTypeStr)) {
            return "B562";
        } else if ("3".equals(deviceTypeStr)) {
            return "A563";
        } else {
            return null;
        }
    }

    public String getTcpByDeviceType(String deviceTypeStr) {
        if ("2".equals(deviceTypeStr)) {
            return "B569";
        } else if ("3".equals(deviceTypeStr)) {
            return "A566";
        } else {
            return null;
        }
    }

}
