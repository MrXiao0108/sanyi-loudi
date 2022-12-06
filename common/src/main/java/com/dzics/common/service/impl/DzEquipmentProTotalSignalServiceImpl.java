package com.dzics.common.service.impl;

import com.dzics.common.model.statistics.MakeQuantity;
import com.dzics.common.model.entity.DzEquipmentProTotalSignal;
import com.dzics.common.dao.DzEquipmentProTotalSignalMapper;
import com.dzics.common.service.DzEquipmentProTotalSignalService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 设备总生产数量 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2022-06-22
 */
@Service
public class DzEquipmentProTotalSignalServiceImpl extends ServiceImpl<DzEquipmentProTotalSignalMapper, DzEquipmentProTotalSignal> implements DzEquipmentProTotalSignalService {

    @Override
    public List<MakeQuantity> sumNumber(String table, String localData) {
        return this.baseMapper.sumNumber(table,localData);
    }
}
