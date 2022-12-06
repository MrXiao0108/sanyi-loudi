package com.dzics.kanban.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.kanban.dao.SysPermissionMapper;
import com.dzics.kanban.model.entity.SysPermission;
import com.dzics.kanban.service.SysPermissionService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 菜单权限表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {

    @Override
    public SysPermission selPermissionCode(String perms) {
        return baseMapper.selPermissionCode(perms);
    }
}
