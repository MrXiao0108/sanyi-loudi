package com.dzics.data.acquisition.service;

import com.dzics.common.model.custom.*;
import com.dzics.common.model.entity.*;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.timeanalysis.TimeAnalysisCmd;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

/**
 * 缓存接口
 *
 * @author ZhangChengJun
 * Date 2021/1/27.
 * @since
 */
public interface CacheService {
    /**
     * @param lineNum     产线序号
     * @param deviceNum   设备序号
     * @param deviceType  设备类型
     * @param orderNumber
     * @return
     */
    @Cacheable(value = "cacheService.getDeviceOrgCode", key = "#lineNum+#deviceNum+#deviceType+#orderNumber", unless = "#result == null")
    String getDeviceOrgCode(String lineNum, String deviceNum, String deviceType, String orderNumber);

    /**
     * @param lineNum     产线序号
     * @param deviceNum   设备序号
     * @param deviceType  设备类型
     * @param orderNumber
     * @return
     */
    @Cacheable(value = "cacheService.getUpValueDevice", key = "#lineNum+#deviceNum+#deviceType+#orderNumber", unless = "#result == null")
    UpValueDevice getUpValueDevice(String lineNum, String deviceNum, String deviceType, String orderNumber);

    /**
     * @param lineNum        产线序号
     * @param deviceNum      设备序号
     * @param deviceType     设备类型
     * @param orderNumber
     * @param nowValueDevice 当前生产数据
     * @return
     */
    @CachePut(value = "cacheService.getUpValueDevice", key = "#lineNum+#deviceNum+#deviceType+#orderNumber")
    UpValueDevice saveUpValueDevice(String lineNum, String deviceNum, String deviceType, String orderNumber, UpValueDevice nowValueDevice);


    /**
     * 根据当前时间返回当前班次
     *
     * @param lineNum      产线序号
     * @param deviceNum    设备序号
     * @param deviceType   设备类型
     * @param orderNumber
     * @param nowLocalDate 当前日期
     * @param localTime    当前时间
     * @return
     */

    DzLineShiftDay getLingShifuDay(String lineNum, String deviceNum, String deviceType, String orderNumber, LocalDate nowLocalDate, LocalTime localTime);

    /**
     * @param lineNum      产线序号
     * @param deviceNum    设备序号
     * @param deviceType   设备类型
     * @param nowLocalDate 当前日期
     * @return
     */
    @Cacheable(value = "cacheService.getLingSHifuDays", key = "#lineNum+#deviceNum+#deviceType+#orderNumber+#nowLocalDate.toString()", unless = "#result == null")
    List<DzLineShiftDay> getLingShifuDays(String lineNum, String deviceNum, String deviceType, String orderNumber, LocalDate nowLocalDate);


    /**
     * @param id          每日排班中的 班次id
     * @param productType
     * @param batchNumber
     * @param modelNumber
     * @param hour
     * @return
     */
    @Cacheable(cacheNames = "cacheService.getDzDayEqProNum", key = "#id+#productType+#batchNumber+#modelNumber+#hour", unless = "#result == null")
    DzEquipmentProNum getDzDayEqProNum(Long id, String productType, String batchNumber, String modelNumber, int hour);

    @CachePut(cacheNames = "cacheService.getDzDayEqProNum", key = "#dzEquipmentProNum.dayId+#dzEquipmentProNum.productType+#dzEquipmentProNum.batchNumber+#dzEquipmentProNum.modelNumber")
    DzEquipmentProNum updateDzEqProNum(DzEquipmentProNum dzEquipmentProNum);

    @CachePut(cacheNames = "cacheService.getDzDayEqProNum", key = "#dzEqProNum.dayId+#dzEqProNum.productType+#dzEqProNum.batchNumber+#dzEqProNum.modelNumber")
    DzEquipmentProNum saveDzEqProNum(DzEquipmentProNum dzEqProNum);


    /**
     * 设备状态
     *
     * @param deviceNum
     * @param lineNum
     * @param deviceTypeStr
     * @param orderNumber
     * @return
     */
    @Cacheable(value = "cacheService.getTypeLingEqNo", key = "#deviceNum+#lineNum+#deviceTypeStr+#orderNumber", unless = "#result == null")
    DzEquipment getTypeLingEqNo(String deviceNum, String lineNum, String deviceTypeStr, String orderNumber);

    @CachePut(value = "cacheService.getTypeLingEqNo", key = "#upDzDqState.equipmentNo+#upDzDqState.lineNo+#upDzDqState.equipmentType+#upDzDqState.orderNo")
    DzEquipment updateByLineNoAndEqNo(DzEquipment upDzDqState);

    @Cacheable(value = "cacheService.getTypeLingEqNoPush", key = "#deviceNum+#lineNum+#deviceTypeStr+#orderNumber", unless = "#result == null")
    DzEquipment getTypeLingEqNoPush(String deviceNum, String lineNum, String deviceTypeStr, String orderNumber);

    @CachePut(value = "cacheService.getTypeLingEqNoPush", key = "#upDzDqState.equipmentNo+#upDzDqState.lineNo+#upDzDqState.equipmentType+#upDzDqState.orderNo")
    DzEquipment updateByLineNoAndEqNoPush(DzEquipment upDzDqState);

    @Cacheable(value = "cacheService.getUpRunState", key = "#lineNum+#deviceNum+#deviceTypeStr+#orderNumber", unless = "#result == null")
    CmdTcp getUpRunState(String lineNum, String deviceNum, String deviceTypeStr, String orderNumber, CmdTcp nowDzDq);

    @CachePut(value = "cacheService.getUpRunState", key = "#lineNum+#deviceNum+#deviceTypeStr+#orderNumber")
    CmdTcp upDateUpRunState(String lineNum, String deviceNum, String deviceTypeStr, String orderNumber, CmdTcp nowDzDq);

    @Cacheable(value = "cacheService.upDownSum", key = "#lineNum+#deviceNum+#deviceTypeStr+#orderNumber", unless = "#result == null")
    Long upDownSum(String lineNum, String deviceNum, String deviceTypeStr, String orderNumber);

    @CachePut(value = "cacheService.upDownSum", key = "#lineNum+#deviceNum+#deviceTypeStr+#orderNumber")
    Long upDateDownSum(String lineNum, String deviceNum, String deviceTypeStr, String orderNumber, Long dowmSum);


    @Cacheable(value = "cacheService.upDownSumTime", key = "#lineNum+#deviceNum+#deviceTypeStr+#orderNumber", unless = "#result == null")
    Long upDownSumTime(String lineNum, String deviceNum, String deviceTypeStr, String orderNumber);

    @CachePut(value = "cacheService.upDownSumTime", key = "#lineNum+#deviceNum+#deviceTypeStr+#orderNumber")
    Long upDateDownTime(String lineNum, String deviceNum, String deviceTypeStr, String orderNumber, Long downTime);

    /**
     * 根据产品id获取检测配置项
     * id，根据TabCloVal排序
     *
     * @param productNo 产品id
     * @return
     */
    @Cacheable(value = "cacheService.getDzProDetectIonTemp", key = "#productNo")
    List<ProductTemp> getDzProDetectIonTemp(String productNo);

    /**
     * 根据 设备号，产线号，设备类型，订单类型获取设备id
     *
     * @param deviceNum
     * @param lineNum
     * @param deviceType
     * @param orderNumber
     * @return
     */
    @Cacheable(value = "cacheService.getTypeLingEqNoId", key = "#deviceNum+#lineNum+#deviceType+#orderNumber")
    Long getTypeLingEqNoId(String deviceNum, String lineNum, String deviceType, String orderNumber);


    @Cacheable(cacheNames = "cacheServiceSig.getDzDayEqProNumSig", key = "#id+#productType+#batchNumber+#modelNumber+#hour", unless = "#result == null")
    DzEquipmentProNumSignal getDzDayEqProNumSig(Long id, String productType, String batchNumber, String modelNumber, int hour);

    @CachePut(cacheNames = "cacheServiceSig.getDzDayEqProNumSig", key = "#dzEquipmentProNumSignal.dayId+#dzEquipmentProNumSignal.productType+#dzEquipmentProNumSignal.batchNumber+#dzEquipmentProNumSignal.modelNumber+#dzEquipmentProNumSignal.workHour")
    DzEquipmentProNumSignal updateDzEqProNumSignal(DzEquipmentProNumSignal dzEquipmentProNumSignal);

    /**
     * @param dzEquipmentProNumSignal
     * @return
     */
    @CachePut(cacheNames = "cacheServiceSig.getDzDayEqProNumSig", key = "#dzEquipmentProNumSignal.dayId+#dzEquipmentProNumSignal.productType+#dzEquipmentProNumSignal.batchNumber+#dzEquipmentProNumSignal.modelNumber+#dzEquipmentProNumSignal.workHour")
    DzEquipmentProNumSignal saveDzEqProNumSignal(DzEquipmentProNumSignal dzEquipmentProNumSignal);


    /**
     * 获取 脉冲信号的当前生产数量
     *
     * @param lineNum
     * @param deviceNum
     * @param deviceType
     * @param orderNumber
     * @param dayId
     * @return
     */
    @Cacheable(value = "cacheServiceSig.getUpValueDeviceSignal", key = "#lineNum+#deviceNum+#deviceType+#orderNumber+#dayId", unless = "#result == null")
    UpValueDeviceSignal getUpValueDeviceSignal(String lineNum, String deviceNum, String deviceType, String orderNumber, Long dayId);

    /**
     * 更新缓存 中 脉冲信号的当前生产数量
     *
     * @param lineNum
     * @param deviceNum
     * @param deviceType
     * @param orderNumber
     * @param dayId
     * @param upValueDevice
     * @return
     */
    @CachePut(value = "cacheServiceSig.getUpValueDeviceSignal", key = "#lineNum+#deviceNum+#deviceType+#orderNumber+#dayId")
    UpValueDeviceSignal saveUpValueDeviceSignal(String lineNum, String deviceNum, String deviceType, String orderNumber, Long dayId, UpValueDeviceSignal upValueDevice);


    /**
     * 获取系统运行模式
     *
     * @param sub
     * @return
     */
    @Cacheable(cacheNames = "dzDetectionTemplCache.systemRunModel", key = "'runModel'")
    Result systemRunModel(String sub);

    /**
     * 根据产品编号获取 产品的产品名称，产品编号，
     * 也就是工件名称，工件编号
     *
     * @param modelNumber
     * @return
     */
    @Cacheable(cacheNames = "cacheService.getProductNo", key = "#modelNumber", unless = "#result == null")
    WorkNumberName getProductNo(String modelNumber);

    /**
     * 根据订单编号 产线序号 加载 产线id
     *
     * @param orderNo
     * @param lineNo
     * @return
     */
    @Cacheable(cacheNames = "cacheService.lineId", key = "#orderNo+#lineNo")
    Long lineId(String orderNo, String lineNo);

    /**
     * 根据班次ID 获取 班次生产记录表
     *
     * @param id
     * @return
     */
    DzEquipmentProNum getDzEquipmentProNum(Long id);

    /**
     * 设备上次告警状态
     *
     * @param lineNum
     * @param deviceNum
     * @param deviceTypeStr
     * @param orderNumber
     * @param nowDzDq
     * @return
     */
    @Cacheable(value = "cacheService.getUpAlarmRecpRd", key = "#lineNum+#deviceNum+#deviceTypeStr+#orderNumber", unless = "#result == null")
    CmdTcp getUpAlarmRecpRd(String lineNum, String deviceNum, String deviceTypeStr, String orderNumber, CmdTcp nowDzDq);

    /**
     * 更新设备上次告警状态
     *
     * @param lineNum
     * @param deviceNum
     * @param deviceTypeStr
     * @param orderNumber
     * @param nowDzDq
     * @return
     */
    @CachePut(value = "cacheService.getUpAlarmRecpRd", key = "#lineNum+#deviceNum+#deviceTypeStr+#orderNumber")
    CmdTcp upDateUpAlremState(String lineNum, String deviceNum, String deviceTypeStr, String orderNumber, CmdTcp nowDzDq);

    /**
     * 查询运行记录
     *
     * @param orderNumber
     * @param lineNum
     * @param deviceNum
     * @param deviceType
     * @return
     */
    DzEquipmentRunTime getRunTimeRecord(String orderNumber, String lineNum, String deviceNum, Integer deviceType);

    /**
     * 根据运行记录
     *
     * @param dzEquipmentRunTime
     * @return
     */
    DzEquipmentRunTime updateRunTimeRecord(DzEquipmentRunTime dzEquipmentRunTime);

    /**
     * 插入运行记录
     *
     * @param dzEquipmentRunTime
     * @return
     */
    DzEquipmentRunTime insertRunTimeRecord(DzEquipmentRunTime dzEquipmentRunTime);

    /**
     * 根据产品名称 获取产品编号类型
     *
     * @param productType
     * @return
     */
    @Cacheable(cacheNames = "cacheService.getProductType", key = "#productType", unless = "#result == null")
    WorkNumberName getProductType(String productType);

    /**
     * 查询是否存在每日停机次数记录
     *
     * @param lineNum
     * @param deviceNum
     * @param deviceType
     * @param orderNumber
     * @param nowLocalDate
     * @return
     */
    @Cacheable(cacheNames = "cacheService.getDayShoutDownTime", key = "#lineNum+#deviceNum+#deviceType+#orderNumber+#nowLocalDate", unless = "#result == null")
    DzDayShutDownTimes getDayShoutDownTime(String lineNum, String deviceNum, Integer deviceType, String orderNumber, LocalDate nowLocalDate);

    /**
     * 更新每日停机次数
     *
     * @param dzDayShutDownTimes
     * @return
     */
    @CachePut(cacheNames = "cacheService.getDayShoutDownTime", key = "#dzDayShutDownTimes.lineNo+#dzDayShutDownTimes.equipmentNo" +
            "+#dzDayShutDownTimes.equipmentType+#dzDayShutDownTimes.orderNo+#dzDayShutDownTimes.workDate")
    DzDayShutDownTimes updateByIdDzDayShutDownTimes(DzDayShutDownTimes dzDayShutDownTimes);

    /**
     * 设备编号就是工位编号，
     *
     * @param deviceCode
     * @param orderId
     * @param lineId
     * @return
     */
    @Cacheable(cacheNames = "cacheService.getStationId", key = "#deviceCode+#orderId+#lineId", unless = "#result == null")
    DzWorkStationManagement getStationId(String deviceCode, Long orderId, Long lineId);

    @Cacheable(cacheNames = "cacheService.getStationIdMergeCode", key = "#orderId+#lineId+#deviceCode+#mergeCode", unless = "#result == null")
    DzWorkStationManagement getStationIdMergeCode(Long orderId, Long lineId, String deviceCode, String mergeCode);
    /**
     * 根据订单序号和产线序号获取 订单和产线id
     *
     * @param orderCode
     * @param lineNo
     * @return
     */
    @Cacheable(cacheNames = "cacheService.getOrderNoLineNoId", key = "#orderCode+#lineNo", unless = "#result == null")
    OrderIdLineId getOrderNoLineNoId(String orderCode, String lineNo);
    @Cacheable(cacheNames = "cacheService.getOrderNoLineNoId", key = "'id'+#orderId+#lineId", unless = "#result == null")
    OrderIdLineId getOrderIdLineId(Long orderId, Long lineId);

    @Cacheable(cacheNames = "cacheServiceSig.getTypeLingEqNoDeviceSignalValue", key = "#orderNumber+#lineNum+#deviceType+#deviceNum")
    Integer getTypeLingEqNoDeviceSignalValue(String orderNumber, String lineNum, String deviceType, String deviceNum);


    @Cacheable(cacheNames = {"cacheService.getDeviceId"}, key = "#orderCode+#lineNo+#deviceCode+#deviceType", unless = "#result == null")
    Long getDeviceId(String orderCode, String lineNo, String deviceCode, String deviceType);

    MonOrder getMomOrderNoProducBarcode(String producBarcode, String orderNumber, String lineNum);

    @Cacheable(cacheNames = "cacheService.getSystemConfigDepart")
    String getSystemConfigDepart();


    /**
     * IOT数据表缓存设置状态数据
     *
     * @param deviceId 设备唯一属性
     * @return
     */
    @Cacheable(cacheNames = "cacheService.getIotTableDeviceState", key = "#deviceId")
    TimeAnalysisCmd getIotTableDeviceState(Long deviceId);

    /**
     * @param dzDataCollection
     * @return
     */
    @CachePut(cacheNames = "cacheService.getIotTableDeviceState", key = "#dzDataCollection.deviceId")
    TimeAnalysisCmd updateIotTableDeviceState(TimeAnalysisCmd dzDataCollection);

    /**
     * 设备用时分析上次执行时间
     *
     * @return
     */
    @Cacheable(cacheNames = "cacheService.robTimeAnalysis",key = "#type")
    Date robTimeAnalysis(String type);

    @CachePut(cacheNames = "cacheService.robTimeAnalysis",key = "#type")
    Date updateRobTimeAnalysis(String type,Date nowDate);


    /**
     *系统部署模式，单岛 或 多岛
     * @return
     */
    @Cacheable(cacheNames = "cacheService.getSystemConfigModel")
    String getSystemConfigModel();


    @CacheEvict(cacheNames = "cacheService.getNowOrder", allEntries = true)
    void delNowOrder();

    @CachePut(cacheNames = "cacheService.robTimeAnalysisAlarm", key = "#type")
    Date updateRobTimeAnalysisAlarm(String type, Date date);
}
