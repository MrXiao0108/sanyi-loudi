package com.dzics.business.service;

import com.dzics.common.model.request.devicecheck.CheckUpVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.util.PageLimit;

public interface CheckUpItemService {

    Result add(String sub, CheckUpVo checkUpVo);

    Result list(PageLimit pageLimit, Integer deviceType, String checkName);

    Result del(String checkItemId);

    Result put(String sub, CheckUpVo checkUpVo);
}
