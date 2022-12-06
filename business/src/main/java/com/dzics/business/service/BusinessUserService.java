package com.dzics.business.service;

import com.dzics.business.model.response.MenusInfo;
import com.dzics.business.model.response.RoutersInfo;
import com.dzics.business.model.response.UserInfo;
import com.dzics.business.model.vo.RegisterVo;
import com.dzics.business.model.vo.rolemenu.*;
import com.dzics.business.model.vo.user.*;
import com.dzics.common.model.entity.SysRole;
import com.dzics.common.model.entity.SysUser;
import com.dzics.common.model.request.PutUserPasswordVo;
import com.dzics.common.model.request.ReqUploadBase64;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.role.ResSysRole;
import com.dzics.common.util.PageLimit;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @author ZhangChengJun
 * Date 2021/1/6.
 */
public interface BusinessUserService {
    /**
     * 添加用户
     *
     * @param registerVo 用户新增账号信息
     * @param sub        操作账号
     * @return
     */
    Result addUser(RegisterVo registerVo, String sub);

    /**
     * 获取用户信息
     *
     * @param sub 操作账号
     * @return
     */
    @Cacheable(cacheNames = "businessUserService.getInfo", key = "#sub")
    Result<UserInfo> getInfo(String sub);

    /**
     * 获取路由
     *
     * @param sub 操作账号
     * @return 路由采单
     */
    @Cacheable(cacheNames = "businessUserService.getRouters", key = "#sub")
    Result<RoutersInfo> getRouters(String sub);

    /**
     * 获取角色
     *
     * @param sub       操作账号
     * @param pageLimit 分页信息
     * @param selRole   查询信息
     * @return 角色列表
     */
    Result getRoles(String sub, PageLimit pageLimit, SelRole selRole);

    /**
     * 添加角色
     *
     * @param addRole 角色信息
     * @param sub     操作账号
     * @return
     */
    Result addRole(AddRole addRole, String sub);

    /**
     * 添加权限
     *
     * @param addPermission 权限信息
     * @param sub           操作账号
     *                      1.清除权限列表
     * @return
     */
    @CacheEvict(cacheNames = {"businessUserService.selMenuPermission"}, allEntries = true)
    Result addPermission(AddPermission addPermission, String sub);

    /**
     * 更新角色信息
     *
     * @param updateRole
     * @param sub        操作账号
     *                   1.清除用户缓存
     *                   2.清除用户路由
     *                   3.清楚角色
     *                   4.清除授权角色信息
     *                   5.清除授权权限信息
     *                   6.清除菜单列表
     *                   8.清除授权信息
     * @return
     */
    @CacheEvict(cacheNames = {"businessUserService.getInfo",
            "businessUserService.getRouters",
            "businessUserService.getRoles","userRoleService.listRoleCode",
    "rolePermissionService.listRolePermissionCode",
            "businessUserService.selMenuPermission","rolePermissionService.listRolePermission"}, allEntries = true)
    Result updateRole(UpdateRole updateRole, String sub);


    /**
     * 禁用启用角色
     *
     * @param enabledRole 禁用启用信息
     * @param sub         操作账号
     *                    1.清除用户缓存
     *                    2.清除用户路由
     *                    3.清除角色
     *                    4.清除授权角色信息
     *                     5.清除授权权限信息
     *                    8.清除鉴权信息
     * @return
     */
    @CacheEvict(cacheNames = {"businessUserService.getInfo",
            "businessUserService.getRouters",
            "businessUserService.getRoles",
            "userRoleService.listRoleCode"
    ,"rolePermissionService.listRolePermissionCode",
            "rolePermissionService.listRolePermission"}, allEntries = true)
    Result disableEnabledRole(DisableEnabledRole enabledRole, String sub);

    /**
     * 删除角色
     *
     * @param delRole 角色信息
     * @param sub     操作账号
     *                1.清除用户路由
     *                2.清除角色
     *                3.清除用户授权角色信息
     *                5.清除授权权限信息
     *
     * @return
     */
    @CacheEvict(cacheNames = {"businessUserService.getRouters",
            "businessUserService.getRoles","userRoleService.listRoleCode",
    "rolePermissionService.listRolePermissionCode","rolePermissionService.listRolePermission"}, allEntries = true)
    Result delRole(Long delRole, String sub);

    /**
     * 删除用户
     *
     * @param delUser 用户id
     * @param usernum
     * @param sub     操作账号
     *                1.清除用户信息
     *                2.清除用户路由
     *                3.清除用户对象
     * @return
     */
    @CacheEvict(cacheNames = {"businessUserService.getInfo"
            , "sysUserService.getByUserName",
            "businessUserService.getRouters"}, key = "#usernum")
    Result delUser(Long delUser, String usernum, String sub);

    /**
     * 编辑用户信息
     *
     * @param updateUser 用户信息
     * @param sub        操作账号
     *                   1.清除用户信息
     *                   2.清除用户路由
     *                   3.清除用户可切换站点
     *                   4.清除用户对象
     * @return
     */
    @CacheEvict(cacheNames = {"businessUserService.getInfo",
            "businessUserService.getRouters",
            "businessUserService.querySwitchSite",
            "sysUserService.getByUserName"}, allEntries = true)
    Result updateUser(UpdateUser updateUser, String sub);


    /**
     * 启用禁用用户
     *
     * @param enabledUser 用户启用禁用信息
     * @param sub         操作账号
     *                    1.清除用户信息
     *                    2.清除用户路由
     *                    3.清除用户对象
     * @return
     */
    @CacheEvict(cacheNames = {"businessUserService.getInfo", "businessUserService.getRouters", "sysUserService.getByUserName"}, key = "#enabledUser.username")
    Result disableEnabledUser(DisableEnabledUser enabledUser, String sub);

    /**
     * 重置密码
     *
     * @param resetUser 重置密码信息
     * @param sub       操作账号
     *                  1 清除用户信息
     *                  2.清除用户对象
     * @return
     */
    @CacheEvict(cacheNames = {"businessUserService.getInfo", "sysUserService.getByUserName"}, key = "#resetUser.username")
    Result resetUser(ResetUser resetUser, String sub);

    /**
     * @param sub 操作账号
     * @return
     */
    @Cacheable(cacheNames = "businessUserService.selMenuPermission", key = "#sub")
    Result selMenuPermission(String sub);


    /**
     * 编辑更新采单信息
     *
     * @param updatePermission 采单权限信息
     * @param sub              操作账号
     *                         1.清除用户缓存  清除用户缓存
     *                         2.清除用户路由
     *                         3.清除菜单列表
     *                         4.清除角色权限信息
     *                         5.清除授权权限信息
     * @return
     */
    @CacheEvict(cacheNames = {"businessUserService.getInfo", "businessUserService.getRouters",
            "businessUserService.selMenuPermission", "businessUserService.getRoles"
            , "userRoleService.listRoleCode",
    "rolePermissionService.listRolePermissionCode","rolePermissionService.listRolePermission"}, allEntries = true)
    Result updatePermission(UpdatePermission updatePermission, String sub);

    /**
     * 删除采单
     *
     * @param delPermission 采单id
     * @param sub           操作账号
     *                      1.清除菜单列表
     *                      2.清除角色
     *                      3.清除授权角色信息
     *                      4.清除授权权限信息
     * @return
     */
    @CacheEvict(cacheNames = {"businessUserService.selMenuPermission",
            "businessUserService.getRoles"
            ,"userRoleService.listRoleCode",
    "rolePermissionService.listRolePermissionCode"}, allEntries = true)
    Result delPermission(Long delPermission, String sub);


    /**
     * 切换站点
     *
     * @param switchSite 站点信息
     * @param sub        操作账号
     *                   1.  清除用户缓存
     *                   2. 清除路由列表
     *                   3.清除用户对象
     * @return
     */
    @CacheEvict(cacheNames = {"businessUserService.getInfo", "businessUserService.getRouters",
            "sysUserService.getByUserName"}, key = "#sub")
    Result switchSite(SwitchSite switchSite, String sub);

    /**
     * 可切换的站点列表
     *
     * @param byUserName 操作账号
     * @param isAll
     * @return
     */
    @Cacheable(cacheNames = {"businessUserService.querySwitchSite"}, key = "#byUserName.username+#byUserName.useOrgCode+#byUserName.orgCode+#isAll")
    Result querySwitchSite(SysUser byUserName, Boolean isAll);

    /**
     * @param id  权限id
     * @param sub 操作账号
     * @return
     */
    Result<MenusInfo> selMenuPermissionId(Long id, String sub);

    /**
     * @param sub 操作账号
     * @return
     */
    Result treeSelect(String sub, SysUser userOrgCode);

    /**
     * 操作账号
     *
     * @param id  角色id
     * @param sub 操作用户
     * @return
     */
    Result treeSelectCheck(Long id, String sub);

    /**
     * @param id  角色id
     * @param sub 操作用户
     * @return
     */
    Result<ResSysRole> getRolesDetails(Long id, String sub);

    /**
     * @param pageLimit
     * @param selUser
     * @param sub       操作用户
     * @return
     */
    Result userLists(PageLimit pageLimit, SelUser selUser, String sub);

    Result getRolesNotAdmin(String sub, Long userId);


    /**
     * @param userId 查询用户id
     * @param sub    操作用户
     * @return
     */
    Result getUserId(Long userId, String sub);

    /**
     * 修改用户密码
     *
     * @param sub
     * @param putUserInfoVo 1.清除用户缓存信息
     *                      2. 清楚用户对象
     * @return
     */
    @CacheEvict(cacheNames = {"businessUserService.getInfo","sysUserService.getByUserName"}, key = "#sub")
    Result putPassword(String sub, PutUserPasswordVo putUserInfoVo);

    Result putUpload(String sub, MultipartFile[] file);

    Result putAvatarBase64(String sub, ReqUploadBase64 files);
}
