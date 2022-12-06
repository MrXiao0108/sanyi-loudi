package com.dzics.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.common.model.entity.SysUser;
import com.dzics.common.model.request.PutUserInfoVo;
import com.dzics.common.model.request.ReqUploadBase64;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.UserListRes;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
public interface SysUserServiceDao extends IService<SysUser> {

    /**
     * 根据账号获取用户信息
     *
     * @param sub
     * @return
     */
    @Cacheable(cacheNames = "sysUserService.getByUserName", key = "#sub")
    SysUser getByUserName(String sub);

    /**
     * 获取站点下的用户数量
     *
     * @param orgCode 站点编码
     * @return
     */
    Integer getCountByOrgCode(String orgCode);

    /**
     * @param useOrgCode 系统编码
     * @param realname   用户名
     * @param username   账号
     * @param status     状态
     * @param createTime
     * @param endTime
     * @return
     */
    List<UserListRes> listUserOrgCode(String field,String type,String useOrgCode, String realname, String username, Integer status, Date createTime, Date endTime);

    /**
     * 编辑用户基本信息
     *
     * @param sub
     * @param putUserInfoVo 1. 清除用户信息
     * @return
     */
    @CacheEvict(cacheNames = {"businessUserService.getInfo"}, key = "#sub")
    Result put(String sub, PutUserInfoVo putUserInfoVo);


    /**
     * 头像更改  清除用户缓存信息 businessUserService.getInfo
     *
     * @param sub
     * @param file 1. 清除用户缓存信息 businessUserService.getInfo
     * @return
     */
    @CacheEvict(cacheNames = {"businessUserService.getInfo"}, key = "#sub")
    Result putAvatar(String sub, MultipartFile file);

    Result putUpload(String sub, MultipartFile [] file, String address, String filePath);
    /**
     * 未走逻辑删除逻辑
     *
     * @param username 根据用户名查询是否存在
     * @return
     */
    Long listUsername(String username);


    /**
     * 根据用户id查询用户角色
     */

    String getUserRoleName(Long id);

    /**
     * 根据sub 判断当前用户的查询userOrgCode
     */
    String getUserOrgCode(String sub);

    Result putAvatarBase64(String sub, ReqUploadBase64 files, String address, String parentPath);

}
