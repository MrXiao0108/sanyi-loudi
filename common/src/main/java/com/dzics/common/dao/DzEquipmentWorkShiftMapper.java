package com.dzics.common.dao;

import com.dzics.common.model.entity.DzEquipmentWorkShift;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.request.AddWorkShiftVo;
import com.dzics.common.model.response.DzEquipmentWorkShiftDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 设备工作班次表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
@Mapper
@Repository
public interface DzEquipmentWorkShiftMapper extends BaseMapper<DzEquipmentWorkShift> {


    void del(@Param("id") Long id, @Param("useOrgCode") String useOrgCode);

    /**
     * 查詢起始时间和结束时间在同一天的班次 （例如:8:00:00--23:00:00）
     * @param orgCode
     * @param id
     * @param dd
     * @return
     */
    List<DzEquipmentWorkShift> getPresentWorkShift(@Param("orgCode") String orgCode,@Param("id") Long id,@Param("dd") String dd);
    /**
     * 查詢起始时间和结束时间跨天的班次 （例如:22:00:00--05:00:00）
     * @param orgCode
     * @param id
     * @param dd
     * @return
     */
    List<DzEquipmentWorkShift> getPresentWorkShift2(@Param("orgCode") String orgCode,@Param("id") Long id,@Param("dd") String dd);

    List<DzEquipmentWorkShiftDo> getListByLineId(@Param("lineId")Long lineId, @Param("useOrgCode")String useOrgCode);
}
