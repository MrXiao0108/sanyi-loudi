package com.dzics.sanymom.service;

import com.dzics.common.model.agv.EmptyFrameMovesDzdc;
import com.dzics.common.model.entity.MonOrder;
import com.dzics.common.model.response.Result;

/**
 * @Classname LogisticsService
 * @Description 物流配送
 * @Date 2022/5/6 14:01
 * @Created by NeverEnd
 */
public interface LogisticsService {
    Result moveAgv(EmptyFrameMovesDzdc frameMoves, MonOrder momOrder);
}
