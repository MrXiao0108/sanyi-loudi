package com.dzics.kanban.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.kanban.model.entity.SysPermission;
import com.dzics.kanban.model.entity.SysRolePermission;
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
