package com.dzics.common.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.common.model.entity.DzEquipment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.request.PutEquipmentDataStateVo;
import com.dzics.common.model.request.SelectEquipmentVo;
import com.dzics.common.model.response.*;
import com.dzics.common.model.response.device.DeviceMessage;
import com.dzics.common.model.response.equipmentstate.DzDataCollectionDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 设备表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
@Mapper
@Repository
public interface DzEquipmentMapper extends BaseMapper<DzEquipment> {

    List<EquipmentListDo> list(SelectEquipmentVo data);


    EquipmentDo getById(@Param("useOrgCode") String useOrgCode, @Param("id") Long id);

    List<String> listLingIdEquimentName(@Param("lineId") Long lineId);

    List<JCEquiment> listjcjqr(@Param("localDate") LocalDate localDate);

    DzEquipment listjcjqrdeviceid(@Param("deviceId") Long deviceId, @Param("localDate") LocalDate localDate);


    List<DzDataCollectionDo> getMachiningMessageStatus(@Param("lineNo") String lineNo, @Param("orderNum") String orderNum, @Param("now") LocalDate now);

    List<EquipmentDo> equipmentList(SelectEquipmentVo data);

    List<EquimentOrderLineId> getOrderLineEqId(@Param("list") List<String> list);

    List<DevcieNameId> getByIds(@Param("lineId") Long lineId);

    List<Long> getByOrderNoLineNo(@Param("orderNo") String orderNo, @Param("lineNo") String lineNo);

    List<DeviceMessage> getDevcieLineId(Long lineId);

    List<EquipmentStateDo> getEquipmentState(String lineId);

    Boolean putEquipmentDataState(PutEquipmentDataStateVo stateVo);

    default Map<Long,DzEquipment> getDeviceId(Long id) {
        QueryWrapper<DzEquipment> wp = new QueryWrapper<>();
        wp.eq("line_id", id);
        wp.eq("is_show", 1);
        List<DzEquipment> dzEquipments = selectList(wp);
        return dzEquipments.stream().collect(
                Collectors.toMap(DzEquipment::getId, dzEquipment -> dzEquipment));
    }
}
