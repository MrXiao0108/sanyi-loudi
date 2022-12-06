package com.dzics.common.service;

import com.dzics.common.model.entity.DzWorkStationManagement;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.common.model.request.PutProcessShowVo;
import com.dzics.common.model.request.mom.LineIdWorkStation;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.productiontask.station.ResWorkStation;

import java.util.List;

/**
 * <p>
 * 工位表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-18
 */
public interface DzWorkStationManagementService extends IService<DzWorkStationManagement> {

    /**
     * 工位列表
     *
     * @param field
     * @param type
     * @param stationCode
     * @param workCode
     * @param orderId
     * @param lineId
     * @return
     */
    List<ResWorkStation> getWorkingStation(String field, String type, String stationCode, String workCode, String orderId, String lineId);

    DzWorkStationManagement getWorkStationCode(String deviceCode, Long orderId, Long lineId);

    boolean putOnoffShow(PutProcessShowVo processShowVo);

    Result getLineId(LineIdWorkStation workStation);

    Result getStationById(String lineId);

    DzWorkStationManagement getStationIdMergeCode(String mergeCode, String deviceCode, Long orderId, Long lineId);
}
