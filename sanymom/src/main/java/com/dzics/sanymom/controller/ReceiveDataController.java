package com.dzics.sanymom.controller;

import com.dzics.common.model.entity.MomMaterialPoint;
import com.dzics.common.model.response.Result;
import com.dzics.sanymom.service.CleanRedisCache;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 接收消费端数据接口
 *
 * @author ZhangChengJun
 * Date 2021/12/14.
 * @since
 */
@Api(tags = {"接收数据"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RequestMapping("/api/receive/data")
@RestController
@Slf4j
public class ReceiveDataController {
    @Autowired
    private CleanRedisCache cleanRedisCache;

    @ApiOperation(value = "清除料点信息缓存")
    @PostMapping("/get/position")
    public Result getPosition(@RequestBody MomMaterialPoint feedingVo) {
        return cleanRedisCache.cleanPosition(feedingVo.getOrderNo(), feedingVo.getLineNo(), feedingVo.getInIslandCode());
    }

}
