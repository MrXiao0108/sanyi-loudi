package com.dzics.common.dao;

import com.dzics.common.model.custom.OrderIdLineId;
import com.dzics.common.model.entity.DzProductionLine;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.request.SelectLineVo;
import com.dzics.common.model.response.LineDo;
import com.dzics.common.model.response.commons.Lines;
import com.dzics.common.model.response.feishi.LineListDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 产线表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
@Mapper
@Repository
public interface DzProductionLineMapper extends BaseMapper<DzProductionLine> {

    List<LineDo> list(SelectLineVo data);

    List<LineListDo> allLineList();

    List<String> selectLineIdList();


    List<Lines> listOrderId(@Param("orderId") String orderId);

    OrderIdLineId getOrderNoAndLineNo(@Param("orderCode") String orderCode, @Param("lineNo") String lineNo);

    Long getLineEqmentId(@Param("orderNo") String orderNo, @Param("lineNo") String lineNo);

    List<Lines> getByOerderId(@Param("orderId") Long ordeId);

    OrderIdLineId getOrderIdLineId(@Param("orderId") Long orderId, @Param("lineId") Long lineId);

    String getlineType(@Param("orderNo") String orderNo, @Param("lineNo") String lineNo);
}
