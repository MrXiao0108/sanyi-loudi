package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.SysDepartMapper;
import com.dzics.common.model.entity.SysDepart;
import com.dzics.common.service.SysDepartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 站点公司表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Service
public class SysDepartServiceImpl extends ServiceImpl<SysDepartMapper, SysDepart> implements SysDepartService {
    @Autowired
    private SysDepartMapper sysDepartMapper;



    @Override
    public SysDepart getByParentId(int i) {
        QueryWrapper<SysDepart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", 0);
        return sysDepartMapper.selectOne(queryWrapper);
    }

    @Override
    public List<SysDepart> listNotDz() {
        QueryWrapper<SysDepart> departQueryWrapper = new QueryWrapper<>();
        departQueryWrapper.ne("parent_id",0);
        return sysDepartMapper.selectList(departQueryWrapper);

    }
}
