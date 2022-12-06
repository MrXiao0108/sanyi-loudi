package com.dzics.business.service;

import com.dzics.common.model.request.mom.GetWorkingDetailsVo;
import com.dzics.common.model.response.Result;

public interface MomDzWorkingFlowService {
    Result getWorkingDetails(GetWorkingDetailsVo getWorkingDetailsVo);
}
