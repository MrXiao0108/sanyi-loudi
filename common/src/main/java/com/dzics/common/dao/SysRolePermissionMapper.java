package com.dzics.common.dao;

import com.dzics.common.model.entity.SysPermission;
import com.dzics.common.model.entity.SysRolePermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 角色权限表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Mapper
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {

    List<String> listRolePermissionCode(@Param("list") List<Long> list);

    List<SysPermission> listRolePermission(List<Long> list);
}
