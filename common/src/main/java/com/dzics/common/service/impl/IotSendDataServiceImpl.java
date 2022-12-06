package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.common.model.entity.IotSendData;
import com.dzics.common.dao.IotSendDataMapper;
import com.dzics.common.service.IotSendDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-09-09
 */
@Service
public class IotSendDataServiceImpl extends ServiceImpl<IotSendDataMapper, IotSendData> implements IotSendDataService {

    @Autowired
    private IotSendDataMapper iotSendDataMapper;

    @Override
    public void delDateBaseIot(Integer days) {
        long time = System.currentTimeMillis() - ((long) (days * 3600 * 1000));
        Date date = new Date(time);
        QueryWrapper<IotSendData> queryWrapper = new QueryWrapper<>();
        queryWrapper.le("create_time", date);
        iotSendDataMapper.delete(queryWrapper);
    }
}
