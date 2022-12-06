package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.SysRolePermissionMapper;
import com.dzics.common.model.entity.SysPermission;
import com.dzics.common.model.entity.SysRolePermission;
import com.dzics.common.service.SysRolePermissionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 角色权限表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Service
public class SysRolePermissionServiceImpl extends ServiceImpl<SysRolePermissionMapper, SysRolePermission> implements SysRolePermissionService {


    @Override
    public List<String> listRolePermissionCode(List<Long> collect, String username, String useOrgCode) {
        return baseMapper.listRolePermissionCode(collect);
    }

    @Override
    public List<SysPermission> listRolePermission(List<Long> collect, String joinKey) {
        return baseMapper.listRolePermission(collect);
    }

    @Override
    public void removeRoleId(Long roleId) {
        QueryWrapper<SysRolePermission> wpRoPermi = new QueryWrapper<>();
        wpRoPermi.eq("role_id",roleId);
        baseMapper.delete(wpRoPermi);
    }

    @Override
    public Integer selectByPerId(Long delPermission) {
        QueryWrapper<SysRolePermission> wpRoPermi = new QueryWrapper<>();
        wpRoPermi.eq("permission_id",delPermission);
        return baseMapper.selectCount(wpRoPermi);

    }


}
