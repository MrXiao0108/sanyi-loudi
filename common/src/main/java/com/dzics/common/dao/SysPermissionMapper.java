package com.dzics.common.dao;

import com.dzics.common.model.entity.SysPermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 菜单权限表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    SysPermission selPermissionCode(@Param("perms") String perms);
}
