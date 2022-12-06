package com.dzics.sanymom.service;

import com.dzics.common.model.response.Result;

/**
 * @author ZhangChengJun
 * Date 2021/12/30.
 * @since
 */
public interface CleanRedisCache {
    Result cleanPosition(String orderNo, String lineNo, String inIslandCode);

}
