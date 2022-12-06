package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.DzDetectorDataMapper;
import com.dzics.common.model.entity.DzDetectorData;
import com.dzics.common.service.DzDetectorDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 产品检测设置默认模板 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-04
 */
@SuppressWarnings("ALL")
@Service
public class DzDetectorDataServiceImpl extends ServiceImpl<DzDetectorDataMapper, DzDetectorData> implements DzDetectorDataService {

    @Autowired
    private DzDetectorDataMapper dzDetectorDataMapper;

    /**
     * 分组后的每次检测数据 key
     *
     * @param productNo       产品id
     * @param detectionResult
     * @param startTime
     * @param endTime
     * @param orgCode
     * @return
     */
    @Override
    public List<DzDetectorData> groupBuby(String productNo, Integer detectionResult, Date startTime, Date endTime, String orgCode) {
        List<DzDetectorData> templates = dzDetectorDataMapper.groupBuby(productNo, detectionResult, startTime, endTime, orgCode);
        return templates;
    }

    @Override
    public List<DzDetectorData> getByOrderNoProNo(String groupKey) {
        return null;
    }

    /**
     * 分组后的每次检测数据 key
     *
     * @return
     */
    @Override
    public List<DzDetectorData> groupBubyData() {
        List<DzDetectorData> templates = dzDetectorDataMapper.groupBubyData();
        return templates;
    }

    @Override
    public List<Map<String, Object>> getGroupKey(List<String> groupKey) {
        return dzDetectorDataMapper.getGroupKey(groupKey);
    }
}
