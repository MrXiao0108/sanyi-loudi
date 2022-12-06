package com.dzics.common.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.model.agv.MomUpPoint;
import com.dzics.common.model.entity.MomMaterialPoint;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.request.mom.GetFeedingAgvVo;
import com.dzics.common.model.response.mom.GetFeedingAgvDo;
import com.dzics.common.model.response.mom.MaterialPointStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 料点编码 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-11-02
 */
@Mapper
@Repository
public interface MomMaterialPointMapper extends BaseMapper<MomMaterialPoint> {
    List<GetFeedingAgvDo> getAllPoints(GetFeedingAgvVo getFeedingAgvVo);

    MomUpPoint getStationCode(@Param("basketType") String basketType, @Param("orderCode") String orderCode, @Param("lineNo") String lineNo);

    List<GetFeedingAgvDo> exportAll(GetFeedingAgvVo getFeedingAgvVo);


    List<MaterialPointStatus> getMaterialPointStatus(@Param("orderCodeSys") String orderCodeSys);

    default List<String> getOrderNoLineNo(String orderNo, String lineNo) {
        QueryWrapper<MomMaterialPoint> wp = new QueryWrapper<>();
        wp.eq("order_no", orderNo);
        wp.eq("line_no", lineNo);
        List<MomMaterialPoint> momMaterialPoints = selectList(wp);
        if (CollectionUtils.isNotEmpty(momMaterialPoints)) {
            return momMaterialPoints.stream().map(MomMaterialPoint::getStationId).distinct().collect(Collectors.toList());
        }
        return null;
    }
}
