package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.DzWorkStationManagementMapper;
import com.dzics.common.model.entity.DzWorkStationManagement;
import com.dzics.common.model.request.PutProcessShowVo;
import com.dzics.common.model.request.mom.LineIdWorkStation;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.productiontask.station.ResWorkStation;
import com.dzics.common.model.response.workStation.GetWorkStationDo;
import com.dzics.common.service.DzWorkStationManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 工位表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-18
 */
@Service
public class DzWorkStationManagementServiceImpl extends ServiceImpl<DzWorkStationManagementMapper, DzWorkStationManagement> implements DzWorkStationManagementService {

    @Autowired
    private DzWorkStationManagementMapper stationManagementMapper;

    @Override
    public List<ResWorkStation> getWorkingStation(String field, String type, String stationCode, String workCode, String orderId, String lineId) {
        Long ordrIdl = null;
        if (!StringUtils.isEmpty(orderId)) {
            ordrIdl = Long.valueOf(orderId);
        }
        Long lineIdL = null;
        if (!StringUtils.isEmpty(lineId)) {
            lineIdL = Long.valueOf(lineId);
        }
        List<ResWorkStation> stations = stationManagementMapper.getWorkingStation(field, type, stationCode, workCode, ordrIdl, lineIdL);
        return stations;
    }

    @Override
    public DzWorkStationManagement getWorkStationCode(String deviceCode, Long orderId, Long lineId) {
        QueryWrapper<DzWorkStationManagement> wp = new QueryWrapper<>();
        wp.eq("order_id", orderId);
        wp.eq("line_id", lineId);
        wp.eq("station_code", deviceCode);
        DzWorkStationManagement one = getOne(wp);
        return one;
    }

    @Override
    public boolean putOnoffShow(PutProcessShowVo processShowVo) {
        DzWorkStationManagement stationManagement = new DzWorkStationManagement();
        stationManagement.setStationId(processShowVo.getStationId());
        stationManagement.setOnOff(processShowVo.getOnOff());
        int i = stationManagementMapper.updateById(stationManagement);
        return i > 0 ? true : false;
    }

    @Override
    public Result getLineId(LineIdWorkStation workStation) {
        QueryWrapper<DzWorkStationManagement> wp = new QueryWrapper<>();
        wp.eq("line_id", workStation.getLineId());
        wp.eq("on_off",1);
        List<DzWorkStationManagement> list = list(wp);
        return Result.OK(list);
    }

    @Override
    public Result getStationById(String lineId) {
        List <GetWorkStationDo> workStationByLineId = stationManagementMapper.getWorkStationByLineId(lineId);
        return Result.ok(workStationByLineId);
    }

    @Override
    public DzWorkStationManagement getStationIdMergeCode(String mergeCode, String deviceCode, Long orderId, Long lineId) {
        QueryWrapper<DzWorkStationManagement> wp = new QueryWrapper<>();
        wp.eq("order_id", orderId);
        wp.eq("line_id", lineId);
        wp.ne("station_code", deviceCode);
        wp.eq("merge_code",mergeCode);
        DzWorkStationManagement one = getOne(wp);
        return one;
    }
}
