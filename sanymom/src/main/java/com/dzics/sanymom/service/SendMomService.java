package com.dzics.sanymom.service;

import com.dzics.common.model.entity.MomOrderCompleted;

public interface SendMomService {
    /**
     * 质量参数上传到MOM
     *
     * @param staCode
     * @return
     */
    boolean uploadCheckData(MomOrderCompleted staCode);
}
