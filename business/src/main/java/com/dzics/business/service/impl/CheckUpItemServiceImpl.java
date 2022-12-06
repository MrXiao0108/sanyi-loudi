package com.dzics.business.service.impl;
import com.dzics.common.enums.Message;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.business.service.CheckUpItemService;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.entity.DzCheckUpItem;
import com.dzics.common.model.entity.DzCheckUpItemType;
import com.dzics.common.model.entity.SysDictItem;
import com.dzics.common.model.entity.SysUser;
import com.dzics.common.model.request.devicecheck.CheckTypeVo;
import com.dzics.common.model.request.devicecheck.CheckUpVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.devicecheck.CheckTypeDo;
import com.dzics.common.model.response.devicecheck.DzCheckUpItemDo;
import com.dzics.common.service.DzCheckUpItemService;
import com.dzics.common.service.DzCheckUpItemTypeService;
import com.dzics.common.service.SysDictItemService;
import com.dzics.common.service.SysUserServiceDao;
import com.dzics.common.util.PageLimit;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class CheckUpItemServiceImpl implements CheckUpItemService {
    @Autowired
    DzCheckUpItemService dzCheckUpItemService;
    @Autowired
    private SysUserServiceDao sysUserServiceDao;
    @Autowired
    private DzCheckUpItemTypeService dzCheckUpItemTypeService;
    @Autowired
    private SysDictItemService sysDictItemService;
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Result add(String sub, CheckUpVo checkUpVo) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        DzCheckUpItem dzCheckUpItem=new DzCheckUpItem();
        dzCheckUpItem.setDeviceType(checkUpVo.getDeviceType());
        dzCheckUpItem.setCheckName(checkUpVo.getCheckName());
        dzCheckUpItem.setOrgCode(byUserName.getOrgCode());
        dzCheckUpItem.setCreateBy(byUserName.getUsername());
        boolean save = dzCheckUpItemService.save(dzCheckUpItem);
        if(save){
            List<DzCheckUpItemType> list=new ArrayList<>();
            for (CheckTypeVo checkTypeVo:checkUpVo.getCheckTypeList()) {
                DzCheckUpItemType dzCheckUpItemType=new DzCheckUpItemType();
                dzCheckUpItemType.setDeviceType(checkUpVo.getDeviceType());
                dzCheckUpItemType.setCheckItemId(dzCheckUpItem.getCheckItemId());
                dzCheckUpItemType.setDictItemId(checkTypeVo.getDictItemId());
                dzCheckUpItemType.setDictCode(checkTypeVo.getDictCode());
                dzCheckUpItemType.setChecked(checkTypeVo.getChecked());
                dzCheckUpItemType.setOrgCode(byUserName.getOrgCode());
                dzCheckUpItemType.setCreateBy(byUserName.getUsername());
                list.add(dzCheckUpItemType);
            }
            if(list.size()>0){
                dzCheckUpItemTypeService.saveBatch(list);
                return Result.ok();
            }
        }
        return Result.error(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
    }

    @Override
    public Result list(PageLimit pageLimit, Integer deviceType, String checkName) {
        if(pageLimit.getPage() != -1){
            PageHelper.startPage(pageLimit.getPage(),pageLimit.getLimit());
        }
        QueryWrapper<DzCheckUpItem>wrapper=new QueryWrapper();
        if(deviceType!=null){
            wrapper.eq("device_type",deviceType);
        }
        if(!StringUtils.isEmpty(checkName)){
            wrapper.likeRight("check_name",checkName);
        }
        List<DzCheckUpItem> data = dzCheckUpItemService.list(wrapper);
        PageInfo<DzCheckUpItem>info=new PageInfo<>(data);
        List<DzCheckUpItem> list = info.getList();
        List<DzCheckUpItemDo>dzCheckUpItemDos=new ArrayList<>();
        if(CollectionUtils.isEmpty(list)){
            return Result.ok(new ArrayList<>(),info.getTotal());
        }

        //查询检测项类型
        List<String> collect = list.stream().map(p -> p.getCheckItemId()).collect(Collectors.toList());
        List<DzCheckUpItemType> check_item_id = dzCheckUpItemTypeService.list(new QueryWrapper<DzCheckUpItemType>().in("check_item_id", collect));
        //字典表翻译检测项类型
        List<Long> collect1 = check_item_id.stream().map(p -> p.getDictItemId()).collect(Collectors.toList());
        List<SysDictItem> dictItems = sysDictItemService.list(new QueryWrapper<SysDictItem>().in("id", collect1));

        List<CheckTypeDo>checkTypeDos=new ArrayList<>();
        for (DzCheckUpItemType dzCheckUpItemType:check_item_id) {
            CheckTypeDo checkTypeDo=new CheckTypeDo();
            checkTypeDo.setCheckItemId(dzCheckUpItemType.getCheckItemId());
            checkTypeDo.setDictCode(dzCheckUpItemType.getDictCode());
            checkTypeDo.setChecked(dzCheckUpItemType.getChecked());
            checkTypeDo.setCheckTypeId(dzCheckUpItemType.getCheckTypeId());
            for (SysDictItem sysDictItem:dictItems) {
                if(sysDictItem.getId().intValue()==dzCheckUpItemType.getDictItemId().intValue()){
                    checkTypeDo.setItemText(sysDictItem.getItemText());
                    break;
                }
            }
            checkTypeDos.add(checkTypeDo);
        }
        for (DzCheckUpItem dzCheckUpItem:list) {
            DzCheckUpItemDo dzCheckUpItemDo=new DzCheckUpItemDo();
            BeanUtils.copyProperties(dzCheckUpItem,dzCheckUpItemDo);
            List<CheckTypeDo> checkTypeList = new ArrayList<>();
            for (CheckTypeDo checkTypeDo:checkTypeDos) {
                if(checkTypeDo.getCheckItemId().equals(dzCheckUpItem.getCheckItemId())){
                    checkTypeList.add(checkTypeDo);
                }
            }
            dzCheckUpItemDo.setCheckTypeList(checkTypeList);
            dzCheckUpItemDos.add(dzCheckUpItemDo);
        }
        return Result.ok(dzCheckUpItemDos,info.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Result del(String checkItemId) {
        dzCheckUpItemTypeService.remove(new QueryWrapper<DzCheckUpItemType>().eq("check_item_id", checkItemId));
        dzCheckUpItemService.remove(new QueryWrapper<DzCheckUpItem>().eq("check_item_id", checkItemId));
        return Result.ok();
    }

    @Override
    public Result put(String sub, CheckUpVo checkUpVo) {
        //判断同类型下巡检名称是否存在

        QueryWrapper<DzCheckUpItem> wrapper = new QueryWrapper<DzCheckUpItem>()
                .eq("check_name", checkUpVo.getCheckName())
                .eq("device_type", checkUpVo.getDeviceType())
                .ne("check_item_id", checkUpVo.getCheckItemId());
        DzCheckUpItem one = dzCheckUpItemService.getOne(wrapper);
        if(one!=null){
            log.error("巡检想名称重复");
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_200);
        }
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);

        one=new DzCheckUpItem();
        one.setCheckItemId( checkUpVo.getCheckItemId());
        one.setDeviceType(checkUpVo.getDeviceType());
        one.setCheckName(checkUpVo.getCheckName());
        one.setUpdateBy(byUserName.getUsername());
        boolean b = dzCheckUpItemService.updateById(one);
        if(b){
            List<DzCheckUpItemType>list=new ArrayList<>();
            for (CheckTypeVo checkTypeVo:checkUpVo.getCheckTypeList()) {
                DzCheckUpItemType dzCheckUpItemType=new DzCheckUpItemType();
                BeanUtils.copyProperties(checkTypeVo,dzCheckUpItemType);
                dzCheckUpItemType.setCreateBy("111");
                list.add(dzCheckUpItemType);
            }
            dzCheckUpItemTypeService.updateBatchById(list);
        }
        return Result.ok();
    }
}
