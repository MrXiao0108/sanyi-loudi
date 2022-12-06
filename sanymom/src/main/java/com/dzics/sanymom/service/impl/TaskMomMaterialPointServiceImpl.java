package com.dzics.sanymom.service.impl;

import com.dzics.common.dao.MomMaterialPointMapper;
import com.dzics.common.model.agv.MomUpPoint;
import com.dzics.common.service.MomMaterialPointService;
import com.dzics.sanymom.service.TaskMomMaterialPointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ZhangChengJun
 * Date 2021/11/5.
 * @since
 */
@Slf4j
@Service
public class TaskMomMaterialPointServiceImpl implements TaskMomMaterialPointService {
    @Autowired
    private MomMaterialPointMapper momMaterialPointMapper;

    @Override
    public MomUpPoint getStationCode(String basketType, String orderCode, String lineNo) {
        return momMaterialPointMapper.getStationCode(basketType, orderCode, lineNo);
    }
}
