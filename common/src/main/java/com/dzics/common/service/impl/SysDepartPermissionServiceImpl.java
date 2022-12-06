package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.SysDepartPermissionMapper;
import com.dzics.common.model.entity.SysDepartPermission;
import com.dzics.common.service.SysDepartPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-13
 */
@Service
public class SysDepartPermissionServiceImpl extends ServiceImpl<SysDepartPermissionMapper, SysDepartPermission> implements SysDepartPermissionService {
    @Autowired
    private SysDepartPermissionMapper departPermissionMapper;

    @Override
    public void removeDepartId(Long departId) {
        QueryWrapper<SysDepartPermission> wpDepPermi = new QueryWrapper<>();
        wpDepPermi.eq("depart_id", departId);
        departPermissionMapper.delete(wpDepPermi);
    }

    @Override
    public List<SysDepartPermission> listDepartIdPerMission(Long affiliationDepartId) {
        QueryWrapper<SysDepartPermission> wpDepPermi = new QueryWrapper<>();
        wpDepPermi.eq("depart_id", affiliationDepartId);
        return departPermissionMapper.selectList(wpDepPermi);
    }

    @Override
    public Integer selectByPerId(Long delPermission) {
        QueryWrapper<SysDepartPermission> wpDepPermi = new QueryWrapper<>();
        wpDepPermi.eq("permission_id", delPermission);
        return departPermissionMapper.selectCount(wpDepPermi);
    }
}
