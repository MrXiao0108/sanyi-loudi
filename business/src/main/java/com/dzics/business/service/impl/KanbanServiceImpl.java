package com.dzics.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.business.model.response.MenusInfo;
import com.dzics.business.model.response.RoutersInfo;
import com.dzics.business.model.vo.rolemenu.AddPermission;
import com.dzics.business.model.vo.rolemenu.SelKbRouting;
import com.dzics.business.model.vo.rolemenu.UpdatePermission;
import com.dzics.business.service.KanbanService;
import com.dzics.business.util.ChildrenUtils;
import com.dzics.common.enums.MenuType;
import com.dzics.common.exception.CustomException;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.entity.SysKanbanRouting;
import com.dzics.common.model.entity.SysUser;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.SysKanbanRoutingService;
import com.dzics.common.service.SysUserServiceDao;
import com.dzics.common.util.StringToUpcase;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ZhangChengJun
 * Date 2021/4/28.
 * @since
 */
@Service
@Slf4j
public class KanbanServiceImpl implements KanbanService {

    @Autowired
    private SysKanbanRoutingService kanbanRoutingService;
    @Autowired
    private SysUserServiceDao sysUserServiceDao;

    @Override
    public Result<MenusInfo> selMenuPermission(String sub) {
        List<SysKanbanRouting> list = kanbanRoutingService.list();
        List<MenusInfo> menusInfos = new ArrayList<>();
        for (SysKanbanRouting sysPermission : list) {
            MenusInfo metaInfo = new MenusInfo();
            BeanUtils.copyProperties(sysPermission, metaInfo);
            metaInfo.setChildren(Lists.newArrayList());
            metaInfo.setMenuId(String.valueOf(sysPermission.getId()));
            metaInfo.setMenuName(sysPermission.getTitle());
            metaInfo.setCreateTime(sysPermission.getCreateTime());
            menusInfos.add(metaInfo);
        }
        return new Result(CustomExceptionType.OK, menusInfos);
    }

    @Override
    public Result addPermission(AddPermission addPermission, String sub) {
        if (!"0".equals(addPermission.getParentId())) {
            SysKanbanRouting byId = kanbanRoutingService.getById(Long.valueOf(addPermission.getParentId()));
            if (byId.getMenuType().getCode().intValue() == MenuType.F.getCode()) {
                if (addPermission.getMenuType().getDesc().equals(MenuType.M.getDesc()) || addPermission.getMenuType().getDesc().equals(MenuType.C.getDesc())) {
                    throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR18);
                }
            }

        }
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        SysKanbanRouting sysPermission = new SysKanbanRouting();
        BeanUtils.copyProperties(addPermission, sysPermission);
        sysPermission.setDelFlag(false);
        sysPermission.setCreateBy(byUserName.getUsername());
        sysPermission.setAlwaysShow(false);
        sysPermission.setName(StringToUpcase.toUpperCase(addPermission.getPath()));
        sysPermission.setRedirect(addPermission.getRedirect() != null ? addPermission.getRedirect() : "");
        sysPermission.setTitle(addPermission.getMenuName());
        if (sysPermission.getMenuType().getCode().intValue() == MenuType.F.getCode().intValue()) {
            sysPermission.setIsRoute(false);
        }
        kanbanRoutingService.save(sysPermission);
        return new Result(CustomExceptionType.OK);
    }

    @Override
    public Result<MenusInfo> selMenuPermissionId(Long id, String sub) {
        SysKanbanRouting sysPermission = kanbanRoutingService.getById(id);
        if (sysPermission == null) {
            throw new CustomException(CustomExceptionType.OK_NO_DATA);
        }
        MenusInfo metaInfo = new MenusInfo();
        BeanUtils.copyProperties(sysPermission, metaInfo);
        metaInfo.setChildren(Lists.newArrayList());
        metaInfo.setMenuId(String.valueOf(sysPermission.getId()));
        metaInfo.setMenuName(sysPermission.getTitle());
        metaInfo.setCreateTime(sysPermission.getCreateTime());
        metaInfo.setMenuType(sysPermission.getMenuType());
        return new Result(CustomExceptionType.OK, metaInfo);
    }

    @Override
    public Result updatePermission(UpdatePermission updatePermission, String sub) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        if (updatePermission.getIsRoute() == null) {
            updatePermission.setIsRoute(1);
        }
        SysKanbanRouting sysPermission = new SysKanbanRouting();
        BeanUtils.copyProperties(updatePermission, sysPermission);
        sysPermission.setTitle(updatePermission.getMenuName());
        sysPermission.setId(Long.valueOf(updatePermission.getMenuId()));
        sysPermission.setCreateBy(byUserName.getUsername());
        sysPermission.setHidden(updatePermission.getHidden());
        sysPermission.setName(StringToUpcase.toUpperCase(updatePermission.getPath()));
        sysPermission.setIsRoute(updatePermission.getIsRoute().intValue() == 1 ? true : false);
        if (sysPermission.getMenuType().getCode().intValue() == MenuType.F.getCode().intValue()) {
            sysPermission.setIsRoute(false);
        }
        kanbanRoutingService.updateById(sysPermission);
        return new Result(CustomExceptionType.OK);
    }

    @Override
    public Result delPermission(Long delPermission, String sub) {
        kanbanRoutingService.removeById(delPermission);
//        删除子节点
        QueryWrapper<SysKanbanRouting> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", delPermission);
        List<SysKanbanRouting> list = kanbanRoutingService.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            List<Long> perId = list.stream().map(pis -> pis.getId()).collect(Collectors.toList());
            kanbanRoutingService.removeByIds(perId);
        }
        return new Result(CustomExceptionType.OK);
    }

    @Override
    public Result selRoutingDetails(SelKbRouting kbRouting, String sub) {
        if (kbRouting == null || StringUtils.isEmpty(kbRouting.getParentTitle())) {
            return selRouting(sub);
        }
        QueryWrapper<SysKanbanRouting> wp = new QueryWrapper<>();
        wp.eq("title", kbRouting.getParentTitle());
        wp.eq("parent_id", 0);
        SysKanbanRouting one = kanbanRoutingService.getOne(wp);
        if (one != null) {
            QueryWrapper<SysKanbanRouting> wpKb = new QueryWrapper<>();
            wpKb.eq("parent_id", one.getId());
            List<SysKanbanRouting> list = kanbanRoutingService.list(wpKb);
            List<MenusInfo> menusInfos = new ArrayList<>();
            for (SysKanbanRouting sysPermission : list) {
                MenusInfo metaInfo = new MenusInfo();
                BeanUtils.copyProperties(sysPermission, metaInfo);
                metaInfo.setChildren(Lists.newArrayList());
                metaInfo.setMenuId(String.valueOf(sysPermission.getId()));
                metaInfo.setMenuName(sysPermission.getTitle());
                metaInfo.setCreateTime(sysPermission.getCreateTime());
                metaInfo.setMenuType(sysPermission.getMenuType());
                menusInfos.add(metaInfo);
            }
            return new Result(CustomExceptionType.OK, menusInfos);
        }
        return Result.ok(CustomExceptionType.OK_NO_DATA);
    }

    @Override
    public Result<SysKanbanRouting> selRouting(String sub) {
        List<SysKanbanRouting> list = kanbanRoutingService.list();
        List<MenusInfo> menusInfos = new ArrayList<>();
        for (SysKanbanRouting sysPermission : list) {
            MenusInfo metaInfo = new MenusInfo();
            BeanUtils.copyProperties(sysPermission, metaInfo);
            metaInfo.setChildren(Lists.newArrayList());
            metaInfo.setMenuId(String.valueOf(sysPermission.getId()));
            metaInfo.setMenuName(sysPermission.getTitle());
            metaInfo.setCreateTime(sysPermission.getCreateTime());
            metaInfo.setMenuType(sysPermission.getMenuType());
            menusInfos.add(metaInfo);
        }
        return new Result(CustomExceptionType.OK, menusInfos);
    }

    @Override
    public Result<SysKanbanRouting> selMenuRouting(SelKbRouting kbRouting, String sub) {
        List<SysKanbanRouting> permissions = new ArrayList<>();
        if (kbRouting == null || StringUtils.isEmpty(kbRouting.getParentTitle())) {
            permissions = kanbanRoutingService.list();
        }else {
            QueryWrapper<SysKanbanRouting> wp = new QueryWrapper<>();
            wp.eq("title", kbRouting.getParentTitle());
            wp.eq("parent_id", 0);
            SysKanbanRouting one = kanbanRoutingService.getOne(wp);
            if (one != null) {
                QueryWrapper<SysKanbanRouting> wpKb = new QueryWrapper<>();
                wpKb.eq("parent_id", one.getId());
                permissions = kanbanRoutingService.list(wpKb);
            }
        }
        if (CollectionUtils.isNotEmpty(permissions)) {
            List<SysKanbanRouting> permissionsNext = new ArrayList<>();
            List<RoutersInfo> parRout = ChildrenUtils.parPermissionNextKb(permissions, permissionsNext);
            List<RoutersInfo> parRoutEnd = ChildrenUtils.getChildRoutersInfoKb(parRout, permissionsNext);
            return new Result(CustomExceptionType.OK, parRoutEnd);
        }
        return null;
    }


}
