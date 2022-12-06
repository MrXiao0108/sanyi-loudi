package com.dzics.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.business.service.BusMaintainDeviceService;
import com.dzics.common.model.entity.DzMaintainDevice;
import com.dzics.common.model.entity.DzMaintainDeviceHistory;
import com.dzics.common.model.entity.DzMaintainDeviceHistoryDetails;
import com.dzics.common.model.entity.SysUser;
import com.dzics.common.model.request.base.BaseTimeLimit;
import com.dzics.common.model.request.device.maintain.*;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.device.maintain.MaintainDevice;
import com.dzics.common.model.response.device.maintain.MaintainRecord;
import com.dzics.common.model.response.device.maintain.MaintainRecordDetails;
import com.dzics.common.service.DzMaintainDeviceHistoryDetailsService;
import com.dzics.common.service.DzMaintainDeviceHistoryService;
import com.dzics.common.service.DzMaintainDeviceService;
import com.dzics.common.service.SysUserServiceDao;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ZhangChengJun
 * Date 2021/9/29.
 * @since
 */
@Service
public class BusMaintainDeviceServiceImpl implements BusMaintainDeviceService {
    @Autowired
    private DzMaintainDeviceService dzMaintainDevice;
    @Autowired
    private SysUserServiceDao sysUserServiceDao;
    @Autowired
    private DzMaintainDeviceHistoryService deviceHistoryService;
    @Autowired
    private DzMaintainDeviceHistoryDetailsService deviceHistoryDetailsService;

    @Override
    public Result getMaintainList(String sub, BaseTimeLimit pageLimit, MaintainDeviceParms parmsReq) {
        if (pageLimit.getPage() != -1){
            PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        }
        LocalDate startTime = pageLimit.getStartTime();
        LocalDate endTime = pageLimit.getEndTime();
        String field = pageLimit.getField();
        String type = pageLimit.getType();
        String equipmentNo = parmsReq.getEquipmentNo();
        Long lineId = StringUtils.isEmpty(parmsReq.getLineId()) ? null : Long.valueOf(parmsReq.getLineId());
        String states = parmsReq.getStates();
        LocalDate now = LocalDate.now();
        if (!StringUtils.isEmpty(states)) {
            if ("1".equals(states)) {
                List<MaintainDevice> maintainDevices = dzMaintainDevice.getMaintainListWait(lineId, equipmentNo, states, startTime, endTime, field, type, now);
                PageInfo<MaintainDevice> info = new PageInfo<>(maintainDevices);
                List<MaintainDevice> list = info.getList();
                return Result.ok(list, info.getTotal());
//            搜索等待下一次保养
            } else if ("2".equals(states)) {
//            搜索过期为保养
                List<MaintainDevice> maintainDevices = dzMaintainDevice.getMaintainListOver(lineId, equipmentNo, states, startTime, endTime, field, type, now);
                PageInfo<MaintainDevice> info = new PageInfo<>(maintainDevices);
                List<MaintainDevice> list = info.getList();
                return Result.ok(list, info.getTotal());
            }

        }
        List<MaintainDevice> maintainDevices = dzMaintainDevice.getMaintainList(lineId, equipmentNo, states, startTime, endTime, field, type);
        PageInfo<MaintainDevice> info = new PageInfo<>(maintainDevices);
        List<MaintainDevice> list = info.getList();
        setSateMaintainRecord(list, now);
        return Result.ok(list, info.getTotal());
    }

    @Override
    public Result addMaintainDevice(String sub, AddMaintainDevice parmsReq) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        DzMaintainDevice maintainDevice = new DzMaintainDevice();
        maintainDevice.setLineId(Long.valueOf(parmsReq.getLineId()));
        maintainDevice.setDeviceId(Long.valueOf(parmsReq.getDeviceId()));
        maintainDevice.setDateOfProduction(parmsReq.getDateOfProduction());
        maintainDevice.setMaintainDateBefore(parmsReq.getMaintainDateBefore());
        maintainDevice.setMaintainDateAfter(parmsReq.getMaintainDateAfter());
        maintainDevice.setFrequency(parmsReq.getFrequency());
        maintainDevice.setMultiple(parmsReq.getMultiple());
        maintainDevice.setUnit(parmsReq.getUnit());
        maintainDevice.setOrgCode(byUserName.getUseOrgCode());
        maintainDevice.setDelFlag(false);
        maintainDevice.setCreateBy(byUserName.getRealname());
        dzMaintainDevice.save(maintainDevice);
        return Result.ok();
    }

    @Override
    public Result updateMaintainDevice(String sub, AddMaintainDevice parmsReq) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        DzMaintainDevice maintainDevice = new DzMaintainDevice();
        maintainDevice.setMaintainId(parmsReq.getMaintainId());
        maintainDevice.setDeviceId(Long.valueOf(parmsReq.getDeviceId()));
        maintainDevice.setDateOfProduction(parmsReq.getDateOfProduction());
        maintainDevice.setMaintainDateBefore(parmsReq.getMaintainDateBefore());
        maintainDevice.setMaintainDateAfter(parmsReq.getMaintainDateAfter());
        maintainDevice.setMultiple(parmsReq.getMultiple());
        maintainDevice.setFrequency(parmsReq.getFrequency());
        maintainDevice.setUnit(parmsReq.getUnit());
        maintainDevice.setUpdateBy(byUserName.getRealname());
        dzMaintainDevice.updateById(maintainDevice);
        return Result.ok();
    }

    @Override
    public Result getMaintainRecord(String sub, BaseTimeLimit pageLimit, MaintainRecordParms parmsReq) {
        PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        List<MaintainRecord> maintainRecords = dzMaintainDevice.getMaintainRecord(pageLimit, parmsReq);
        PageInfo<MaintainRecord> info = new PageInfo<>(maintainRecords);
        return Result.ok(info.getList(), info.getTotal());
    }

    @Override
    public Result getMaintainRecordDetails(String sub, MaintainDetailsParms parmsReq) {
        List<MaintainRecordDetails> maintainRecordDetails = dzMaintainDevice.getMaintainRecordDetails(parmsReq.getMaintainHistoryId());
        return Result.OK(maintainRecordDetails);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result addMaintainRecord(String sub, AddMaintainRecord parmsReq) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        Date date = new Date();
        LocalDate now = LocalDate.now();
        String username = byUserName.getUsername();
        String realname = byUserName.getRealname();
        String useOrgCode = byUserName.getUseOrgCode();
        String maintainId = parmsReq.getMaintainId();
        DzMaintainDevice byId = dzMaintainDevice.getById(maintainId);
        String unit = byId.getUnit();//单位 年 月 日
        Integer frequency = byId.getFrequency();// 单位内执行次数
        Integer multiple = byId.getMultiple();
        Integer dayNum = 0;
        if ("年".equals(unit)) {
            dayNum = 365 * multiple / frequency;
        }
        if ("月".equals(unit)) {
            dayNum = 30 * multiple / frequency;
        }
        if ("周".equals(unit)) {
            dayNum = 7 * multiple / frequency;
        }
        DzMaintainDevice maintainDevice = new DzMaintainDevice();
        maintainDevice.setMaintainId(maintainId);
        maintainDevice.setMaintainDateBefore(now);
        maintainDevice.setMaintainDateAfter(now.plusDays(Long.valueOf(dayNum)));
        dzMaintainDevice.updateById(maintainDevice);
        DzMaintainDeviceHistory deviceHistory = new DzMaintainDeviceHistory();
        deviceHistory.setMaintainId(maintainId);
        deviceHistory.setMaintainDate(date);
        deviceHistory.setUsername(username);
        deviceHistory.setOrgCode(useOrgCode);
        deviceHistory.setDelFlag(false);
        deviceHistory.setCreateBy(realname);
        deviceHistoryService.save(deviceHistory);
        String maintainHistoryId = deviceHistory.getMaintainHistoryId();
        List<MaintainRecordDetails> recordDetails = parmsReq.getRecordDetails();
        if (CollectionUtils.isNotEmpty(recordDetails)) {
            List<DzMaintainDeviceHistoryDetails> historyDetails = new ArrayList<>();
            for (MaintainRecordDetails recordDetail : recordDetails) {
                DzMaintainDeviceHistoryDetails deviceHistoryDetails = new DzMaintainDeviceHistoryDetails();
                deviceHistoryDetails.setMaintainHistoryId(maintainHistoryId);
                deviceHistoryDetails.setMaintainItem(recordDetail.getMaintainItem());
                deviceHistoryDetails.setMaintainContent(recordDetail.getMaintainContent());
                deviceHistoryDetails.setOrgCode(useOrgCode);
                deviceHistoryDetails.setDelFlag(false);
                deviceHistoryDetails.setCreateBy(realname);
                historyDetails.add(deviceHistoryDetails);
            }
            deviceHistoryDetailsService.saveBatch(historyDetails);
        }
        return Result.ok();
    }

    @Override
    public Result delMaintainDevice(String sub, String maintainId) {
        dzMaintainDevice.removeById(maintainId);
        QueryWrapper<DzMaintainDeviceHistory> wp = new QueryWrapper<>();
        wp.eq("maintain_id", maintainId);
        List<DzMaintainDeviceHistory> list = deviceHistoryService.list(wp);
        if (CollectionUtils.isNotEmpty(list)) {
            List<String> collect = list.stream().map(s -> s.getMaintainHistoryId()).collect(Collectors.toList());
            QueryWrapper<DzMaintainDeviceHistoryDetails> wpDetails = new QueryWrapper<>();
            wpDetails.in("maintain_history_id", collect);
            deviceHistoryDetailsService.remove(wpDetails);
        }
        deviceHistoryService.remove(wp);
        return Result.ok();
    }

    private void setSateMaintainRecord(List<MaintainDevice> list, LocalDate now) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (MaintainDevice maintainDevice : list) {
            LocalDate localDate = LocalDate.parse(maintainDevice.getMaintainDateAfter(), df);
            if (localDate.compareTo(now) > 0) {
                maintainDevice.setStates("等待下一次保养");
            } else {
                maintainDevice.setStates("保养时间超时");
            }

        }
    }
}
