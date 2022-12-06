package com.dzics.business.service.impl;

import com.dzics.common.model.dto.check.LogPromptMsgDto;
import com.dzics.business.service.BusLogPromptMsgService;
import com.dzics.common.dao.LogPromptMsgMomMapper;
import com.dzics.common.model.request.mom.AgvLogParms;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xnb
 * @date 2022/10/9 0009 10:42
 */
@Service
@Slf4j
public class BusLogPromptMsgServiceImpl implements BusLogPromptMsgService {
    @Autowired
    private LogPromptMsgMomMapper logPromptMsgMomMapper;


    @Override
    public List<LogPromptMsgDto> getMomLogs(String orderNo, String createDate, String wipOrderNo, String pointCode, String brief, String beginTime, String endTime,String filed,String type) {
        return logPromptMsgMomMapper.getMomLogs(orderNo, createDate, wipOrderNo, pointCode, brief, beginTime, endTime,filed,type);
    }
}
