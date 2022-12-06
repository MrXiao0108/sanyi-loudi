package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.SysConfigMapper;
import com.dzics.common.enums.ConfigType;
import com.dzics.common.model.entity.SysConfig;
import com.dzics.common.model.response.RunDataModel;
import com.dzics.common.service.SysConfigService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 系统运行模式 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-31
 */
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {
    @Override
    public RunDataModel systemRunModel() {
        return baseMapper.systemRunModel(ConfigType.rumModel);
    }

    @Override
    public void editSystemRunModel(RunDataModel runDataModel) {
        QueryWrapper<SysConfig> wp = new QueryWrapper<>();
        wp.eq("type_config", ConfigType.rumModel);
        SysConfig sysConfig = new SysConfig();
        sysConfig.setPlanday(runDataModel.getPlanDay());
        sysConfig.setRundatamodel(runDataModel.getRunDataModel());
        sysConfig.setTablename(runDataModel.getTableName());
        baseMapper.update(sysConfig, wp);
    }

    @Override
    public SysConfig getConfig(String i) {
        QueryWrapper<SysConfig> wp = new QueryWrapper<>();
        wp.eq("type_config", i);
        return baseMapper.selectOne(wp);
    }

    @Override
    public void updateConfigType(String lockPassword) {
        QueryWrapper<SysConfig> wp = new QueryWrapper<>();
        wp.eq("type_config", ConfigType.ConfigPassword);
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigValue(lockPassword);
        baseMapper.update(sysConfig, wp);
    }

    @Override
    public List<String> getMouthDate(int year, int monthValue) {
        return baseMapper.getMouthDate(year, monthValue);
    }
}
