package com.dzics.sanymom.service;

import com.dzics.common.model.custom.WorkReportDto;
import com.dzics.common.model.entity.MomOrderCompleted;

/**
 * @Classname WorkReportService
 * @Description 报工接口
 * @Date 2022/5/20 17:28
 * @Created by NeverEnd
 */
public interface WorkReportService {
    /**
     * 报工
     *
     * @param workReportDto
     * @return
     */

    MomOrderCompleted sendWorkReport(WorkReportDto workReportDto);
}
