package com.dzics.sanymom.service.impl;

import com.dzics.common.model.response.Result;
import com.dzics.common.util.RedisKey;
import com.dzics.sanymom.service.CleanRedisCache;
import com.dzics.sanymom.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ZhangChengJun
 * Date 2021/12/30.
 * @since
 */
@Service
@Slf4j
public class CleanRedisCacheImpl implements CleanRedisCache {
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public Result cleanPosition(String orderNo, String lineNo, String inIslandCode) {
        redisUtil.del(RedisKey.Rob_Call_Material + orderNo + lineNo + inIslandCode);
        return Result.ok();
    }
}
