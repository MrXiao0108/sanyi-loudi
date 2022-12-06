package com.dzics.common.service;

import com.dzics.common.model.entity.SysDictItem;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.cache.annotation.CachePut;

/**
 * <p>
 * 系统字典详情 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
public interface SysDictItemService extends IService<SysDictItem> {
    String getDictTest(String datasource, String key);

    String getMomRunModel(String momRunModelKey, String orderCode);

    String updateAgvRunModel(String momRunModelKey, String orderCode, Integer logId);
}
