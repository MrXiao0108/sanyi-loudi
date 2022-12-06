package com.dzics.common.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.entity.DzEquipmentDowntimeRecord;
import com.dzics.common.model.request.GetByEquipmentNoVo;
import com.dzics.common.model.response.GetByEquipmentNoDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 设备停机记录 Mapper 接口 xX
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-20
 */
@Mapper
@Repository
public interface DzEquipmentDowntimeRecordMapper extends BaseMapper<DzEquipmentDowntimeRecord> {

    List<GetByEquipmentNoDo> getByEquipmentNo(GetByEquipmentNoVo getByEquipmentNoVo);

    List<BigDecimal> operation(@Param("id") Long id,
                               @Param("startTime") Date startTime,
                               @Param("endTime") Date endTime);

    List<String> selectDate(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    Long getTimeDuration(@Param("lineNo") String lineNo, @Param("orderNo") String orderNo, @Param("equipmentNo") String equipmentNo, @Param("equipmentType") Integer equipmentType, @Param("startTime") LocalDate startTime, @Param("endTime") LocalDate endTime);

    Long getTimeDurationNowDay(@Param("equipmentNo") String equipmentNo, @Param("equipmentType") Integer equipmentType, @Param("dayNow") LocalDate dayNow);

    Long getTimeDurationHistory(@Param("equipmentNo") String equipmentNo, @Param("equipmentType") Integer equipmentType);
}
