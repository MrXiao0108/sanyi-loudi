package com.dzics.common.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.entity.DzDetectorData;
import com.dzics.common.model.request.SelectTrendChartVo;
import com.dzics.common.model.response.DetectionData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 产品检测设置默认模板 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-04
 */
@Mapper
@Repository
public interface DzDetectorDataMapper extends BaseMapper<DzDetectorData> {

    List<Map<String, Object>> getGroupKey(@Param("list") List<String> list);

    List<DetectionData> getDataValue(@Param("groupKey") String groupKey);

    List<BigDecimal> selectTrendChart(SelectTrendChartVo selectTrendChartVo);

    List<BigDecimal> getChartsData(@Param("productNo") String productNo, @Param("orderNo") String orderNo, @Param("lineNo") String lineNo);

    List<DzDetectorData> groupBuby(@Param("productNo") String productNo, @Param("detectionResult") Integer detectionResult, @Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("orgCode") String orgCode);

    List<DzDetectorData> groupBubyData();

}
