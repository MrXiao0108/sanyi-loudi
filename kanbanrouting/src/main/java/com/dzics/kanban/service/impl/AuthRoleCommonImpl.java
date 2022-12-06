package com.dzics.kanban.service.impl;

import com.dzics.kanban.enums.UserIdentityEnum;
import com.dzics.kanban.model.entity.SysDepart;
import com.dzics.kanban.model.entity.SysRole;
import com.dzics.kanban.model.entity.SysUser;
import com.dzics.kanban.service.AuthRoleCommon;
import com.dzics.kanban.service.SysUserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ZhangChengJun
 * Date 2021/1/14.
 * @since
 */
@Service
@Slf4j
public class AuthRoleCommonImpl implements AuthRoleCommon {
    @Autowired
    private SysUserRoleService userRoleService;
    @Override
    public List<SysRole> getSysRoles(SysUser byUserName, SysDepart sysDepart) {
        List<SysRole> roleList;
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue()) {
//          大正用户
            if (byUserName.getAffiliationDepartId().compareTo(sysDepart.getId()) == 0) {
//            没有切换站点
                roleList = userRoleService.listRoleCode(byUserName.getId(), byUserName.getUseOrgCode(),byUserName.getUsername());
            } else {
                roleList = userRoleService.listOrgCodeBasicsRole(byUserName.getUseOrgCode());
            }
        } else if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DEPART.getCode().intValue()) {
//            站点用户
            roleList = userRoleService.listRoleCode(byUserName.getId(), byUserName.getUseOrgCode(),byUserName.getUsername());
        } else {
//            站点子用户
            roleList = userRoleService.listRoleCode(byUserName.getId(), byUserName.getUseOrgCode(),byUserName.getUsername());
        }
        return roleList;
    }

    @Override
    public List<Long> getSysRolesId(SysUser byUserName, SysDepart sysDepart) {
        List<SysRole> roleList = new ArrayList<>();
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue()) {
//          大正用户
            if (byUserName.getAffiliationDepartId().compareTo(sysDepart.getId()) == 0) {
//            没有切换站点
                roleList = userRoleService.listRoleCode(byUserName.getId(), byUserName.getUseOrgCode(),byUserName.getUsername());
            } else {
                roleList = userRoleService.listOrgCodeBasicsRole(byUserName.getUseOrgCode());
            }
        } else if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DEPART.getCode().intValue()) {
//            站点用户
            roleList = userRoleService.listRoleCode(byUserName.getId(), byUserName.getUseOrgCode(),byUserName.getUsername());
        } else {
//            站点子用户
            roleList = userRoleService.listRoleCode(byUserName.getId(), byUserName.getUseOrgCode(),byUserName.getUsername());
        }
        return roleList.stream().map(ro -> ro.getRoleId()).collect(Collectors.toList());
    }

}
