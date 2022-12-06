package com.dzics.business.service;

import com.dzics.common.model.request.mom.AddFeedingVo;
import com.dzics.common.model.request.mom.UpdateFeedingVo;
import com.dzics.common.model.response.Result;

/**
 * @author ZhangChengJun
 * Date 2021/12/30.
 * @since
 */
public interface BusMomMaterialPointService {
    /**
     * 新增AGV投料点
     *
     * @return
     */
    Result addFeedingPoint(String sub, AddFeedingVo addFeedingVo);


    /**
     * 修改AGV投料点
     *
     * @return
     */
    Result putFeedingPoint(String sub, UpdateFeedingVo addFeedingVo);

}
