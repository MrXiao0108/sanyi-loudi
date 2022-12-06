package com.dzics.common.service;

import com.dzics.common.model.entity.DzDataCollection;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.common.model.response.timeanalysis.TimeAnalysisCmd;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-07-29
 */
public interface DzDataCollectionService extends IService<DzDataCollection> {

    /**
     * 根据设备类型获取设备状态指令信号
     * @return
     * @param shardingParameter
     */
    List<TimeAnalysisCmd> getDeviceTypeCmdSingal(String shardingParameter);

    TimeAnalysisCmd getDeviceId(Long deviceId);



}
