package com.dzics.business.service;

import com.dzics.common.model.request.SelectTrendChartVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.cpk.AnalysisDataVO;
import com.dzics.common.model.response.down.ExpCpkAll;

public interface ProductTrendChartService {
    /**
     * 根据产品id和产品配置表id查询产品配置数据趋势图
     * @param sub
     * @param selectTrendChartVo
     * @return
     */
    Result<AnalysisDataVO> list(String sub, SelectTrendChartVo selectTrendChartVo);

    /**
     * 根据产品id(序号)查询 检测项列表
     * @param productId
     * @return
     */
    Result getByProductId(String productId);

    ExpCpkAll getExpCpkDataAll(AnalysisDataVO data);

}
