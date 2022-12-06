package com.dzics.common.dao;

import com.dzics.common.model.custom.CallMaterial;
import com.dzics.common.model.entity.MomWaitCallMaterial;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 等待叫料的订单 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-06-10
 */
public interface MomWaitCallMaterialMapper extends BaseMapper<MomWaitCallMaterial> {

    List<CallMaterial> getWorkStation(@Param("proTaskId") String proTaskId, @Param("stationCode") String stationCode);

}
