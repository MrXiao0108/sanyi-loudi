package com.dzics.kanban.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.kanban.enums.*;
import com.dzics.kanban.exception.CustomException;
import com.dzics.kanban.exception.CustomWarnException;
import com.dzics.kanban.exception.enums.CustomExceptionType;
import com.dzics.kanban.exception.enums.CustomResponseCode;
import com.dzics.kanban.model.entity.*;
import com.dzics.kanban.model.request.ImgHeadBase64;
import com.dzics.kanban.model.request.PutUserPasswordVo;
import com.dzics.kanban.model.response.*;
import com.dzics.kanban.model.vo.RegisterVo;
import com.dzics.kanban.model.vo.depart.ResDepart;
import com.dzics.kanban.model.vo.rolemenu.*;
import com.dzics.kanban.model.vo.user.*;
import com.dzics.kanban.service.*;
import com.dzics.kanban.util.*;
import com.dzics.kanban.util.md5.Md5Util;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ZhangChengJun
 * Date 2021/1/6.
 */
@Service
@Slf4j
public class BusinessUserServiceImpl implements BusinessUserService {
    @Autowired
    private WapperAddUtil wapperAddUtil;
    @Autowired
    private SysUserServiceDao sysUserServiceDao;
    @Autowired
    private SysUserDepartService userDepartService;
    @Autowired
    private BusinessDepartService businessDepartService;
    @Autowired
    private SysUserRoleService userRoleService;
    @Autowired
    private SysRolePermissionService rolePermissionService;
    @Autowired
    private SysPermissionService permissionService;
    @Autowired
    private SysRoleService roleService;
    @Autowired
    private SysDepartPermissionService departPermissionService;
    @Autowired
    private AuthRoleCommon authRoleCommon;
    @Autowired
    private RedisUtil redisUtil;
    @Value("${file.ip.address}")
    private String address;
    @Value("${file.parent.path}")
    private String parentPath;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Result addUser(RegisterVo registerVo, String sub) {
        Long count = sysUserServiceDao.listUsername(registerVo.getUsername());
        if (count > 0) {
            throw new CustomWarnException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR53);
        } else {
            SysUser byUserName = sysUserServiceDao.getByUserName(sub);
//            保存用户
            registerVo.setPassword(Md5Util.md5(registerVo.getPassword()));
            SysUser sysUser = new SysUser();
            BeanUtils.copyProperties(registerVo, sysUser);
            sysUser.setDelFlag(false);
            sysUser.setStatus(1);
            sysUser.setCreateBy(sub);
            sysUser.setOrgCode(byUserName.getUseOrgCode());
//            校验添加用户类型
            if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue()) {
                if (byUserName.getOrgCode().equals(byUserName.getUseOrgCode())) {
                    if (registerVo.getUserIdentity() == null || registerVo.getUserIdentity().intValue() == UserIdentityEnum.ORTER.getCode()) {
                        throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR22);
                    }
                } else {
                    registerVo.setUserIdentity(UserIdentityEnum.ORTER.getCode());
                }
            }
//         设置校验添加用户归属站点
            if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue()) {
                if (registerVo.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode()) {
                    SysDepart sysDepart = businessDepartService.getByParentId(0);
                    if (sysDepart == null) {
                        log.error("添加用户查询归属站点的父id 是 0的站点不存在");
                        throw new CustomException(CustomExceptionType.SYSTEM_ERROR, CustomResponseCode.ERR0);
                    }
                    registerVo.setAffiliationDepartId(sysDepart.getId());
                    sysUser.setAffiliationDepartId(sysDepart.getId());
                    sysUser.setUseOrgCode(sysDepart.getOrgCode());
                } else if (registerVo.getUserIdentity().intValue() == UserIdentityEnum.DEPART.getCode()) {
                    if (registerVo.getAffiliationDepartId() == null) {
                        throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR21);
                    }
                } else if (registerVo.getUserIdentity().intValue() == UserIdentityEnum.ORTER.getCode()) {
                    SysDepart byCode = businessDepartService.getByCode(byUserName.getUseOrgCode());
                    registerVo.setAffiliationDepartId(byCode.getId());
                    sysUser.setAffiliationDepartId(byCode.getId());
                    sysUser.setUseOrgCode(byCode.getOrgCode());
                }
            } else {
                registerVo.setAffiliationDepartId(byUserName.getAffiliationDepartId());
                sysUser.setAffiliationDepartId(byUserName.getAffiliationDepartId());
                sysUser.setUseOrgCode(byUserName.getOrgCode());
            }
            sysUser.setAvatar(ImgHeadBase64.AVATAR);
            sysUserServiceDao.save(sysUser);
//            保存关联站点关系
            if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue()) {
                if (registerVo.getUserIdentity().intValue() == UserIdentityEnum.DEPART.getCode()) {
                    registerVo.setDepartId(Arrays.asList(registerVo.getAffiliationDepartId()));
                } else if (registerVo.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode()) {
                    if (CollectionUtils.isEmpty(registerVo.getDepartId())) {
                        registerVo.setDepartId(Arrays.asList(1L));
                    } else {
                        registerVo.getDepartId().add(1L);
                    }
                } else {
                    SysDepart byCode = businessDepartService.getByCode(byUserName.getUseOrgCode());
                    registerVo.setDepartId(Arrays.asList(byCode.getId()));
                }
                List<SysUserDepart> userDeparts = registerVo.getDepartId().stream().map(depgl -> new SysUserDepart(sysUser.getId(), depgl, byUserName.getUseOrgCode(), false, byUserName.getUsername())).collect(Collectors.toList());
                userDepartService.saveBatch(userDeparts);
            } else {
                SysUserDepart sysUserDepart = new SysUserDepart(sysUser.getId(), registerVo.getAffiliationDepartId(), byUserName.getUseOrgCode(), false, byUserName.getUsername());
                userDepartService.save(sysUserDepart);
            }
//            保存角色信息
            if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue()) {
//              大正用户新建用户
                if (registerVo.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode()) {
//                    新建大正用户
                    if (CollectionUtils.isNotEmpty(registerVo.getRoleIds())) {
                        List<SysUserRole> collect = registerVo.getRoleIds().stream().map(rid -> new SysUserRole(sysUser.getId(), rid, byUserName.getUseOrgCode(), false, sysUser.getUsername())).collect(Collectors.toList());
                        userRoleService.saveBatch(collect);
                    }
                } else if (registerVo.getUserIdentity().intValue() == UserIdentityEnum.DEPART.getCode()) {
//                    新建站点用户
                    List<SysDepartPermission> departPermissions = departPermissionService.listDepartIdPerMission(registerVo.getAffiliationDepartId());
                    if (CollectionUtils.isEmpty(departPermissions)) {
                        throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR20);
                    }
                    SysRole role = roleService.getRole(registerVo.getAffiliationDepartId(), BasicsRole.JC.getCode());
                    SysUserRole sysUserRole = new SysUserRole();
                    sysUserRole.setUserId(sysUser.getId());
                    sysUserRole.setRoleId(role.getRoleId());
                    sysUserRole.setOrgCode(byUserName.getUseOrgCode());
                    sysUserRole.setDelFlag(false);
                    sysUserRole.setCreateBy(byUserName.getUsername());
                    userRoleService.save(sysUserRole);
                } else {
//                    新建站点子用户
                    if (CollectionUtils.isNotEmpty(registerVo.getRoleIds())) {
                        List<SysUserRole> collect = registerVo.getRoleIds().stream().map(rid -> new SysUserRole(sysUser.getId(), rid, byUserName.getUseOrgCode(), false, sysUser.getUsername())).collect(Collectors.toList());
                        userRoleService.saveBatch(collect);
                    }
                }
            } else {
//            非大正 例如富华新建用户
                if (CollectionUtils.isNotEmpty(registerVo.getRoleIds())) {
                    List<SysUserRole> collect = registerVo.getRoleIds().stream().map(rid -> new SysUserRole(sysUser.getId(), rid, byUserName.getUseOrgCode(), false, sysUser.getUsername())).collect(Collectors.toList());
                    userRoleService.saveBatch(collect);
                }

            }
            return new Result(CustomExceptionType.OK);
        }
    }

    /**
     * 编辑用户信息
     *
     * @param updateUser 用户信息
     * @param sub        操作账号
     * @return
     */

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result updateUser(UpdateUser updateUser, String sub) {
        SysUser byUserx = sysUserServiceDao.getByUserName(sub);
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(updateUser, sysUser);
        sysUser.setUpdateBy(byUserx.getUsername());
        sysUser.setId(updateUser.getId());
        sysUserServiceDao.updateById(sysUser);
        Date updateTime = new Date();
        //            更新站点关系
        SysUser user = sysUserServiceDao.getById(updateUser.getId());
        if (user.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode()) {
            userDepartService.removeUserId(updateUser.getId());
            if (CollectionUtils.isEmpty(updateUser.getDepartId())) {
                updateUser.setDepartId(Arrays.asList(1L));
            } else {
                updateUser.getDepartId().add(1L);
            }
            List<SysUserDepart> userDeparts = updateUser.getDepartId().stream().map(depgl -> new SysUserDepart(updateUser.getId(), depgl, byUserx.getUseOrgCode(), false, byUserx.getUsername(), byUserx.getUsername(), updateTime)).collect(Collectors.toList());
            userDepartService.saveBatch(userDeparts);
        }

//              更新角色信息
        if (byUserx.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue()) {
            if (user.getUserIdentity().intValue() != UserIdentityEnum.DEPART.getCode()) {
                userRoleService.removeUserId(updateUser.getId());
                if (CollectionUtils.isNotEmpty(updateUser.getRoleIds())) {
                    List<SysUserRole> collect = updateUser.getRoleIds().stream().map(rid -> new SysUserRole(updateUser.getId(), rid, byUserx.getUseOrgCode(), false, sysUser.getUsername(), sysUser.getUsername(), updateTime)).collect(Collectors.toList());
                    userRoleService.saveBatch(collect);
                }
            }
        } else {
//            非大正 例如富华新建用户
            userRoleService.removeUserId(updateUser.getId());
            if (CollectionUtils.isNotEmpty(updateUser.getRoleIds())) {
                List<SysUserRole> collect = updateUser.getRoleIds().stream().map(rid -> new SysUserRole(updateUser.getId(), rid, byUserx.getUseOrgCode(), false, sysUser.getUsername(), sysUser.getUsername(), updateTime)).collect(Collectors.toList());
                userRoleService.saveBatch(collect);
            }
        }
        redisUtil.del("userRoleService.listRoleCode::" + user.getUsername() + user.getUseOrgCode()
                , "rolePermissionService.listRolePermissionCode::" + user.getUsername() + user.getUseOrgCode());
        return new Result(CustomExceptionType.OK);
    }


    @Override
    public Result<UserInfo> getInfo(String sub) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        SysDepart sysDepart = businessDepartService.getByCode(byUserName.getUseOrgCode());
        if (sysDepart == null) {
            throw new CustomException(CustomExceptionType.USER_IS_NULL, CustomResponseCode.ERR19);
        }
        UserInfo userInfo = new UserInfo();
        List<SysRole> roleList = authRoleCommon.getSysRoles(byUserName, sysDepart);
        UserMessage userMessage = new UserMessage();
        if (CollectionUtils.isNotEmpty(roleList)) {
            List<Long> roleIds = roleList.stream().map(role -> role.getRoleId()).collect(Collectors.toList());
            List<String> roleCodes = roleList.stream().map(role -> role.getRoleCode()).collect(Collectors.toList());
            String roleNames = roleList.stream().map(role -> role.getRoleName()).collect(Collectors.joining("|"));
            List<String> permissionCode = rolePermissionService.listRolePermissionCode(roleIds, byUserName.getUsername(), byUserName.getUseOrgCode());
            userInfo.setRoles(roleCodes);
            userInfo.setPermissions(permissionCode);
            userMessage.setRoleName(roleNames);
        }

        ResDepart resDepart = new ResDepart();
        BeanUtils.copyProperties(sysDepart, resDepart);
        resDepart.setDepartId(sysDepart.getId());
        userMessage.setSysDepart(resDepart);
        BeanUtils.copyProperties(byUserName, userMessage);
        userMessage.setUserId(byUserName.getId());
        userInfo.setUser(userMessage);
        return new Result(CustomExceptionType.OK, userInfo);
    }


    @Override
    public Result<RoutersInfo> getRouters(String sub) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        SysDepart sysDepart = businessDepartService.getByCode(byUserName.getUseOrgCode());
        List<SysRole> roleList = authRoleCommon.getSysRoles(byUserName, sysDepart);
        if (CollectionUtils.isEmpty(roleList)) {
            return new Result(CustomExceptionType.OK_NO_DATA);
        }
        List<Long> roleIds = roleList.stream().map(role -> role.getRoleId()).collect(Collectors.toList());
        QueryWrapper<SysRolePermission> rolePermissionQueryWrapper = new QueryWrapper<>();
        rolePermissionQueryWrapper.eq("org_code", byUserName.getUseOrgCode());
        rolePermissionQueryWrapper.in("role_id", roleIds);
        List<SysRolePermission> permissions = rolePermissionService.list(rolePermissionQueryWrapper);
        if (CollectionUtils.isNotEmpty(permissions)) {
            List<Long> perIds = permissions.stream().map(per -> per.getPermissionId()).collect(Collectors.toList());
            QueryWrapper<SysPermission> permissionQueryWrapper = new QueryWrapper<>();
            permissionQueryWrapper.in("id", perIds);
            permissionQueryWrapper.eq("is_route", true);
            permissionQueryWrapper.orderByAsc("sort_no");
            List<SysPermission> list = permissionService.list(permissionQueryWrapper);
            List<SysPermission> permissionsNext = new ArrayList<>();
            List<RoutersInfo> parRout = ChildrenUtils.parPermissionNext(list, permissionsNext);
            List<RoutersInfo> parRoutEnd = ChildrenUtils.getChildRoutersInfo(parRout, permissionsNext);
            return new Result(CustomExceptionType.OK, parRoutEnd);
        }
        throw new CustomException(CustomExceptionType.OK_NO_DATA);
    }


    @Override
    public Result<SysRole> getRoles(String sub, PageLimit pageLimit, SelRole selRole) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        List<SysRole> list = roleService.listNoBasicsRole(byUserName.getUseOrgCode(), selRole.getRoleName(), selRole.getRoleCode(), selRole.getStatus(), selRole.getStartTime(), selRole.getEndTime());
        PageInfo<SysRole> paInfo = new PageInfo<>(list);
        return new Result(CustomExceptionType.OK, paInfo.getList(), paInfo.getTotal());
    }

    @CacheEvict(cacheNames = {"businessUserService.getRoles"}, allEntries = true)
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result addRole(AddRole addRole, String sub) {
        SysRole sysRole = roleService.selRoleCode(addRole.getRoleCode());
        if (sysRole != null) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR14);
        }
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        SysRole role = new SysRole();
        BeanUtils.copyProperties(addRole, role);
        role.setOrgCode(byUserName.getUseOrgCode());
        role.setDelFlag(false);
        role.setCreateBy(byUserName.getUsername());
        role.setBasicsRole(BasicsRole.NJC.getCode());
        roleService.save(role);
        List<Long> permissionId = addRole.getPermissionId();
        if (CollectionUtils.isNotEmpty(permissionId)) {
            List<SysRolePermission> rolePermissions = permissionId.stream().map(pid -> new SysRolePermission(role.getRoleId(), pid, byUserName.getUseOrgCode(), false, byUserName.getUsername())).collect(Collectors.toList());
            rolePermissionService.saveBatch(rolePermissions);
        }
        return new Result(CustomExceptionType.OK);
    }


    @Override
    public Result addPermission(AddPermission addPermission, String sub) {
        if (addPermission.getParentId().longValue() != 0) {
            SysPermission byId = permissionService.getById(addPermission.getParentId());
            if (byId.getMenuType().getCode().intValue() == MenuType.F.getCode()) {
                if (addPermission.getMenuType().getDesc().equals(MenuType.M.getDesc()) || addPermission.getMenuType().getDesc().equals(MenuType.C.getDesc())) {
                    throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR18);
                }
            }

        }
        SysPermission permission = permissionService.selPermissionCode(addPermission.getPerms());
        if (permission != null) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR15);
        }
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        SysPermission sysPermission = new SysPermission();
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
        permissionService.save(sysPermission);
        return new Result(CustomExceptionType.OK);
    }


    /**
     * 更新角色信息
     *
     * @param updateRole
     * @param sub
     * @return
     */
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result updateRole(UpdateRole updateRole, String sub) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", updateRole.getRoleId());
        SysRole sysRole = new SysRole();
        BeanUtils.copyProperties(updateRole, sysRole);
        sysRole.setUpdateBy(byUserName.getUsername());
        roleService.update(sysRole, queryWrapper);
        QueryWrapper<SysRolePermission> roPerWp = new QueryWrapper<>();
        roPerWp.eq("role_id", updateRole.getRoleId());
        rolePermissionService.remove(roPerWp);
        List<Long> permissionId = updateRole.getPermissionId();
        if (CollectionUtils.isNotEmpty(permissionId)) {
            List<SysRolePermission> rolePermissions = permissionId.stream().map(pid ->
                    new SysRolePermission(updateRole.getRoleId(), pid, byUserName.getUseOrgCode(), false, byUserName.getUsername())).collect(Collectors.toList());
            rolePermissionService.saveBatch(rolePermissions);
        }
        return new Result(CustomExceptionType.OK);
    }

    @Override
    public Result disableEnabledRole(DisableEnabledRole enabledRole, String sub) {
        if (enabledRole.getStatus().intValue() == 0) {
            Integer roleId = userRoleService.countRoleUser(enabledRole.getRoleId());
            if (roleId != null && roleId.intValue() > 0) {
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR32);
            }
        }
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        SysRole sysRole = new SysRole();
        sysRole.setRoleId(enabledRole.getRoleId());
        sysRole.setStatus(enabledRole.getStatus());
        sysRole.setUpdateBy(byUserName.getUsername());
        roleService.updateById(sysRole);
        return new Result(CustomExceptionType.OK);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result delRole(Long delRole, String sub) {
        Integer coutuserRole = userRoleService.countRoleUser(delRole);
        if (coutuserRole != null && coutuserRole.intValue() > 0) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR30);
        }
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        SysRole sysRole = new SysRole();
        sysRole.setRoleId(delRole);
        sysRole.setUpdateBy(byUserName.getUsername());
        roleService.updateById(sysRole);
        roleService.removeById(delRole);
        return new Result(CustomExceptionType.OK);
    }


    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result delUser(Long delUser, String usernum, String sub) {
        SysUser userServiceById = sysUserServiceDao.getById(delUser);
        sysUserServiceDao.removeById(delUser);
        userDepartService.removeUserId(delUser);
        userRoleService.removeUserId(delUser);
        redisUtil.del("userRoleService.listRoleCode::" + userServiceById.getUsername() + userServiceById.getUseOrgCode()
                , "rolePermissionService.listRolePermissionCode::" + userServiceById.getUsername() + userServiceById.getUseOrgCode());
        return new Result(CustomExceptionType.OK);
    }


    @Override
    public Result disableEnabledUser(DisableEnabledUser enabledUser, String sub) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        SysUser sysUser = new SysUser();
        sysUser.setUpdateBy(byUserName.getUsername());
        sysUser.setId(enabledUser.getId());
        sysUser.setStatus(enabledUser.getStatus());
        sysUserServiceDao.updateById(sysUser);
        SysUser userServiceById = sysUserServiceDao.getById(enabledUser.getId());
        redisUtil.del("userRoleService.listRoleCode::" + userServiceById.getUsername() + userServiceById.getUseOrgCode()
                , "rolePermissionService.listRolePermissionCode::" + userServiceById.getUsername() + userServiceById.getUseOrgCode());
        return new Result(CustomExceptionType.OK);
    }


    @Override
    public Result resetUser(ResetUser resetUser, String sub) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        SysUser sysUser = new SysUser();
        sysUser.setPassword(Md5Util.md5(resetUser.getPassword()));
        sysUser.setId(resetUser.getUserId());
        sysUser.setUpdateBy(byUserName.getUsername());
        sysUser.setSecret("");
        sysUser.setRefSecret("");
        sysUserServiceDao.updateById(sysUser);
        return new Result(CustomExceptionType.OK);
    }


    @Override
    public Result selMenuPermission(String sub) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        SysDepart byCode = businessDepartService.getByCode(byUserName.getUseOrgCode());
        List<SysPermission> list = new ArrayList<>();
        if (byUserName.getUsername().equals(BasicsAdmin.Admin.getCode())) {
            QueryWrapper<SysPermission> wp = new QueryWrapper<>();
            wp.orderByAsc("sort_no");
            list = permissionService.list(wp);
        } else {
            List<Long> sysRoles = authRoleCommon.getSysRolesId(byUserName, byCode);
            String joinKey = StringUtils.join(sysRoles, "");
            list = rolePermissionService.listRolePermission(sysRoles, joinKey);
        }

        List<MenusInfo> menusInfos = new ArrayList<>();
        for (SysPermission sysPermission : list) {
            MenusInfo metaInfo = new MenusInfo();
            BeanUtils.copyProperties(sysPermission, metaInfo);
            metaInfo.setChildren(Lists.newArrayList());
            metaInfo.setMenuId(String.valueOf(sysPermission.getId()));
            metaInfo.setMenuName(sysPermission.getTitle());
            metaInfo.setCreateTime(sysPermission.getCreateTime());
            metaInfo.setMenuType(sysPermission.getMenuType());
            metaInfo.setParentId(String.valueOf(sysPermission.getParentId()));
            menusInfos.add(metaInfo);
        }
        return new Result(CustomExceptionType.OK, menusInfos);
    }


    @Override
    public Result updatePermission(UpdatePermission updatePermission, String sub) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        if (updatePermission.getIsRoute() == null){
            updatePermission.setIsRoute(1);
        }
        SysPermission sysPermission = new SysPermission();
        BeanUtils.copyProperties(updatePermission, sysPermission);
        sysPermission.setTitle(updatePermission.getMenuName());
        sysPermission.setId(updatePermission.getMenuId());
        sysPermission.setCreateBy(byUserName.getUsername());
        sysPermission.setHidden(updatePermission.getHidden());
        sysPermission.setName(StringToUpcase.toUpperCase(updatePermission.getPath()));
        sysPermission.setIsRoute(updatePermission.getIsRoute().intValue()==1? true:false);
        if (sysPermission.getMenuType().getCode().intValue() == MenuType.F.getCode().intValue()) {
            sysPermission.setIsRoute(false);
        }
        permissionService.updateById(sysPermission);
        return new Result(CustomExceptionType.OK);
    }


    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result delPermission(Long delPermission, String sub) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
//        查询角色管理的权限
        Integer coutRole = rolePermissionService.selectByPerId(delPermission);
        if (coutRole != null && coutRole.intValue() > 0) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR28);
        }
        Integer coutRoleDept = departPermissionService.selectByPerId(delPermission);
        if (coutRoleDept != null && coutRoleDept.intValue() > 0) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR29);
        }
//       删除当前节点
        permissionService.removeById(delPermission);
//        删除子节点
        QueryWrapper<SysPermission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", delPermission);
        List<SysPermission> list = permissionService.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            List<Long> perId = list.stream().map(pis -> pis.getId()).collect(Collectors.toList());
            QueryWrapper<SysRolePermission> roleQueryWrapper = new QueryWrapper<>();
            roleQueryWrapper.in("permission_id", perId);
            rolePermissionService.remove(roleQueryWrapper);
            permissionService.removeByIds(perId);
        }
//        删除当前主节点的管理角色表
        QueryWrapper<SysRolePermission> sysRolePermissionQueryWrapper = new QueryWrapper<>();
        sysRolePermissionQueryWrapper.eq("permission_id", delPermission);
        rolePermissionService.remove(sysRolePermissionQueryWrapper);
        return new Result(CustomExceptionType.OK);
    }


    @Transactional(rollbackFor = Throwable.class, isolation = Isolation.SERIALIZABLE)
    @Override
    public Result switchSite(SwitchSite switchSite, String sub) {
        SysDepart byId = businessDepartService.getById(switchSite.getId());
        if (byId == null) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR13);
        }
        if (byId.getStatus().intValue() == StatusEnum.Disable.getCode()) {
            throw new CustomException(CustomExceptionType.AUTHEN_TICATIIN_FAILURE, CustomResponseCode.ERR24);
        }
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        if (byUserName.getUseOrgCode() != null && byUserName.getUseOrgCode().equals(byId.getOrgCode())) {
            return new Result(CustomExceptionType.OK);
        }
        SysUser sysUser = new SysUser();
        sysUser.setId(byUserName.getId());
        sysUser.setUseOrgCode(byId.getOrgCode());
        sysUser.setUpdateBy(byUserName.getUsername());
        sysUserServiceDao.updateById(sysUser);
        return new Result(CustomExceptionType.OK);
    }


    @Override
    public Result querySwitchSite(SysUser byUserName, Boolean isAll) {
        List<SwitchSiteDo> departs = new ArrayList<>();
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue()) {
//            大正用户
            if (byUserName.getUseOrgCode().equals(byUserName.getOrgCode())) {
//                没有切换站点
                if (isAll) {
//                  获取所有站点信息
                    departs = businessDepartService.listAll();
                    return new Result(CustomExceptionType.OK, departs);
                } else {
//                    获取关联站点信息
                    departs = getSwitchSiteDos(byUserName, departs);
                    return new Result(CustomExceptionType.OK, departs);
                }
            } else {
//                切换了站点
                if (isAll) {
                    SwitchSiteDo switchSiteDo = businessDepartService.getByOrgCode(byUserName.getUseOrgCode());
                    return Result.ok(Lists.newArrayList(switchSiteDo));
                } else {
//                   获取关联站点信息
                    departs = getSwitchSiteDos(byUserName, departs);
                    return new Result(CustomExceptionType.OK, departs);
                }
            }
        } else {
//           非大正用户 获取关联站点信息
            departs = getSwitchSiteDos(byUserName, departs);
            return new Result(CustomExceptionType.OK, departs);
        }
// SwitchSiteDo switchSiteDo = businessDepartService.getByOrgCode(byUserName.getUseOrgCode());
//                return Result.OK(Lists.newArrayList(switchSiteDo));
    }

    public List<SwitchSiteDo> getSwitchSiteDos(SysUser byUserName, List<SwitchSiteDo> departs) {
        QueryWrapper<SysUserDepart> userDepQwp = new QueryWrapper<>();
        userDepQwp.eq("user_id", byUserName.getId());
        List<SysUserDepart> list = userDepartService.list(userDepQwp);
        if (CollectionUtils.isNotEmpty(list)) {
            List<Long> ids = list.stream().map(dep -> dep.getDepartId()).collect(Collectors.toList());
            departs = businessDepartService.listId(ids);

        }
        return departs;
    }

    @Override
    public Result<MenusInfo> selMenuPermissionId(Long id, String sub) {
        SysPermission sysPermission = permissionService.getById(id);
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
        metaInfo.setParentId(String.valueOf(sysPermission.getParentId()));
        return new Result(CustomExceptionType.OK, metaInfo);
    }

    @Override
    public Result treeSelect(String sub, SysUser byUserName) {
//        获取用户
        List<SysPermission> permissions = new ArrayList<>();
        SysDepart byCode = businessDepartService.getByCode(byUserName.getUseOrgCode());
        if (byUserName.getUsername().equals(BasicsAdmin.Admin.getCode())) {
            if (byCode.getParentId().longValue() == 0) {
                permissions = permissionService.list();
            } else {
                List<Long> sysRoles = authRoleCommon.getSysRolesId(byUserName, byCode);
                if (CollectionUtils.isNotEmpty(sysRoles)) {
                    String joinKey = StringUtils.join(sysRoles, "");
                    permissions = rolePermissionService.listRolePermission(sysRoles, joinKey);
                }
            }
        } else {
            List<Long> sysRoles = authRoleCommon.getSysRolesId(byUserName, byCode);
            if (CollectionUtils.isNotEmpty(sysRoles)) {
                String joinKey = StringUtils.join(sysRoles, "");
                permissions = rolePermissionService.listRolePermission(sysRoles, joinKey);
            }
        }
        List<SysPermission> permissionsNext = new ArrayList<>();
        List<RoutersInfo> parRout = ChildrenUtils.parPermissionNext(permissions, permissionsNext);
        List<RoutersInfo> parRoutEnd = ChildrenUtils.getChildRoutersInfo(parRout, permissionsNext);
        List<Map<String, Object>> map = ChildrenUtils.treeSelectMap(parRoutEnd);
        return new Result(CustomExceptionType.OK, map);
    }


    @Override
    public Result treeSelectCheck(Long id, String sub) {
        Map<String, Object> map = new HashMap<>();
        List<Long> longList = Arrays.asList(id);
        String joinKey = StringUtils.join(longList, "");
        List<SysPermission> sysPermissions = rolePermissionService.listRolePermission(longList, joinKey);
        if (CollectionUtils.isNotEmpty(sysPermissions)) {
            List<Long> checkIds = sysPermissions.stream().map(sysps -> sysps.getId()).collect(Collectors.toList());
            map.put("checkedKeys", checkIds);
        }
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        Result result = this.treeSelect(sub, byUserName);
        map.put("menus", result.getData());
       return  Result.ok(map);
    }

    @Override
    public Result<SysRole> getRolesDetails(Long id, String sub) {
        SysRole byId = roleService.getById(id);
        return new Result<>(CustomExceptionType.OK, byId);
    }


    @Override
    public Result userLists(PageLimit pageLimit, SelUser selUser, String sub) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        List<UserListRes> list = sysUserServiceDao.listUserOrgCode(byUserName.getUseOrgCode(), selUser.getRealname(), selUser.getUsername(), selUser.getStatus(), selUser.getStartTime(), selUser.getEndTime());
        PageInfo pageInfo = new PageInfo(list);
        return new Result(CustomExceptionType.OK, pageInfo.getList(), pageInfo.getTotal());
    }

    @Override
    public Result getRolesNotAdmin(String sub, Long userId) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        QueryWrapper<SysRole> rwp = new QueryWrapper<>();
        rwp.eq("org_code", byUserName.getUseOrgCode());
        rwp.ne("basics_role", BasicsRole.JC.getCode());
        List<SysRole> roleList = roleService.list(rwp);
        Map<String, Object> map = new HashMap<>();
        if (CollectionUtils.isNotEmpty(roleList)) {
            roleList.stream().forEach(ro -> {
                if (ro.getBasicsRole().intValue() == BasicsRole.Admin.getCode().intValue()) {
                    ro.setDisabled(true);
                } else {
                    ro.setDisabled(false);
                }
            });
        }
        //        角色信息
        map.put("roles", roleList);
//       传递用户信息
        if (userId != null) {
            SysUser userServiceById = sysUserServiceDao.getById(userId);
            List<Long> roleId = userRoleService.listRoleId(userServiceById.getId(), byUserName.getUseOrgCode(), BasicsRole.NJC.getCode());
            map.put("roledIds", roleId);
            //      用户信息
            map.put("user", userServiceById);
            SysDepart byParentId = businessDepartService.getByParentId(0);
            //       关联站点
            List<SysUserDepart> userDeparts = userDepartService.listByUserIdOrgcodeNeDepartId(userServiceById.getId(), userServiceById.getUseOrgCode(), byParentId.getId());
            List<Long> departIds = userDeparts.stream().map(usd -> usd.getDepartId()).collect(Collectors.toList());
            map.put("departIds", departIds);
        }
//        站点信息
        List<SysDepart> allDepart = businessDepartService.listNotDz();
        SysDepart byCode = businessDepartService.getByCode(byUserName.getUseOrgCode());
        if (byCode.getParentId().longValue() == 0) {
//            所有站点
            map.put("departs", allDepart);
        } else {
            QueryWrapper<SysUserDepart> userDepQwp = new QueryWrapper<>();
            userDepQwp.eq("user_id", byUserName.getId());
            userDepQwp.eq("org_code", byUserName.getUseOrgCode());
            List<SysUserDepart> list = userDepartService.list(userDepQwp);
            if (CollectionUtils.isNotEmpty(list)) {
                List<Long> collect = list.stream().map(dep -> dep.getDepartId()).collect(Collectors.toList());
                QueryWrapper<SysDepart> depQwp = new QueryWrapper<>();
                depQwp.in("id", collect);
                List<SysDepart> departs = businessDepartService.list(depQwp);
                map.put("departs", departs);
            } else {
                map.put("departs", new ArrayList<>());
            }
        }
        map.put("affiliationDepartIds", allDepart);
        Result result = new Result(CustomExceptionType.OK, map);
        return result;
    }

    @Override
    public Result getUserId(Long userId, String sub) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("avatar", "create_by", "create_time", "id", "realname", "status", "update_by", "update_time", "use_org_code", "username");
        queryWrapper.eq("id", userId);
        SysUser sysUser = sysUserServiceDao.getOne(queryWrapper);
        return new Result(CustomExceptionType.OK, sysUser);
    }

    @Override
    public Result putPassword(String sub, PutUserPasswordVo putUserInfoVo) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        if (!putUserInfoVo.getPasswordNew().equals(putUserInfoVo.getPasswordRepeat())) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_44);
        }
        if (!Md5Util.md5(putUserInfoVo.getPasswordOld()).equals(byUserName.getPassword())) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_45);
        }
        byUserName.setPassword(Md5Util.md5(putUserInfoVo.getPasswordNew()));
        sysUserServiceDao.updateById(byUserName);
        redisUtil.del("userRoleService.listRoleCode::" + byUserName.getUsername() + byUserName.getUseOrgCode()
                , "rolePermissionService.listRolePermissionCode::" + byUserName.getUsername() + byUserName.getUseOrgCode());
        return null;
    }

    @Override
    public Result putUpload(String sub, MultipartFile[] file) {
        return sysUserServiceDao.putUpload(sub, file, address, parentPath);
    }


}
