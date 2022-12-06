package com.dzics.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.business.model.response.RoutersInfo;
import com.dzics.business.model.vo.depart.*;
import com.dzics.business.service.BusinessDepartService;
import com.dzics.business.util.ChildrenUtils;
import com.dzics.business.util.WapperAddUtil;
import com.dzics.common.dao.DzOrderMapper;
import com.dzics.common.dao.DzProductMapper;
import com.dzics.common.dao.SysDepartMapper;
import com.dzics.common.enums.BasicsRole;
import com.dzics.common.enums.Message;
import com.dzics.common.enums.StatusEnum;
import com.dzics.common.exception.CustomException;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.entity.*;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.SwitchSiteDo;
import com.dzics.common.model.response.role.ResSysDepart;
import com.dzics.common.service.*;
import com.dzics.common.util.PageLimit;
import com.dzics.common.util.UnderlineTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BusinessDepartServiceImpl implements BusinessDepartService {
    @Autowired
    private SysDepartMapper sysDepartMapper;
    @Autowired
    SysUserServiceDao sysUserServiceDao;
    @Autowired
    private SysDepartService departService;
    @Autowired
    private WapperAddUtil wapperAddUtil;
    @Autowired
    private SysDepartPermissionService departPermissionService;
    @Autowired
    private SysUserDepartService userDepartService;
    @Autowired
    private SysPermissionService permissionService;
    @Autowired
    private SysRoleService roleService;
    @Autowired
    private SysRolePermissionService rolePermissionService;
    @Autowired
    DzProductMapper dzProductMapper;
    @Autowired
    DzOrderMapper dzOrderMapper;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result addDepart(AddDepart addDepart, String sub) {
        SysDepart byCode = this.getByCode(addDepart.getOrgCode());
        if (byCode != null) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR23);
        }
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        SysDepart sysDepart = new SysDepart();
        BeanUtils.copyProperties(addDepart, sysDepart);
        sysDepart.setCreateBy(byUserName.getUsername());
        sysDepart.setDelFlag(false);
        sysDepart.setParentId(1L);
        departService.save(sysDepart);
        List<Long> permissionId = addDepart.getPermissionId();
        if (CollectionUtils.isNotEmpty(permissionId)) {
            List<SysDepartPermission> collect = permissionId.stream().map(pmid -> new SysDepartPermission(sysDepart.getId(), pmid, false, byUserName.getUsername())).collect(Collectors.toList());
            departPermissionService.saveBatch(collect);
//            创建站点基础角色
            SysRole sysRole = new SysRole();
            sysRole.setRoleName(sysDepart.getDepartName());
            sysRole.setRoleCode(sysDepart.getOrgCode() + "_Admin");
            sysRole.setDescription("基础Admin角色");
            sysRole.setDepartId(sysDepart.getId());
            sysRole.setBasicsRole(BasicsRole.JC.getCode());
            sysRole.setOrgCode(sysDepart.getOrgCode());
            sysRole.setStatus(StatusEnum.Enable.getCode());
            sysRole.setDelFlag(false);
            sysRole.setCreateBy(byUserName.getUsername());
            roleService.save(sysRole);
            List<SysRolePermission> rolePermissions = permissionId.stream().map(pid ->
                    new SysRolePermission(sysRole.getRoleId(), pid, sysDepart.getOrgCode(),
                            false, byUserName.getUsername())).collect(Collectors.toList());
            rolePermissionService.saveBatch(rolePermissions);
        }
        return new Result(CustomExceptionType.OK);
    }

    @Override
    public Result queryDepart(PageLimit pageLimit, SelDepart selDepart, String sub) {
        PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        QueryWrapper<SysDepart> queryWrapper = new QueryWrapper<>();
        if (selDepart != null) {
            wapperAddUtil.addQuery(selDepart, queryWrapper);
        }
        queryWrapper.ne("parent_id", 0L);
        if (!StringUtils.isEmpty(pageLimit.getType())) {
            if ("DESC".equals(pageLimit.getType())) {
                queryWrapper.orderByDesc(UnderlineTool.humpToLine(pageLimit.getField()));
            } else if ("ASC".equals(pageLimit.getType())) {
                queryWrapper.orderByAsc(UnderlineTool.humpToLine(pageLimit.getField()));
            }
        }
        List<SysDepart> list = departService.list(queryWrapper);
        PageInfo<SysDepart> sysDepartPageInfo = new PageInfo<>(list);
        List<ResDepart> departs = new ArrayList<>();
        for (SysDepart sysDepart : sysDepartPageInfo.getList()) {
            ResDepart resDepart = new ResDepart();
            BeanUtils.copyProperties(sysDepart, resDepart);
            resDepart.setDepartId(String.valueOf(sysDepart.getId()));
            departs.add(resDepart);
        }
        return new Result(CustomExceptionType.OK, departs, sysDepartPageInfo.getTotal());
    }


    /**
     * @param sub      操作用户
     * @param departId 站点id 如果传递了站点id则返回 已选择的权限 id
     * @return
     */
    @Override
    public Result getDepartMsg(String sub, Long departId) {
        Map<String, Object> map = new HashMap<>();
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        SysDepart byCode = this.getByCode(byUserName.getUseOrgCode());
        if (byCode == null) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR13);
        }
        if (byCode.getParentId().longValue() != 0) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR31);
        } else {
            List<SysPermission> permissions = permissionService.list();
            List<SysPermission> permissionsNext = new ArrayList<>();
            List<RoutersInfo> parRout = ChildrenUtils.parPermissionNext(permissions, permissionsNext);
            List<RoutersInfo> parRoutEnd = ChildrenUtils.getChildRoutersInfo(parRout, permissionsNext);
            List<Map<String, Object>> list = ChildrenUtils.treeSelectMap(parRoutEnd);
            map.put("permissions", list);
        }
        if (departId != null) {
//         编辑是 需要获取 获取的站点信息
            QueryWrapper<SysDepartPermission> depPerMis = new QueryWrapper<>();
            depPerMis.eq("depart_id", departId);
            List<String> permisChecked = departPermissionService.list(depPerMis).stream().map(dePers -> String.valueOf(dePers.getPermissionId())).collect(Collectors.toList());
            map.put("checkedKeys", permisChecked);
        }
        return new Result(CustomExceptionType.OK, map);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result<ResDepart> delDepart(Long departId, String sub) {
        //该站点是否绑定产品判断
        List<DzProduct> dzProducts = dzProductMapper.selectList(new QueryWrapper<DzProduct>().eq("depart_id", departId));
        if (dzProducts.size() > 0) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_69);
        }
        //该站点是否绑定订单判断
        List<DzOrder> order = dzOrderMapper.selectList(new QueryWrapper<DzOrder>().eq("depart_id", departId));
        if (order.size() > 0) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_70);
        }
        SysDepart byId = departService.getById(departId);
        Integer sizeUser = sysUserServiceDao.getCountByOrgCode(byId.getOrgCode());
        if (sizeUser.intValue() > 0) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR25);
        }
        departService.removeById(departId);
        QueryWrapper<SysDepartPermission> depMissWp = new QueryWrapper<>();
        depMissWp.eq("depart_id", departId);
        departPermissionService.remove(depMissWp);
        QueryWrapper<SysUserDepart> wpUserDep = new QueryWrapper<>();
        wpUserDep.eq("depart_id", departId);
        userDepartService.remove(wpUserDep);
        QueryWrapper<SysRole> roleQueryWrapper = new QueryWrapper<>();
        roleQueryWrapper.eq("org_code", byId.getOrgCode());
        roleService.remove(roleQueryWrapper);
        QueryWrapper<SysRolePermission> wpRoPermis = new QueryWrapper<>();
        wpRoPermis.eq("org_code", byId.getOrgCode());
        rolePermissionService.remove(wpRoPermis);
        return new Result(CustomExceptionType.OK);
    }

    @Override
    public Result getDepartDetails(String sub, Long departId) {
        SysDepart byId = departService.getById(departId);
        ResDepart resDepart = new ResDepart();
        BeanUtils.copyProperties(byId, resDepart);
        resDepart.setDepartId(String.valueOf(byId.getId()));
        return new Result(CustomExceptionType.OK, resDepart);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result updateDepart(UpdateDepart updateDepart, String sub) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        SysDepart sysDepart = new SysDepart();
        BeanUtils.copyProperties(updateDepart, sysDepart);
        sysDepart.setCreateBy(byUserName.getUsername());
        sysDepart.setUpdateBy(byUserName.getUsername());
        sysDepart.setId(Long.valueOf(updateDepart.getDepartId()));
        departService.updateById(sysDepart);
        departPermissionService.removeDepartId(Long.valueOf(updateDepart.getDepartId()));
        List<String> permissionId = updateDepart.getPermissionId();
        if (CollectionUtils.isNotEmpty(permissionId)) {
            List<SysDepartPermission> collect = permissionId.stream().map(pmid -> new SysDepartPermission(sysDepart.getId(), Long.valueOf(pmid), false, byUserName.getUsername())).collect(Collectors.toList());
            departPermissionService.saveBatch(collect);
//            更新站点基础角色
            SysRole role = roleService.getRole(sysDepart.getId(), BasicsRole.JC.getCode());
            rolePermissionService.removeRoleId(role.getRoleId());
            List<SysRolePermission> rolePermissions = permissionId.stream().map(pid ->
                    new SysRolePermission(role.getRoleId(), Long.valueOf(pid), sysDepart.getOrgCode(),
                            false, byUserName.getUsername())).collect(Collectors.toList());
            rolePermissionService.saveBatch(rolePermissions);
        }
        return new Result(CustomExceptionType.OK);
    }

    @Override
    public Result disableEnabledRole(DisableEnabledDepart enabledDepart, String sub) {
        SysDepart sysDepart = new SysDepart();
        sysDepart.setId(Long.valueOf(enabledDepart.getDepartId()));
        sysDepart.setStatus(enabledDepart.getStatus());
        departService.updateById(sysDepart);
        roleService.updateDepartId(Long.valueOf(enabledDepart.getDepartId()), enabledDepart.getStatus());
        return new Result(CustomExceptionType.OK);
    }

    @Override
    public SysDepart getById(Long id) {
        return sysDepartMapper.selectById(id);
    }

    @Override
    public List<SysDepart> list(QueryWrapper<SysDepart> depQwp) {
        return sysDepartMapper.selectList(depQwp);
    }

    @Override
    public List<SwitchSiteDo> listId(List<Long> ids) {
        return sysDepartMapper.listId(ids);
    }

    @Override
    public List<SwitchSiteDo> listAll() {
        return sysDepartMapper.listAll();
    }

    @Override
    public SwitchSiteDo getByOrgCode(String orgCode) {
        return sysDepartMapper.getByOrgCode(orgCode);
    }


    @Override
    public SysDepart getByCode(String useOrgCode) {
        return sysDepartMapper.getByCode(useOrgCode);
    }

    @Override
    public ResSysDepart getByParentId(int i) {
        QueryWrapper<SysDepart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", 0);
        SysDepart sysDepart = sysDepartMapper.selectOne(queryWrapper);
        ResSysDepart resSysDepart = null;
        if (sysDepart != null) {
            resSysDepart = new ResSysDepart();
            BeanUtils.copyProperties(sysDepart, resSysDepart);
            resSysDepart.setParentId(String.valueOf(sysDepart.getParentId()));
            resSysDepart.setId(String.valueOf(sysDepart.getId()));
        }
        return resSysDepart;
    }

    @Override
    public List<ResSysDepart> listNotDz() {
        QueryWrapper<SysDepart> departQueryWrapper = new QueryWrapper<>();
        departQueryWrapper.ne("parent_id", 0);
        List<SysDepart> departs = sysDepartMapper.selectList(departQueryWrapper);
        List<ResSysDepart> resSysDeparts = new ArrayList<>();
        departs.stream().forEach(s -> {
            ResSysDepart resSysDepart = new ResSysDepart();
            BeanUtils.copyProperties(s, resSysDepart);
            resSysDepart.setId(String.valueOf(s.getId()));
            resSysDepart.setParentId(String.valueOf(s.getParentId()));
            resSysDeparts.add(resSysDepart);
        });
        return resSysDeparts;
    }
}
