package com.dzics.sanymom.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.common.model.entity.MomDistributionWaitRequest;
import com.dzics.common.model.entity.MomProgressFeedback;
import com.dzics.common.service.MomDistributionWaitRequestService;
import com.dzics.common.service.MomProgressFeedbackService;
import com.dzics.sanymom.service.MomHttpRequestService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 查询带报工记录 重新报工任务
 *
 * @author ZhangChengJun
 * Date 2021/6/16.
 */
@Component
@Slf4j
public class ReportWorkTask {
    @Autowired
    private MomHttpRequestService momHttpRequestService;

    @Autowired
    private MomProgressFeedbackService feedbackService;
    @Autowired
    private MomDistributionWaitRequestService distributionWaitRequestService;


    /**
     * 根据待报工记录重新报工  每7s检查报工一次
     */
//    @Transactional(rollbackFor = Throwable.class)
//    @Scheduled(cron = "0/7 * * * * ?")
    public void reportWorkMom() {
        PageHelper.startPage(1, 10);
        QueryWrapper<MomProgressFeedback> wp = new QueryWrapper<>();
        wp.orderByDesc("create_time");
        List<MomProgressFeedback> list = feedbackService.list(wp);
        PageInfo<MomProgressFeedback> momProgressFeedbackPageInfo = new PageInfo<>(list);
        List<MomProgressFeedback> infoList = momProgressFeedbackPageInfo.getList();
        List<String> ids = momHttpRequestService.reportWorkMom(infoList);
        boolean b = feedbackService.removeByIds(ids);
    }

    /**
     * 工序间配送 重新执行  工序间配送等待请求
     */
//    @Scheduled(cron = "0/2 * * * * ?")
    public void interProcessDistribution() {
        PageHelper.startPage(1, 2);
        QueryWrapper<MomDistributionWaitRequest> wp = new QueryWrapper<>();
        List<MomDistributionWaitRequest> requests = distributionWaitRequestService.list(wp);
        PageInfo<MomDistributionWaitRequest> pageInfo = new PageInfo<>(requests);
        List<MomDistributionWaitRequest> list = pageInfo.getList();
        List<String> reIds = momHttpRequestService.interProcessDistribution(list);
        boolean b = distributionWaitRequestService.removeByIds(reIds);
    }
}
