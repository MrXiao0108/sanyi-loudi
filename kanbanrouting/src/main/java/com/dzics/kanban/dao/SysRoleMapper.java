package com.dzics.kanban.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.kanban.model.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 角色表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    SysRole selRoleCode(@Param("roleCode") String roleCode);
    List<String> getRoleName(Long id);

    String getSystemRunConfig();

}
