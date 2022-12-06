package com.dzics.common.dao;

import com.dzics.common.model.entity.DzDataCollection;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.response.timeanalysis.TimeAnalysisCmd;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-07-29
 */
public interface DzDataCollectionMapper extends BaseMapper<DzDataCollection> {

    List<TimeAnalysisCmd> getDeviceTypeCmdSingal(@Param("shardingParameter") String shardingParameter);

    TimeAnalysisCmd getDeviceId(@Param("deviceId") Long deviceId);
}
