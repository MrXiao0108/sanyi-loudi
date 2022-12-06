package com.dzics.kanban.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.kanban.exception.enums.CustomExceptionType;
import com.dzics.kanban.exception.enums.CustomResponseCode;
import com.dzics.kanban.model.entity.*;
import com.dzics.kanban.model.request.kb.*;
import com.dzics.kanban.model.response.Result;
import com.dzics.kanban.service.*;
import com.dzics.kanban.util.PageLimit;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ZhangChengJun
 * Date 2021/4/27.
 * @since
 */
@Service
public class InterfaceMethodImpl implements InterfaceMethod {

    @Autowired
    private SysMethodGroupConfigurationService methodGroupConfigurationService;
    @Autowired
    private SysMethodGroupService methodGroupService;
    @Autowired
    private SysInterfaceGroupService interfaceGroupService;
    @Autowired
    private SysInterfaceMethodService interfaceMethodService;
    @Autowired
    private SysInterfaceGroupConfigurationService configurationService;

    @Override
    public Result getInterfaceMethod(String sub, PageLimit pageLimit) {
        PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        List<SysInterfaceMethod> list = interfaceMethodService.list();
        PageInfo<SysInterfaceMethod> info = new PageInfo<>(list);
        Result ok = Result.ok();
        ok.setData(info.getList());
        ok.setCount(info.getTotal());
        return ok;
    }

    @Override
    public Result addInterfaceMethod(String sub, SysInterfaceMethod interfaceMethod) {
        QueryWrapper<SysInterfaceMethod> wp = new QueryWrapper<>();
        wp.eq("method_name", interfaceMethod.getMethodName());
        wp.eq("bean_name", interfaceMethod.getBeanName());
        wp.or();
        wp.eq("response_name", interfaceMethod.getResponseName());
        List<SysInterfaceMethod> list = interfaceMethodService.list(wp);
        if (CollectionUtils.isEmpty(list)) {
            interfaceMethodService.save(interfaceMethod);
            return Result.ok();
        } else {
            Result result = new Result();
            result.setCode(CustomExceptionType.TOKEN_PERRMITRE_ERROR.getCode());
            result.setMsg(CustomResponseCode.ERR36.getChinese());
            return result;
        }
    }

    @Override
    public Result editInterfaceMethod(String sub, SysInterfaceMethod sysInterfaceMethod) {
        boolean updateById = interfaceMethodService.updateById(sysInterfaceMethod);
        return Result.ok();
    }

    @Override
    public Result getGroup(String sub, PageLimit pageLimit) {
        PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        QueryWrapper<SysInterfaceGroup> wp = new QueryWrapper<>();
        wp.orderByAsc("sort_code", "group_id");
        List<SysInterfaceGroup> list = interfaceGroupService.list(wp);
        PageInfo<SysInterfaceGroup> info = new PageInfo<>(list);
        Result<Object> ok = Result.ok();
        ok.setData(info.getList());
        ok.setCount(info.getTotal());
        return ok;
    }

    @Override
    public Result addGroup(String sub, SysInterfaceGroup interfaceGroup) {
        QueryWrapper<SysInterfaceGroup> wp = new QueryWrapper<>();
        wp.eq("group_code", interfaceGroup.getGroupCode());
        List<SysInterfaceGroup> list = interfaceGroupService.list(wp);
        if (CollectionUtils.isEmpty(list)) {
            boolean save = interfaceGroupService.save(interfaceGroup);
            return Result.ok();
        } else {
            Result result = new Result();
            result.setCode(CustomExceptionType.TOKEN_PERRMITRE_ERROR.getCode());
            result.setMsg(CustomResponseCode.ERR37.getChinese());
            return result;
        }
    }

    @Override
    public Result editGroup(String sub, SysInterfaceGroup interfaceGroup) {
        boolean updateById = interfaceGroupService.updateById(interfaceGroup);
        return Result.ok();
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result addGroupInerfaceConfig(String sub, AddInterfaceGroupConfiguration groupConfiguration) {
        List<ReqGroupConfiguration> interfaceIdsHandel = new ArrayList<>();
        List<ReqGroupConfiguration> interfaceIds = groupConfiguration.getInterfaceIds();
        for (ReqGroupConfiguration interfaceId : interfaceIds) {
            if (StringUtils.isEmpty(interfaceId.getGroupName())) {
                interfaceIdsHandel.add(interfaceId);
            }
        }
        String groupId = groupConfiguration.getGroupId();
        QueryWrapper<SysInterfaceGroupConfiguration> wp = new QueryWrapper<>();
        wp.eq("group_id", groupId);
        boolean remove = configurationService.remove(wp);
        List<SysInterfaceGroupConfiguration> collect = interfaceIdsHandel.stream().map(inface -> new SysInterfaceGroupConfiguration(groupId, inface.getInterfaceId(), inface.getCacheDuration())).collect(Collectors.toList());
        boolean b = configurationService.saveBatch(collect);
        return Result.ok();
    }

    @Override
    public Result getGroupInerfaceConfig(String sub, GetGroupConfig config) {
        String groupId = config.getGroupId();
//        组信息
        QueryWrapper<SysInterfaceGroup> wpGroup = new QueryWrapper<>();
        SysInterfaceGroup byId = interfaceGroupService.getById(groupId);
        if (byId == null) {
            return Result.ok(CustomExceptionType.OK_NO_DATA);
        }
//       全部接口信息
        List<SysInterfaceMethod> interfaceMethods = interfaceMethodService.list();
//        已选中接口信息
        QueryWrapper<SysInterfaceGroupConfiguration> wp = new QueryWrapper<>();
        wp.eq("group_id", groupId);
        wp.select("interface_id", "cache_duration");
        List<SysInterfaceGroupConfiguration> list = configurationService.list(wp);
        ReqGroupConfig reqGroupConfig = new ReqGroupConfig();
        reqGroupConfig.setInterfaceGroup(byId);
        if (CollectionUtils.isNotEmpty(list)) {
            for (SysInterfaceGroupConfiguration configuration : list) {
                for (SysInterfaceMethod interfaceMethod : interfaceMethods) {
                    if (configuration.getInterfaceId().equals(interfaceMethod.getInterfaceId())) {
                        interfaceMethod.setCacheDuration(configuration.getCacheDuration());
                        interfaceMethod.setIsShow(0);
                        interfaceMethod.setSortCode(new BigDecimal(0));
                        break;
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(interfaceMethods)) {
            List<SysMethodGroupConfiguration> configurationList = methodGroupConfigurationService.list();
            for (SysInterfaceMethod interfaceMethod : interfaceMethods) {
                String interfaceId = interfaceMethod.getInterfaceId();
                for (SysMethodGroupConfiguration sysMethodGroupConfiguration : configurationList) {
                    String methodId = sysMethodGroupConfiguration.getMethodId();
                    if (interfaceId.equals(methodId)) {
                        interfaceMethod.setParentId(sysMethodGroupConfiguration.getGroupId());
                        break;
                    }
                }
            }
            List<SysMethodGroup> methodGroups = methodGroupService.list();
            for (SysMethodGroup methodGroup : methodGroups) {
                SysInterfaceMethod interfaceMethod = new SysInterfaceMethod();
                interfaceMethod.setParentId("0");
                interfaceMethod.setInterfaceId(methodGroup.getMethodGroupId());
                interfaceMethod.setGroupName(methodGroup.getGroupName());
                interfaceMethod.setSortCode(methodGroup.getSortCode());
                interfaceMethods.add(interfaceMethod);
            }
        }
        reqGroupConfig.setInterfaceMethods(interfaceMethods);
        return Result.ok(reqGroupConfig);
    }

    @Override
    public Result getMethodGroup(String methodGroup) {
        Result ok = Result.ok();
        QueryWrapper<SysInterfaceGroup> wp = new QueryWrapper<>();
        wp.eq("group_code", methodGroup);
        SysInterfaceGroup interfaceGroup = interfaceGroupService.getOne(wp);
        if (interfaceGroup != null) {
            String groupId = interfaceGroup.getGroupId();
            QueryWrapper<SysInterfaceGroupConfiguration> wpConf = new QueryWrapper<>();
            wpConf.select("interface_id", "cache_duration");
            wpConf.eq("group_id", groupId);
            List<SysInterfaceGroupConfiguration> list = configurationService.list(wpConf);
            if (CollectionUtils.isNotEmpty(list)) {
                List<String> interfaceId = list.stream().map(conf -> conf.getInterfaceId()).collect(Collectors.toList());
                QueryWrapper<SysInterfaceMethod> wpInterface = new QueryWrapper<>();
                wpInterface.in("interface_id", interfaceId);
                List<SysInterfaceMethod> sysInterfaceMethods = interfaceMethodService.list(wpInterface);
                for (SysInterfaceMethod interfaceMethod : sysInterfaceMethods) {
                    for (SysInterfaceGroupConfiguration configuration : list) {
                        if (interfaceMethod.getInterfaceId().equals(configuration.getInterfaceId())) {
                            interfaceMethod.setCacheDuration(configuration.getCacheDuration());
                        }
                    }
                }
                ok.setData(sysInterfaceMethods);
                return ok;
            }
        }
        return ok;
    }

    @Override
    public Result delInterfaceMethod(String sub, DelInterfaceMethod delInterfaceMethod) {
        boolean b = interfaceMethodService.removeById(delInterfaceMethod.getInterfaceId());
        return Result.ok();
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result delGroup(String sub, DelInterfaceGroup interfaceGroup) {
        String groupId = interfaceGroup.getGroupId();
        boolean b = interfaceGroupService.removeById(groupId);
        QueryWrapper<SysInterfaceGroupConfiguration> wp = new QueryWrapper<>();
        wp.eq("group_id", groupId);
        boolean remove = configurationService.remove(wp);
        return Result.ok();
    }
}
