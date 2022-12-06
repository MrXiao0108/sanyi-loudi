package com.dzics.common.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.custom.LineNumberTotal;
import com.dzics.common.model.custom.MachiningNumTotal;
import com.dzics.common.model.custom.SocketProQuantity;
import com.dzics.common.model.custom.WorkNumberName;
import com.dzics.common.model.entity.DzEquipmentProNum;
import com.dzics.common.model.request.SelectEquipmentDataVo;
import com.dzics.common.model.request.plan.SelectEquipmentProductionVo;
import com.dzics.common.model.request.plan.SelectProductionDetailsVo;
import com.dzics.common.model.response.EquipmentDataDo;
import com.dzics.common.model.response.GetMonthlyCapacityDo;
import com.dzics.common.model.response.charts.EquipmentDataDetailsDo;
import com.dzics.common.model.response.feishi.DayDataResultDo;
import com.dzics.common.model.response.homepage.QualifiedAndOutputDo;
import com.dzics.common.model.response.plan.PlanRecordDetailsListDo;
import com.dzics.common.model.response.plan.SelectEquipmentProductionDetailsDo;
import com.dzics.common.model.response.plan.SelectEquipmentProductionDo;
import org.apache.ibatis.annotations.Mapper;
import com.dzics.common.model.response.plan.SelectProductionDetailsDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 设备生产数量表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
@Mapper
@Repository
public interface DzEquipmentProNumMapper extends BaseMapper<DzEquipmentProNum> {

    /**
     * 多条件查询设备数据
     *
     * @param selectEquipmentDataVo
     * @return
     */
    List<EquipmentDataDo> listEquipmentData(SelectEquipmentDataVo selectEquipmentDataVo);

    List<EquipmentDataDo> listEquipmentDataV2(SelectEquipmentDataVo selectEquipmentDataVo);

    List<PlanRecordDetailsListDo> detailsList(@Param("lineId") Long lineId,
                                              @Param("detectorTime") String detectorTime,
                                              @Param("tableKey") String tableKey, @Param("statisticsEquimentId") Long statisticsEquimentId);

    /**
     * 查询产品生产明细
     *
     * @param selectProductionDetailsVo
     * @return
     */
    List<SelectProductionDetailsDo> list(SelectProductionDetailsVo selectProductionDetailsVo);


    List<EquipmentDataDetailsDo> getEquipmentDataDetails(@Param("equimentId") Long equimentId,
                                                         @Param("startTime") Date startTime,
                                                         @Param("endTime") Date endTime,
                                                         @Param("tableKey") String tableKey);


    /**
     * 信号模式
     * 根据日期 产线序号 获取所有设备中生产数量最小的设备生产的数据
     *
     * @param now                  日期
     * @param linNo                产线序号
     * @param statisticsEquimentId 统计产线设备id
     * @return
     */
    Map<String, BigDecimal> workNowLocalDate(@Param("now") LocalDate now, @Param("linNo") String linNo, @Param("statisticsEquimentId") Long statisticsEquimentId);

    /**
     * 脉冲模式
     * 根据日期 产线序号 获取所有设备中生产数量最小的设备生产的数据
     *
     * @param now                  日期
     * @param linNo                产线序号
     * @param statisticsEquimentId 统计产线设备id
     * @return
     */
    Map<String, BigDecimal> workNowLocalDateSignal(@Param("now") LocalDate now, @Param("linNo") String linNo, @Param("statisticsEquimentId") Long statisticsEquimentId);

    /**
     * 设备生产数量明细列表
     *
     * @param selectProductionDetailsVo
     * @return
     */
    List<SelectEquipmentProductionDo> listProductionEquipment(SelectEquipmentProductionVo selectProductionDetailsVo);

    /**
     * 设备生产数量详情列表
     *
     * @param equimentId
     * @param workDate
     * @param tableKey
     * @return
     */
    List<SelectEquipmentProductionDetailsDo> listProductionEquipmentDetails(@Param("equimentId") Long equimentId, @Param("workDate") Date workDate, @Param("tableKey") String tableKey);

    List<DayDataResultDo> dayData(@Param("tableKey") String tableKey,
                                  @Param("first") String first,
                                  @Param("last") String last,
                                  @Param("equimentId") Long equimentId);

    DayDataResultDo monthData(@Param("month") String month,
                              @Param("tableKey") String tableKey,
                              @Param("equimentId") Long equimentId);


    List<DayDataResultDo> dayDataByLine(@Param("tableKey") String tableKey,
                                        @Param("first") String first,
                                        @Param("last") String last,
                                        @Param("lineId") Long lineId);

    DayDataResultDo monthDataByLine(@Param("month") String month,
                                    @Param("tableKey") String tableKey,
                                    @Param("lineId") Long lineId);

    /**
     * 查询当日生产相关数据
     *
     * @param tableKey 表名
     * @param lineId   产线名
     * @param nowDate  当天日期
     * @return
     */
    QualifiedAndOutputDo outputCapacity(@Param("tableKey") String tableKey,
                                        @Param("lineId") Long lineId,
                                        @Param("nowDate") String nowDate);

    /**
     * 根据设备id查询设备五日内生产量
     *
     * @param eqId
     * @param tableKey
     * @return
     */
    List<Long> getOutputByEqId(@Param("eqId") Long eqId, @Param("tableKey") String tableKey);


    List<MachiningNumTotal> getEqIdData(@Param("now") LocalDate now, @Param("list") List<String> list, @Param("tableKey") String tableKey);

    List<MachiningNumTotal> getEqIdDataWorkShift(@Param("now") LocalDate now, @Param("list") List<String> list, @Param("tableKey") String tableKey);

    List<WorkNumberName> getProductName(@Param("tableKey") String tableKey, @Param("id") Long id);

    List<GetMonthlyCapacityDo> getMonthlyCapacity(@Param("tableKey") String tableKey, @Param("eqId") Long eqId);

    List<MachiningNumTotal> getEqIdDataTotal(@Param("list") List<String> list);

    LineNumberTotal getLineSumQuantity(@Param("now") LocalDate now, @Param("eqId") Long eqId, @Param("tableKey") String tableKey);

    LineNumberTotal getLineSumQuantityWorkShitf(@Param("now") LocalDate now, @Param("eqId") Long eqId, @Param("tableKey") String tableKey);

    List<SocketProQuantity> getInputOutputDefectiveProducts(@Param("tableKey") String tableKey, @Param("deviceIds") List<Long> deviceIds, @Param("now") LocalDate now);

    Long getSumData(@Param("tableKey") String tableKey, @Param("eqID") Long eqID);

    /**
     * 获取五日内生产数量 NG和OK
     *
     * @param eqId
     * @param tableKey
     * @return
     */
    List<Map<String, Object>> getDataNgAndOk(@Param("eqId") Long eqId, @Param("tableKey") String tableKey);

    QualifiedAndOutputDo dailyPassRate(@Param("tableKey") String tableKey, @Param("equipmentId") Long equipmentId, @Param("date") String date);

    List<Map<String, Object>> workNowLocalDateSignalIds(@Param("now") LocalDate now, @Param("list") List<Long> list);

    List<Map<String, Object>> workNowLocalDateIds(@Param("now") LocalDate now, @Param("list") List<Long> list);

    List<Map<String, Object>> dayAndHour(@Param("tableKey") String tableKey, @Param("eqIds") List<Long> eqIds, @Param("today") String today);

    List<Map<String, Object>> allEquipmentDailyCapacity(@Param("tableKey") String tableKey, @Param("orderNo") String orderNo, @Param("lineNo") String lineNo, @Param("nowDate") String nowDate);

    List<Map<String, Object>> getWorkShift(@Param("tableKey") String tableKey, @Param("equimentId") Long equimentId, @Param("mouth") String mouth);
}
