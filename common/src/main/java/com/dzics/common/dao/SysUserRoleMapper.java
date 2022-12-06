package com.dzics.common.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.entity.SysRole;
import com.dzics.common.model.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    List<SysRole> listRoleCode(@Param("id") Long id, @Param("useOrgCode") String useOrgCode);

    List<String> listRoleId(@Param("id") Long id, @Param("useOrgCode") String useOrgCode, @Param("code") Integer code);
}
