package com.dzics.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.common.model.entity.SysPermission;
import com.dzics.common.model.entity.SysRolePermission;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * <p>
 * 角色权限表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
public interface SysRolePermissionService extends IService<SysRolePermission> {

    /**
     * 根据用户角色id 获取对应的权限
     *
     * @param collect    角色id
     * @param username
     * @param useOrgCode
     * @return
     */
    @Cacheable(cacheNames = "rolePermissionService.listRolePermissionCode", key = "#username+#useOrgCode")
    List<String> listRolePermissionCode(List<Long> collect, String username, String useOrgCode);

    @Cacheable(cacheNames = "rolePermissionService.listRolePermission", key = "#joinKey")
    List<SysPermission> listRolePermission(List<Long> collect, String joinKey);


    /**
     * @param roleId 根据角色id删除关系表
     */
    void removeRoleId(Long roleId);

    Integer selectByPerId(Long delPermission);
}
