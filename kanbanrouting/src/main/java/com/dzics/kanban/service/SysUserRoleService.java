package com.dzics.kanban.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.kanban.model.entity.SysRole;
import com.dzics.kanban.model.entity.SysUserRole;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
public interface SysUserRoleService extends IService<SysUserRole> {

    /**
     * 根据用户id 获取用户所有角色code
     *
     * @param id         用户id
     * @param useOrgCode 切换的系统编码
     * @return
     */
    @Cacheable(cacheNames = "userRoleService.listRoleCode", key = "#username+#useOrgCode")
    List<SysRole> listRoleCode(Long id, String useOrgCode, String username);

    /**
     * 根据用户id 获取用户所有角色code
     *
     * @param id         用户id
     * @param useOrgCode 切换的系统编码
     * @param code
     * @return
     */
    List<Long> listRoleId(Long id, String useOrgCode, Integer code);

    /**
     * @param id 用户 id
     * @return 用户对应的所有角色
     */
    List<SysUserRole> listRoles(Long id);

    /**
     * @param userId 用户id
     * @return 所有角色id
     */
    List<Long> getRoleId(Long userId);

    /**
     * 查询站点的基础角色信息
     *
     * @param useOrgCode 系统编码
     * @return
     */
    @Cacheable(cacheNames = "userRoleService.listOrgCodeBasicsRole",key = "#useOrgCode")
    List<SysRole> listOrgCodeBasicsRole(String useOrgCode);

    /**
     * 删除用户对应的角色
     *
     * @param delUser 用户id
     */
    void removeUserId(Long delUser);

    /**
     * 统计使用该角色的人数
     *
     * @param delRole 角色id
     * @return
     */
    Integer countRoleUser(Long delRole);

    /**
     * 获取系统运行环境
     * @return
     */
    String getSystemRunConfig();

}
