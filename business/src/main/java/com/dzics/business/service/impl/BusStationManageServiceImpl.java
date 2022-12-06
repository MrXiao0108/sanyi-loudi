package com.dzics.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.business.model.vo.productiontask.station.AddWorkStation;
import com.dzics.business.model.vo.productiontask.station.SelWorkStation;
import com.dzics.business.model.vo.productiontask.station.UpdateWorkStation;
import com.dzics.business.service.BusStationManageService;
import com.dzics.common.enums.Message;
import com.dzics.common.exception.CustomException;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.entity.DzWorkStationManagement;
import com.dzics.common.model.entity.DzWorkingProcedure;
import com.dzics.common.model.entity.SysUser;
import com.dzics.common.model.request.PutProcessShowVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.productiontask.station.ResWorkStation;
import com.dzics.common.service.DzWorkStationManagementService;
import com.dzics.common.service.DzWorkingProcedureService;
import com.dzics.common.service.SysUserServiceDao;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/5/18.
 * @since
 */
@Service
@Slf4j
public class BusStationManageServiceImpl implements BusStationManageService {
    @Autowired
    private DzWorkStationManagementService dzWorkStationManagementService;
    @Autowired
    private DzWorkingProcedureService dzWorkingProcedureService;
    @Autowired
    private SysUserServiceDao sysUserServiceDao;

    @Override
    public Result addWorkingStation(AddWorkStation workStation, String sub) {
        String workingProcedureId = workStation.getWorkingProcedureId();
        DzWorkingProcedure byId = dzWorkingProcedureService.getById(workingProcedureId);
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        QueryWrapper<DzWorkStationManagement> wp = new QueryWrapper<>();
        wp.eq("sort_code", workStation.getSortCode());
        wp.eq("line_id", byId.getLineId());
        wp.eq("order_id", byId.getOrderId());
        List<DzWorkStationManagement> sort_code = dzWorkStationManagementService.list(wp);
        if (sort_code.size() > 0) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_117);
        }
        QueryWrapper<DzWorkStationManagement> wp2 = new QueryWrapper<>();
        wp2.eq("station_code", workStation.getStationCode());
        wp2.eq("line_id", byId.getLineId());
        wp2.eq("order_id", byId.getOrderId());
        List<DzWorkStationManagement> workCode = dzWorkStationManagementService.list(wp2);
        if (workCode.size() > 0) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR44);
        }

        DzWorkStationManagement management = new DzWorkStationManagement();
        management.setOrderId(byId.getOrderId());
        management.setLineId(byId.getLineId());
        management.setWorkingProcedureId(workingProcedureId);
        management.setStationName(workStation.getStationName());
        management.setStationCode(workStation.getStationCode());
        management.setSortCode(workStation.getSortCode());
        management.setOrgCode(byUserName.getUseOrgCode());
        management.setDelFlag(false);
        management.setDzStationCode(workStation.getDzStationCode());
        management.setNgCode(workStation.getNgCode());
        management.setOutFlag(workStation.getOutFlag());
        management.setMergeCode(workStation.getMergeCode());
        management.setCreateBy(byUserName.getUsername());
        dzWorkStationManagementService.save(management);
        return Result.ok();
    }

    @Override
    public Result updateWorkingStation(UpdateWorkStation station, String sub) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        String workingProcedureId = station.getWorkingProcedureId();
        DzWorkingProcedure byId = dzWorkingProcedureService.getById(workingProcedureId);
        QueryWrapper<DzWorkStationManagement> wrapper = new QueryWrapper<DzWorkStationManagement>()
                .eq("sort_code", station.getSortCode())
                .eq("line_id", byId.getLineId())
                .eq("order_id", byId.getOrderId())
                .notIn("station_id", station.getStationId());
        List<DzWorkStationManagement> sort_code = dzWorkStationManagementService.list(wrapper);
        if (sort_code.size() > 0) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_117);
        }

        QueryWrapper<DzWorkStationManagement> wp2 = new QueryWrapper<>();
        wp2.eq("station_code", station.getStationCode());
        wp2.eq("line_id", byId.getLineId());
        wp2.eq("order_id", byId.getOrderId())
        .notIn("station_id", station.getStationId());
        List<DzWorkStationManagement> workCode = dzWorkStationManagementService.list(wp2);
        if (workCode.size() > 0) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR44);
        }


        DzWorkStationManagement stationManagement = new DzWorkStationManagement();
        stationManagement.setOrderId(byId.getOrderId());
        stationManagement.setLineId(byId.getLineId());
        stationManagement.setDzStationCode(station.getDzStationCode());
        stationManagement.setStationId(station.getStationId());
        stationManagement.setWorkingProcedureId(workingProcedureId);
        stationManagement.setStationName(station.getStationName());
        stationManagement.setStationCode(station.getStationCode());
        stationManagement.setUpdateBy(byUserName.getUsername());
        stationManagement.setSortCode(station.getSortCode());
        stationManagement.setNgCode(station.getNgCode());
        stationManagement.setOutFlag(station.getOutFlag());
        stationManagement.setMergeCode(station.getMergeCode());
        dzWorkStationManagementService.updateById(stationManagement);
        return Result.ok();
    }

    @Override
    public Result delWorkingStation(String stationId, String sub) {
        boolean b = dzWorkStationManagementService.removeById(stationId);
        return Result.ok();
    }

    @Override
    public Result<List<ResWorkStation>> getWorkingStation(SelWorkStation selWorkStation, String sub) {
        if (selWorkStation.getPage() != -1) {
            PageHelper.startPage(selWorkStation.getPage(), selWorkStation.getLimit());
        }
        List<ResWorkStation> stations = dzWorkStationManagementService.getWorkingStation(selWorkStation.getField(), selWorkStation.getType(),
                selWorkStation.getStationCode(), selWorkStation.getWorkCode(), selWorkStation.getOrderId(), selWorkStation.getLineId());
        PageInfo<ResWorkStation> info = new PageInfo<>(stations);
        return Result.ok(info.getList(), Long.valueOf(info.getTotal()));
    }

    @Override
    public Result putOnoff(PutProcessShowVo pShow) {
        boolean a = dzWorkStationManagementService.putOnoffShow(pShow);
        return Result.ok();
    }


}
