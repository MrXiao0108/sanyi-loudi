package com.dzics.common.service;

import com.dzics.common.model.entity.SysPermission;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 菜单权限表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
public interface SysPermissionService extends IService<SysPermission> {

    /**
     * 权限信息
     * @param perms 权限编码
     * @return
     */
    SysPermission selPermissionCode(String perms);
}
