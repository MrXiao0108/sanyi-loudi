package com.dzics.business.service.impl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.business.service.CheckHistoryService;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.entity.DzCheckHistory;
import com.dzics.common.model.entity.DzCheckHistoryItem;
import com.dzics.common.model.entity.DzEquipment;
import com.dzics.common.model.entity.SysUser;
import com.dzics.common.model.request.devicecheck.DeviceCheckItemVo;
import com.dzics.common.model.request.devicecheck.DeviceCheckVo;
import com.dzics.common.model.request.devicecheck.GetDeviceCheckVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.devicecheck.GetDeviceCheckDo;
import com.dzics.common.service.DzCheckHistoryItemService;
import com.dzics.common.service.DzCheckHistoryService;
import com.dzics.common.service.DzEquipmentService;
import com.dzics.common.service.SysUserServiceDao;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CheckHistoryServiceImpl implements CheckHistoryService {
    @Autowired
    DzCheckHistoryService dzCheckHistoryService;
    @Autowired
    SysUserServiceDao sysUserServiceDao;
    @Autowired
    DzCheckHistoryItemService dzCheckHistoryItemService;
    @Autowired
    DzEquipmentService dzEquipmentService;
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Result add(String sub, DeviceCheckVo deviceCheckVo) {
        if(CollectionUtils.isEmpty(deviceCheckVo.getHistoryItemList())){
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
        }
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        DzCheckHistory dzCheckHistory=new DzCheckHistory();
        dzCheckHistory.setLineId(deviceCheckVo.getLineId());
        dzCheckHistory.setDeviceId(deviceCheckVo.getDeviceId());
        dzCheckHistory.setCheckType(deviceCheckVo.getCheckType());
        dzCheckHistory.setUsername(byUserName.getUsername());
        dzCheckHistory.setOrgCode(byUserName.getOrgCode());
        dzCheckHistory.setUpdateBy(byUserName.getUsername());
        dzCheckHistory.setCreateBy(byUserName.getUsername());
        boolean save = dzCheckHistoryService.save(dzCheckHistory);
        if(save){
            List<DzCheckHistoryItem> list=new ArrayList<>();
            for (DeviceCheckItemVo deviceCheckItemVo:deviceCheckVo.getHistoryItemList()) {
                DzCheckHistoryItem data=new DzCheckHistoryItem();
                data.setCheckHistoryId(dzCheckHistory.getCheckHistoryId());
                data.setCheckName(deviceCheckItemVo.getCheckName());
                data.setChecked(deviceCheckItemVo.getChecked());
                data.setContentText(deviceCheckItemVo.getContentText());
                data.setOrgCode(dzCheckHistory.getOrgCode());
                list.add(data);
            }
            if(list.size()>0){
                dzCheckHistoryItemService.saveBatch(list);
                return Result.ok();
            }
        }
        return Result.error(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
    }

    @Override
    public Result list( GetDeviceCheckVo getDeviceCheckVo) {
        if (getDeviceCheckVo.getPage() != -1){
            PageHelper.startPage(getDeviceCheckVo.getPage(),getDeviceCheckVo.getLimit());
        }
        List<GetDeviceCheckDo> list = dzCheckHistoryService.getList(getDeviceCheckVo);
        PageInfo<GetDeviceCheckDo>info=new PageInfo(list);
        return Result.ok(info.getList(),info.getTotal());
    }

    @Override
    public Result getById(String checkHistoryId) {
        List<DzCheckHistoryItem> check_history_item_id = dzCheckHistoryItemService.list(new QueryWrapper<DzCheckHistoryItem>().eq("check_history_id", checkHistoryId));

        DzCheckHistory byId = dzCheckHistoryService.getById(checkHistoryId);
        DzEquipment equipment = dzEquipmentService.getById(byId.getDeviceId());

        Map<String,Object> map=new HashMap<>();
        map.put("historyItemList",check_history_item_id);
        map.put("checkType",byId.getCheckType());
        map.put("deviceId",equipment.getId());
        map.put("lineId",equipment.getLineId());
        map.put("equipmentType",equipment.getEquipmentType());

        return Result.ok(map);
    }

    @Override
    public Result put(String sub, List<DzCheckHistoryItem> list) {
        boolean b = dzCheckHistoryItemService.updateBatchById(list);
        return Result.ok();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Result del(String sub, String checkHistoryId) {
        boolean remove = dzCheckHistoryItemService.remove(new QueryWrapper<DzCheckHistoryItem>().eq("check_history_id", checkHistoryId));
        boolean b = dzCheckHistoryService.removeById(checkHistoryId);
        return Result.ok();
    }
}
