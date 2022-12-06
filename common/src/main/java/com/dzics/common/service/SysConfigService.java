package com.dzics.common.service;

import com.dzics.common.model.entity.SysConfig;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.common.model.response.RunDataModel;

import java.util.List;

/**
 * <p>
 * 系统运行模式 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-31
 */
public interface SysConfigService extends IService<SysConfig> {
    RunDataModel systemRunModel();

    void editSystemRunModel(RunDataModel runDataModel);

    SysConfig getConfig(String i);

    void updateConfigType(String lockPassword);

    List<String> getMouthDate(int year, int monthValue);
}
