package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.SysDictItemMapper;
import com.dzics.common.model.entity.SysDictItem;
import com.dzics.common.service.SysDictItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统字典详情 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Service
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemMapper, SysDictItem> implements SysDictItemService {
    @Autowired
    private SysDictItemMapper sysDictItemMapper;

    @Override
    public String getDictTest(String datasource, String key) {
        return sysDictItemMapper.getDictTest(datasource, key);
    }

    @Override
    public String getMomRunModel(String momRunModelKey, String orderCode) {
        return sysDictItemMapper.getDictCodeAndItemText(momRunModelKey, orderCode);
    }

    @Override
    public String updateAgvRunModel(String momRunModelKey, String orderCode, Integer logId) {
        QueryWrapper<SysDictItem> wp = new QueryWrapper<>();
        wp.eq("dict_code", momRunModelKey);
        wp.eq("item_text", orderCode);
        String rm = logId.intValue() == 1 ? "auto" : "manual";
        SysDictItem sysDictItem = new SysDictItem();
        sysDictItem.setItemValue(rm);
        int update = sysDictItemMapper.update(sysDictItem, wp);
        return update > 0 ? rm : null;
    }
}
