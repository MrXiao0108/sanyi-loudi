package com.dzics.kanban.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.kanban.model.entity.SysUser;
import com.dzics.kanban.model.response.UserListRes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    List<UserListRes> listUserOrgCode(@Param("useOrgCode") String useOrgCode, @Param("realname") String realname,
                                      @Param("username") String username, @Param("status") Integer status,
                                      @Param("createTime") Date createTime, @Param("endTime") Date endTime);

    Long listUsername(@Param("username") String username);
}
