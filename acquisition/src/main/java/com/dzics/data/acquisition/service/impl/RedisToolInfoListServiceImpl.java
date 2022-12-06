package com.dzics.data.acquisition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.common.dao.DzToolCompensationDataMapper;
import com.dzics.common.model.custom.CmdTcp;
import com.dzics.common.model.entity.DzToolCompensationData;
import com.dzics.common.service.DzToolCompensationDataService;
import com.dzics.data.acquisition.service.RedisToolInfoListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RedisToolInfoListServiceImpl implements RedisToolInfoListService {

    @Autowired
    DzToolCompensationDataMapper dzToolCompensationDataMapper;
    @Autowired
    DzToolCompensationDataService dzToolCompensationDataService;

    @Override
    public List<DzToolCompensationData> getCompensationDataList(Long eqId) {
        List<DzToolCompensationData> dataList = dzToolCompensationDataMapper.selectList(new QueryWrapper<DzToolCompensationData>().eq("equipment_id",eqId));
        return dataList;
    }

    @Override
    public List<DzToolCompensationData> updateCompensationDataList(List<DzToolCompensationData> data) {
        boolean b = dzToolCompensationDataService.updateBatchById(data);
        if(b){
            return data;
        }
        return null;
    }
}
