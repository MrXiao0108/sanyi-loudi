package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.DzDataCollectionMapper;
import com.dzics.common.model.entity.DzDataCollection;
import com.dzics.common.model.response.timeanalysis.TimeAnalysisCmd;
import com.dzics.common.service.DzDataCollectionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-07-29
 */
@Service
public class DzDataCollectionServiceImpl extends ServiceImpl<DzDataCollectionMapper, DzDataCollection> implements DzDataCollectionService {

    @Override
    public List<TimeAnalysisCmd> getDeviceTypeCmdSingal(String shardingParameter) {
        return baseMapper.getDeviceTypeCmdSingal(shardingParameter);
    }

    @Override
    public TimeAnalysisCmd getDeviceId(Long deviceId) {
        return baseMapper.getDeviceId(deviceId);
    }


}
