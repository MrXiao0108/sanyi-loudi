package com.dzics.business.service;

import com.dzics.common.model.dto.check.LogPromptMsgDto;
import com.dzics.common.model.request.mom.AgvLogParms;

import java.util.List;

/**
 * @author xnb
 * @date 2022/10/9 0009 10:42
 */
public interface BusLogPromptMsgService {
    /**
     * Mom日志查询
     *
     * @param orderNo
     * @param createDate
     * @param wipOrderNo
     * @param pointCode
     * @param brief
     * @param beginTime
     * @param endTime
     * @return List<LogPromptMsgDto>
     */
    List<LogPromptMsgDto>getMomLogs(String orderNo,String createDate,String wipOrderNo,String pointCode,String brief,String beginTime,String endTime,String filed,String type);
}
