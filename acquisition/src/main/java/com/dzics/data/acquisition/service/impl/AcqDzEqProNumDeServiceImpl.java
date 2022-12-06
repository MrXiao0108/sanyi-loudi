package com.dzics.data.acquisition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.model.custom.UpValueDevice;
import com.dzics.common.model.entity.DzEquipmentProNumDetails;
import com.dzics.common.service.DzEquipmentProNumDetailsService;
import com.dzics.data.acquisition.service.AccqDzEqProNumDetailsService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/1/19.
 * @since
 */
@Service
public class AcqDzEqProNumDeServiceImpl implements AccqDzEqProNumDetailsService {
    @Autowired
    private DzEquipmentProNumDetailsService detailsService;

    @Override
    public DzEquipmentProNumDetails getUpSave(String de, String pr, String deviceType) {
        PageHelper.startPage(1, 1);
        QueryWrapper<DzEquipmentProNumDetails> wp = new QueryWrapper<>();
        wp.eq("equipment_no", de);
        wp.eq("line_no", pr);
        wp.eq("device_type", deviceType);
        wp.orderByDesc("id");
        List<DzEquipmentProNumDetails> list = detailsService.list(wp);
        PageInfo<DzEquipmentProNumDetails> info = new PageInfo<>(list);
        if (CollectionUtils.isNotEmpty(info.getList())) {
            return info.getList().get(0);
        }
        return null;
    }

    @Override
    public void saveDataDetails(DzEquipmentProNumDetails details) {
        details.setCreateTime(new Date());
        detailsService.save(details);
    }

    @Override
    public UpValueDevice getupsavenumlinenuty(String lineNum, String deviceNum, String deviceType, String orderNumber) {
        return detailsService.getupsaveddnumlinnuty(lineNum, deviceNum, deviceType,orderNumber);
    }
}
