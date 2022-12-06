package com.dzics.common.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.entity.DzWorkStationManagement;
import com.dzics.common.model.request.PutProcessShowVo;
import com.dzics.common.model.response.mom.DzicsStationCode;
import com.dzics.common.model.response.productiontask.station.ResWorkStation;
import com.dzics.common.model.response.productiontask.stationbg.StationModelAll;
import com.dzics.common.model.response.workStation.GetWorkStationDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 工位表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-18
 */
@Mapper
@Repository
public interface DzWorkStationManagementMapper extends BaseMapper<DzWorkStationManagement> {

    List<ResWorkStation> getWorkingStation(@Param("field") String field, @Param("type") String type, @Param("stationCode") String stationCode, @Param("workCode") String workCode, @Param("orderId") Long orderId, @Param("lineId") Long lineId);

    List<StationModelAll> getSortPosition(@Param("orderId") Long orderId, @Param("lineId") Long lineId);

    Boolean putOnoffShow(PutProcessShowVo processShowVo);

    List<GetWorkStationDo> getWorkStationByLineId(@Param("lineId") String lineId);

    /**
     * @param orderId 订单
     * @param lineId  产线
     * @param onOff   1 展示 0 不展示
     * @return 返回该产线需要展示的工位
     */
    List<Map<String, Object>> getSortPositionOnOff(@Param("orderId") Long orderId, @Param("lineId") Long lineId, @Param("onOff") int onOff);

    List<DzicsStationCode> getDzicsStationCode(@Param("lineId") String lineId);
}
