package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.DzEquipmentProNumDetailsSignalMapper;
import com.dzics.common.model.custom.UpValueDeviceSignal;
import com.dzics.common.model.entity.DzEquipmentProNumDetailsSignal;
import com.dzics.common.service.DzEquipmentProNumDetailsSignalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 设备生产数量详情表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-23
 */
@Service
public class DzEquipmentProNumDetailsSignalServiceImpl extends ServiceImpl<DzEquipmentProNumDetailsSignalMapper, DzEquipmentProNumDetailsSignal> implements DzEquipmentProNumDetailsSignalService {
    @Autowired
    private DzEquipmentProNumDetailsSignalMapper detailsSignalMapper;

    @Override
    public UpValueDeviceSignal getupsaveddnumlinnuty(String lineNum, String deviceNum, String deviceType, String orderNumber, Long dayId) {
        return detailsSignalMapper.getupsaveddnumlinnuty(lineNum, deviceNum, deviceType, orderNumber,dayId);
    }
}
