package com.dzics.data.acquisition.service;

import com.dzics.common.model.response.LineDayAndSumDataDo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.feishi.DayDataDo;
import com.dzics.common.model.response.homepage.QualifiedAndOutputDo;

public interface HomeLineDataService {
    /**
     * 日生产综合报表
     * @return
     */
    DayDataDo dayData(Long lineId);
    /**
     * 月生产综合报表
     * @return
     */
    DayDataDo monthData(Long lineId);

    /**
     * 日产出率
     * @param lineId
     * @return
     */
    QualifiedAndOutputDo outputCapacity(Long lineId);

    /**
     * 日合格率
     * @param lineId
     * @return
     */
    QualifiedAndOutputDo percentOfPass(Long lineId);



}
