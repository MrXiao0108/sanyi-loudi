package com.dzics.sanymom.service;

import com.dzics.common.model.entity.MomUser;
import com.dzics.common.model.request.mom.MomUserLogin;
import com.dzics.common.model.request.mom.OperationOrderVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.mom.MaterialPointStatus;
import com.dzics.common.model.response.mom.MomAuthOrderRes;
import com.dzics.common.util.PageLimitBase;
import com.dzics.sanymom.model.request.OpenWork;

/**
 * @author ZhangChengJun
 * Date 2022/1/10.
 * @since
 */
public interface MomUserMessage {

    Result login(MomUserLogin login);

    Result getUseLineMsg();

    Result getUserMessage();

    Result<MomUser> getUseLineIslogin();

    Result offNoOpenWork();

    Result offNoOpen(OpenWork openWork);

    Result getMomAuthOrderRes(PageLimitBase pageLimitBase);

    Result getMaterialPointStatus();

    Result operationOrder(OperationOrderVo operationOrderVo);

}
