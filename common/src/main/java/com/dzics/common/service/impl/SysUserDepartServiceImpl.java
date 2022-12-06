package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.SysUserDepartMapper;
import com.dzics.common.model.entity.SysUserDepart;
import com.dzics.common.service.SysUserDepartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Service
public class SysUserDepartServiceImpl extends ServiceImpl<SysUserDepartMapper, SysUserDepart> implements SysUserDepartService {
    @Autowired
    private SysUserDepartMapper sysUserDepartMapper;

    @Override
    public List<SysUserDepart> listByUserIdOrgcodeNeDepartId(Long userId, String useOrgCode,Long departId) {
        QueryWrapper<SysUserDepart> userDepQwp = new QueryWrapper<>();
        userDepQwp.eq("user_id", userId);
        userDepQwp.eq("org_code",useOrgCode);
        userDepQwp.ne("depart_id", departId);
        return sysUserDepartMapper.selectList(userDepQwp);
    }

    @Override
    public void removeUserId(Long delUser) {
        QueryWrapper<SysUserDepart> wpUsDep = new QueryWrapper<>();
        wpUsDep.eq("user_id",delUser);
        sysUserDepartMapper.delete(wpUsDep);
    }
}
