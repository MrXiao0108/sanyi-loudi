package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.DzEquipmentProNumDetailsMapper;
import com.dzics.common.model.custom.UpValueDevice;
import com.dzics.common.model.entity.DzEquipmentProNumDetails;
import com.dzics.common.service.DzEquipmentProNumDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;

/**
 * <p>
 * 设备生产数量详情表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
@Service
public class DzEquipmentProNumDetailsServiceImpl extends ServiceImpl<DzEquipmentProNumDetailsMapper, DzEquipmentProNumDetails> implements DzEquipmentProNumDetailsService {

    @Autowired
    private DzEquipmentProNumDetailsMapper detailsMapper;

    @Override
    public UpValueDevice getupsaveddnumlinnuty(String lineNum, String deviceNum, String deviceType, String orderNumber) {
        return detailsMapper.getupsaveddnumlinnuty(lineNum, deviceNum, deviceType, orderNumber);
    }

    @Override
    public void delProNumDetails(int days) {
        long time = System.currentTimeMillis() - ((long) days * (24 * 3600 * 1000));
        Date date = new Date(time);
        QueryWrapper<DzEquipmentProNumDetails> wp = new QueryWrapper<>();
        wp.lt("create_time", date);
        detailsMapper.delete(wp);
    }
}
