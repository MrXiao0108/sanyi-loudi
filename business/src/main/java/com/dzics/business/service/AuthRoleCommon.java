package com.dzics.business.service;

import com.dzics.common.model.entity.SysDepart;
import com.dzics.common.model.entity.SysRole;
import com.dzics.common.model.entity.SysUser;

import java.util.List;

/**
 * 获取用户角色通用处理接口
 *
 * @author ZhangChengJun
 * Date 2021/1/14.
 * @since
 */
public interface AuthRoleCommon {
    /**
     * 根据当前用户 ，站点信息 加载权限信息
     * @param byUserName
     * @param sysDepart
     * @return 返回角色信息
     */
    List<SysRole> getSysRoles(SysUser byUserName, SysDepart sysDepart);
    /**
     * 根据当前用户 ，站点信息 加载权限信息
     * @param byUserName
     * @param sysDepart
     * @return 返回角色角色id
     */
    List<Long> getSysRolesId(SysUser byUserName, SysDepart sysDepart);
}
