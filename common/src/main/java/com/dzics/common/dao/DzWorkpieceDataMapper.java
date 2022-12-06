package com.dzics.common.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.model.dto.check.DayCheckModel;
import com.dzics.common.model.entity.DzWorkpieceData;
import com.dzics.common.model.request.DetectorDataQuery;
import com.dzics.common.model.response.GetDetectionOneDo;
import com.dzics.common.model.response.productiontask.stationbg.CheckItems;
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
 * 设备检测数据V2新版记录 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-07
 */
@Mapper
@Repository
public interface DzWorkpieceDataMapper extends BaseMapper<DzWorkpieceData> {
    List<Map<String, Object>> newestThreeData(@Param("list") List<String> list);

    List<DzWorkpieceData> getQrCodeOutOk(@Param("qrCode") String qrCode);

    List<DzWorkpieceData> getQrCodeProduct(@Param("qrCode") String qrCode);

    List<CheckItems> getProductIdCheckItems(@Param("productId") String productId, @Param("orderId") Long orderId, @Param("lineId") Long lineId);

    /**
     * 未绑定二维码的检测数据
     *
     * @return
     */
    List<Map<String, Object>> notBoundQrCode(List<String> idList);


    List<String> getNewestThreeDataId(@Param("orderNo") String orderNo, @Param("lineNo") String lineNo, @Param("size") int size);

    List<String> getWorkPieceData(@Param("orderNo") String orderNo, @Param("lineNo") String lineNo, @Param("name") String name, @Param("size") Integer size);

    /**
     * 查询检测数据列表
     *
     * @return
     */
    List<Map<String, Object>> selDetectorData(DetectorDataQuery detectorDataQuery);


    List<BigDecimal> getChartsData(@Param("productNo") String productNo, @Param("orderNo") String orderNo, @Param("lineNo") String lineNo, @Param("fieldName") String fieldName);

    /**
     * 查询最近9条检测记录
     *
     * @param lineNo
     * @param orderNo
     * @param tableColVal
     * @param outOkVal
     * @return
     */
    List<GetDetectionOneDo> selectDataList(@Param("tableColVal") String tableColVal,
                                           @Param("outOkVal") String outOkVal,
                                           @Param("orderNo") String orderNo,
                                           @Param("lineNo") String lineNo);

    List<BigDecimal> getDetectionByMachine(@Param("productNo") String productNo,
                                           @Param("orderNo") String orderNo,
                                           @Param("lineNo") String lineNo,
                                           @Param("fieldName") String fieldName,
                                           @Param("machine") String machine);

    List<DzWorkpieceData> getOneWorkpieceData(@Param("orderNo") String orderNo,
                                              @Param("lineNo") String lineNo,
                                              @Param("productName") String productName,
                                              @Param("producBarcode") String producBarcode,
                                              @Param("newDate") LocalDate newDate,
                                              @Param("startDate") Date startDate,
                                              @Param("endDate") Date endDate,
                                              @Param("number") Integer number);

    DzWorkpieceData getLastDzWorkpieceData(@Param("orderNo") String orderNo, @Param("lineNo") String lineNo, @Param("now") String now);

    /**
     * 精加工获取前缀 看板展示
     * */
    List<DayCheckModel> getDayWorkModel(@Param("month") String month, @Param("orderNo") String orderNo, @Param("lineNo") String lineNo);

    /**
     * 粗加工获取前缀 看板展示
     * */
    List<DayCheckModel> getDayWorkModelCjg(@Param("month") String month, @Param("orderNo") String orderNo, @Param("lineNo") String lineNo);

    default DzWorkpieceData getOrderNoLineNoQrcode(String orderNo, String qrC) {
        QueryWrapper<DzWorkpieceData> wp = new QueryWrapper<>();
        wp.eq("order_no", orderNo);
        wp.eq("produc_barcode", qrC);
        List<DzWorkpieceData> data = selectList(wp);
        if (CollectionUtils.isNotEmpty(data)) {
            return data.get(data.size() - 1);
        }
        return null;
    }

    List<Map<String, Object>> newestThreeDataMom(@Param("list") List<String> list);

    List<Map<String, Object>> newestThreeDataMomSingle(@Param("id") String id);

    Map<String, Object>getMaErBiaoDetectionMonitor(@Param("orderNo")String orderNo,@Param("lineNo")String lineNo,@Param("qrCode")String qrCode);
}
