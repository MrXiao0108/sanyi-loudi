package com.dzics.data.acquisition.service;


import com.dzics.common.model.custom.CmdTcp;
import com.dzics.common.model.entity.DzToolCompensationData;

import java.util.List;

public interface RedisToolInfoListService {

    /**
     * 查询所有刀具信息
     * @return
     */
    List<DzToolCompensationData> getCompensationDataList(Long eqId);

    /**
     *更新所有刀具信息
     * @param data
     * @return
     */
    List<DzToolCompensationData> updateCompensationDataList(List<DzToolCompensationData> data);
}
