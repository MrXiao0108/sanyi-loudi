package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.SysRoleMapper;
import com.dzics.common.enums.BasicsRole;
import com.dzics.common.model.entity.SysRole;
import com.dzics.common.service.SysRoleService;
import com.dzics.common.util.UnderlineTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Override
    public SysRole selRoleCode(String roleCode) {
        return sysRoleMapper.selRoleCode(roleCode);

    }

    @Override
    public SysRole getRole(Long affiliationDepartId, Integer code) {
        QueryWrapper<SysRole> wpRole = new QueryWrapper<>();
        wpRole.eq("depart_id", affiliationDepartId);
        wpRole.eq("basics_role", BasicsRole.JC.getCode());
        return sysRoleMapper.selectOne(wpRole);
    }

    @Override
    public void updateDepartId(Long departId, Integer status) {
        QueryWrapper<SysRole> wpRole = new QueryWrapper<>();
        wpRole.eq("depart_id", departId);
        SysRole sysRole = new SysRole();
        sysRole.setStatus(status);
        sysRoleMapper.update(sysRole, wpRole);
    }

    @Override
    public List<SysRole> listNoBasicsRole(String field,String type,String useOrgCode, String roleName, String roleCode, Integer status, Date createTime, Date endTime) {
        QueryWrapper<SysRole> rwp = new QueryWrapper<>();
        rwp.eq("org_code", useOrgCode);
        rwp.ne("basics_role", BasicsRole.JC.getCode());
        if (!StringUtils.isEmpty(roleCode)) {
            rwp.eq("role_code", roleCode);
        }
        if (status != null) {
            rwp.eq("status", status);
        }
        if (createTime != null) {
            rwp.ge("create_time", createTime);
        }
        if (endTime != null) {
            rwp.le("create_time", endTime);
        }
        if (!StringUtils.isEmpty(roleName)){
            rwp.and(wp -> wp.like("role_name", roleName).or().like("description", roleName));
        }
        if(!StringUtils.isEmpty(type)){
            if("DESC".equals(type)){
                rwp.orderByDesc(UnderlineTool.humpToLine(field));
            } else if("ASC".equals(type)){
                rwp.orderByAsc(UnderlineTool.humpToLine(field));
            }
        }
        return sysRoleMapper.selectList(rwp);
    }
}
