package com.dzics.business.service;

import com.dzics.common.model.custom.MachiningNumTotal;
import com.dzics.common.model.request.kb.GetOrderNoLineNo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.charts.WorkShiftSum;

import java.time.LocalDate;
import java.util.List;

public interface BuProductionQuantityService {

    /**
     * 根据订单产线号获取绑定设备的五日内产量
     *
     * @return
     */
    Result getOutputByLineId(GetOrderNoLineNo getOrderNoLineNo);

    /**
     * 根据订单产线号查询所有设备当日用时分析(旧)-不分时段
     */
    Result getEquipmentAvailable(GetOrderNoLineNo getOrderNoLineNo);

    /**
     * 根据订单产线号查询近五日稼动率
     *
     * @return
     */
    Result getProductionPlanFiveDay(GetOrderNoLineNo getOrderNoLineNo);

    /**
     * 根据订单产线号查询近五日产线计划分析
     *
     * @return
     */
    Result getPlanAnalysis(GetOrderNoLineNo getOrderNoLineNo);

    /**
     * 订单产线号查询刀具信息数据
     *
     * @return
     */
    Result getToolInfoData(GetOrderNoLineNo getOrderNoLineNo);


    /**
     * 订单产线号查询产线当月每日的生产 合格/不合格数量
     *
     * @return
     */
    Result getMonthlyCapacity(GetOrderNoLineNo getOrderNoLineNo);

    /**
     * 订单产线号查询产线当日生产 合格/不合格数量，当日班次生产合格，不合格
     *
     * @return
     */
    Result getMonthlyCapacityShift(GetOrderNoLineNo getOrderNoLineNo);

    List<WorkShiftSum> getWorkShiftSum(Long statisticsEquimentId);

    /**
     * 查询产品最新的4条检测数据
     *
     * @return
     */
    Result inspectionData(GetOrderNoLineNo getOrderNoLineNo);


    /**
     * 根据订单和产线序号查询该产线下（所有机器人和机床）的（日产、总产）
     *
     * @param getOrderNoLineNo
     * @return
     */
    Result getDeviceproductionQuantity(GetOrderNoLineNo getOrderNoLineNo);

    List<MachiningNumTotal> machiningNumTotals(LocalDate now, List<String> collect);

    /**
     * 获取产线的日产
     *
     * @return
     */
    Result getLineSumQuantity(GetOrderNoLineNo orderNoLineNo);


    /**
     * 查询未绑二维码的检测数据(未绑定二维码的检测记录)
     *
     * @return
     */
    Result unBoundQrCode(GetOrderNoLineNo getOrderNoLineNo);


    /**
     * 查询已绑二维码检测数据(已经绑定二维码的检测记录)
     *
     * @return
     */
    Result boundQrCode(GetOrderNoLineNo getOrderNoLineNo);

    /**
     * 获取订单的下设备的 当日 投入 产出 不良品 数量
     *
     * @param orderNoLineNo
     * @return
     */
    Result getInputOutputDefectiveProducts(GetOrderNoLineNo orderNoLineNo);


    /**
     * 获取订单产线下设备的稼动信息
     *
     * @param orderNoLineNo
     * @return
     */
    Result getSocketUtilization(GetOrderNoLineNo orderNoLineNo);


    /**
     * 产线月生产 合格/不合格 数量
     *
     * @param orderNoLineNo
     * @return
     */
    Result getMonthData(GetOrderNoLineNo orderNoLineNo);

    /**
     * 产线月和班次生产 合格/不合格 数量,
     *
     * @param orderNoLineNo
     * @return
     */
    Result getMonthDataShift(GetOrderNoLineNo orderNoLineNo);

    List<WorkShiftSum> getMouthWorkShiftSum(Long eqId);

    /**
     * 获取当前日期数据
     *
     * @param orderNoLineNo
     * @return
     */
    Result getCurrentDate(GetOrderNoLineNo orderNoLineNo);

    /**
     * 获取五日内生产数量(NG和OK)
     */
    Result getDataNgAndOk(GetOrderNoLineNo orderNoLineNo);

    /**
     * 设备今日用时分析(新)
     */
    Result equipmentTimeAnalysis(GetOrderNoLineNo orderNoLineNo);

    /**
     * 设备用时分析包含 作业 待机 故障 关机
     * @param orderNoLineNo
     * @return
     */
    Result getDeviceTimeAnalysis(GetOrderNoLineNo orderNoLineNo);

    /**
     * 当日生产合格率
     */
    Result dailyPassRate(GetOrderNoLineNo orderNoLineNo);

    /**
     * 日生产计划，以及班次生产详情
     */
    Result shiftProductionDetails(GetOrderNoLineNo orderNoLineNo);

    /**
     * 查询当日24小时综合报表
     */
    Result dailyProductionDetails(GetOrderNoLineNo orderNoLineNo);

    /**
     * 日产综合报表(查询当月每日生产数量)
     */
    Result productionDailyReport(GetOrderNoLineNo orderNoLineNo);

    /**
     * 设备 日产/时产
     */
    Result dayAndHour(GetOrderNoLineNo orderNoLineNo);

    /**
     * 贵万江机器人今日用时统计
     */
    Result robotRunTime(GetOrderNoLineNo orderNoLineNo);

    /**
     * 产线设备当日产量分析柱状图1.0版本
     */
    Result allEquipmentDailyCapacity(GetOrderNoLineNo orderNoLineNo);
    /**
     * 产线设备当日产量分析柱状图 1.1版本(可以后台动态控制设备是否展示)
     */
    Result allEquipmentDailyCapacityTwo(GetOrderNoLineNo orderNoLineNo);
    /**
     *产线生产信息总览 （查询类容包含生产订单号,订单开始时间，订单实际生产数/订单总数）
     */
    Result getLineProductionInfo(GetOrderNoLineNo orderNoLineNo);

    /**
     *查询机床A和B 当月31天产量和当天24小时产量
     * (贵万江初次提的这个需求-2021-12-24)
     */
    Result getMachineToolData(GetOrderNoLineNo orderNoLineNo);

    /**
     * 查询产线达成率，合格率
     */
    Result achievingAndQualified(GetOrderNoLineNo orderNoLineNo);
    /**
     * 查询产线指定天数的生产数据(例如5天,7天)
     */
    Result getProductionLineNumberByDay(GetOrderNoLineNo orderNoLineNo);
}
