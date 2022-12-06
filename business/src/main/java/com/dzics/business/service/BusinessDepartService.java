package com.dzics.business.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.business.model.vo.depart.*;
import com.dzics.common.model.entity.SysDepart;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.SwitchSiteDo;
import com.dzics.common.model.response.role.ResSysDepart;
import com.dzics.common.util.PageLimit;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface BusinessDepartService {

    /**
     * @param useOrgCode 系统编码
     * @return 返回站点信息
     */
    @Cacheable(cacheNames = "departService.getByCode",key = "#useOrgCode")
    SysDepart getByCode(String useOrgCode);

    ResSysDepart getByParentId(int i);

    /**
     * @return 排除大正站点 所有站点
     */
    List<ResSysDepart> listNotDz();

    /**
     * @param addDepart 站点信息
     * @param sub       操作用户
     * @return
     */
    @CacheEvict(cacheNames = {"businessDepartService.listAll","businessUserService.querySwitchSite"},allEntries = true)
    Result addDepart(AddDepart addDepart, String sub);

    /**
     * @param pageLimit 分页信息
     * @param selDepart 查询站点条件信息
     * @param sub       操作用户
     * @return
     */
    Result queryDepart(PageLimit pageLimit, SelDepart selDepart, String sub);

    /**
     * 添加站点
     *
     * @param sub      操作用户
     * @param departId 站点id
     * @return
     */
    Result getDepartMsg(String sub, Long departId);

    /**
     * 删除站点
     *
     * @param departId 站点id
     * @param sub      操作用户
     *                1 清除站点id查询信息
     *                2 清除站点编码存储信息
     * @return
     */
    @CacheEvict(cacheNames = {"businessDepartService.getById"
            , "departService.getByCode","rolePermissionService.listRolePermission",
            "businessDepartService.listAll","businessUserService.querySwitchSite"}, allEntries = true)
    Result<ResDepart> delDepart(Long departId, String sub);


    /**
     * @param sub      操作用户
     * @param departId 站点id
     * @return
     */
    Result getDepartDetails(String sub, Long departId);


    /**
     * @param updateDepart 跟新的站点信息
     * @param sub          操作用户
     *                     1.清楚所有缓存
     *                     2.清除用户路由信息
     *                     3.清除权限角色信息
     *                     4.清除授权权限信息
     *                     5.清除站点信息
     *                     6.清除站点编码查询信息
     * @return
     */
    @CacheEvict(cacheNames = {"businessUserService.getRouters", "businessUserService.getInfo",
            "userRoleService.listRoleCode",
            "rolePermissionService.listRolePermissionCode", "businessDepartService.getById",
            "departService.getByCode","userRoleService.listOrgCodeBasicsRole",
    "rolePermissionService.listRolePermission","businessUserService.querySwitchSite"}, allEntries = true)
    Result updateDepart(UpdateDepart updateDepart, String sub);

    /**
     * 启用禁用站点
     *
     * @param enabledDepart 禁用启用信息
     * @param sub           操作用户
     *                      1.清楚所有缓存
     *                      2.清除用户路由信息
     *                      3.清除站点id查询信息
     *                      4.清除站点编码查询信息
     * @return
     */
    @CacheEvict(cacheNames = {"businessUserService.getRouters", "businessUserService.getInfo",
            "businessDepartService.getById","departService.getByCode"
    ,"rolePermissionService.listRolePermission","businessUserService.querySwitchSite"}, allEntries = true)
    Result disableEnabledRole(DisableEnabledDepart enabledDepart, String sub);

    /**
     * @param id 站点id
     * @return
     */
    @Cacheable(value = "businessDepartService.getById", key = "#id")
    SysDepart getById(Long id);

    /**
     * @param depQwp
     * @return
     */
    List<SysDepart> list(QueryWrapper<SysDepart> depQwp);

    List<SwitchSiteDo> listId(List<Long> ids);

    /**
     * @return 获取所有站点
     */
    @Cacheable(value = "businessDepartService.listAll")
    List<SwitchSiteDo> listAll();

    SwitchSiteDo getByOrgCode(String orgCode);

}
