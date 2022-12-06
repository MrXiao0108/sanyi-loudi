package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.SysRoleMapper;
import com.dzics.common.dao.SysUserRoleMapper;
import com.dzics.common.enums.BasicsRole;
import com.dzics.common.model.entity.SysRole;
import com.dzics.common.model.entity.SysUserRole;
import com.dzics.common.service.SysUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleStatus;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Override
    public List<SysRole> listRoleCode(Long id, String useOrgCode,String username) {
        return sysUserRoleMapper.listRoleCode(id, useOrgCode);
    }

    @Override
    public List<String> listRoleId(Long id, String useOrgCode, Integer code) {
        return sysUserRoleMapper.listRoleId(id, useOrgCode,code);
    }

    @Override
    public List<SysUserRole> listRoles(Long id) {
        QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", id);
        return sysUserRoleMapper.selectList(queryWrapper);

    }

    @Override
    public List<Long> getRoleId(Long userId) {
        QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(queryWrapper);
        return userRoles.stream().map(usro -> usro.getRoleId()).collect(Collectors.toList());
    }

    @Override
    public List<SysRole> listOrgCodeBasicsRole(String useOrgCode) {
        QueryWrapper<SysRole> roleQueryWrapper = new QueryWrapper<>();
        roleQueryWrapper.eq("org_code", useOrgCode);
        roleQueryWrapper.eq("basics_role", BasicsRole.JC.getCode());
        roleQueryWrapper.eq("status",1 );
        return sysRoleMapper.selectList(roleQueryWrapper);
    }

    @Override
    public void removeUserId(Long delUser) {
        QueryWrapper<SysUserRole> wpUsRo = new QueryWrapper<>();
        wpUsRo.eq("user_id", delUser);
        sysUserRoleMapper.delete(wpUsRo);
    }

    @Override
    public Integer countRoleUser(Long delRole) {
        QueryWrapper<SysUserRole> wpUsRo = new QueryWrapper<>();
        wpUsRo.eq("role_id", delRole);
        return sysUserRoleMapper.selectCount(wpUsRo);

    }

}
