package com.dzics.business.service;

import com.dzics.business.model.vo.productiontask.station.AddWorkStation;
import com.dzics.business.model.vo.productiontask.station.SelWorkStation;
import com.dzics.business.model.vo.productiontask.station.UpdateWorkStation;
import com.dzics.common.model.request.PutProcessShowVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.productiontask.station.ResWorkStation;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;

/**
 * 工位管理接口
 *
 * @author ZhangChengJun
 * Date 2021/5/18.
 * @since
 */
public interface BusStationManageService {

    /**
     * 新增工位
     * @param workStation 工位信息
     * @param sub
     * @return
     */
    Result addWorkingStation(AddWorkStation workStation, String sub);

    /**
     * 编辑工位
     * @param station
     * @param sub
     * @return
     */
    @CacheEvict(cacheNames = {"cacheService.getStationIdMergeCode","cacheService.getStationId"},allEntries = true)
    Result updateWorkingStation(UpdateWorkStation station, String sub);

    /**
     * 删除工位
     * @param stationId
     * @param sub
     * @return
     */
    Result delWorkingStation(String stationId, String sub);

    /**
     * 获取工位列表
     * @param selWorkStation
     * @param sub
     * @return
     */
    Result<List<ResWorkStation>> getWorkingStation(SelWorkStation selWorkStation, String sub);

    /**
     * 工位是否展示
     * @param
     * @param
     * @return
     */
    Result putOnoff(PutProcessShowVo pShow);
}
