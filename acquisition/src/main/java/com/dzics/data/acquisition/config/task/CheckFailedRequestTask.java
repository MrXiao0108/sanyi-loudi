package com.dzics.data.acquisition.config.task;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.model.entity.DzWaitCheckRes;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.DzWaitCheckResService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @Classname CheckFailedRequestTask
 * @Description 请求到单岛上的
 * 报工请求
 * 检测结果请求
 * 失败后重新触发的任务
 * @Date 2022/5/30 10:33
 * @Created by NeverEnd
 */
@Slf4j
@Service
public class CheckFailedRequestTask implements SimpleJob {
    @Autowired
    private DzWaitCheckResService waitCheckResService;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void execute(ShardingContext shardingContext) {
        try {
            PageHelper.startPage(1, 20);
            List<DzWaitCheckRes> list = waitCheckResService.list();
            PageInfo<DzWaitCheckRes> info = new PageInfo<>(list);
            List<DzWaitCheckRes> waitCheckRes = info.getList();
            if (CollectionUtils.isNotEmpty(waitCheckRes)) {
                for (DzWaitCheckRes waitCheckRe : waitCheckRes) {
                    ResponseEntity<Result> response = restTemplate.postForEntity(waitCheckRe.getUrl(), waitCheckRe.getReqParms(), Result.class);
                    Result body = response.getBody();
                    Integer code = body.getCode();
                    if (0 == code) {
                        waitCheckResService.removeById(waitCheckRe.getId());
                    }
                }
            }
        }catch (Throwable throwable){
            log.error("重启触发请求到单岛失败：{}",throwable.getMessage());
        }

    }
}
