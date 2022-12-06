package com.dzics.kanban.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.kanban.model.entity.SysDepartPermission;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-13
 */
public interface SysDepartPermissionService extends IService<SysDepartPermission> {

    /**
     * @param departId 站点id
     */
    void removeDepartId(Long departId);

    /**
     * 查询站点是否有授权的权限
     *
     * @param affiliationDepartId 站点id
     * @return
     */
    List<SysDepartPermission> listDepartIdPerMission(Long affiliationDepartId);

    /**
     * 查询站点中关联的权限
     * @param delPermission
     * @return
     */
    Integer selectByPerId(Long delPermission);
}
