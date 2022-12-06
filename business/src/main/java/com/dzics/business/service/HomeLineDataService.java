package com.dzics.business.service;

import com.dzics.common.model.response.feishi.DayDataDo;
import com.dzics.common.model.response.homepage.QualifiedAndOutputDo;
import org.apache.ibatis.annotations.Select;

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
