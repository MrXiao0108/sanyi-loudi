package com.dzics.kanban.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.kanban.model.entity.SysRole;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 不走逻辑删除
     * @param roleCode  角色编码
     * @return
     */
    SysRole selRoleCode(String roleCode);

    /**
     * @param affiliationDepartId  归属站点id
     * @param code 是否基础角色
     * @return
     */
    SysRole getRole(Long affiliationDepartId, Integer code);


    /**
     * 根据站点 id 更新角色状态
     * @param departId 站点id
     * @param status 状态
     */
    void updateDepartId(Long departId, Integer status);

    /**
     * 角色列表去除基础权限
     * @param useOrgCode 站点编码
     * @param roleName 角色名称
     * @param roleCode 角色编码
     * @param status 状态
     * @param createTime 创建开始时间
     * @param endTime 创建解释时间
     * @return
     */
    List<SysRole> listNoBasicsRole(String useOrgCode, String roleName, String roleCode, Integer status, Date createTime, Date endTime);

}
