package com.dzics.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.common.model.entity.LogPromptMsgMom;
import com.dzics.common.model.request.mom.AgvLogParms;
import com.dzics.common.model.request.mom.BackMomLogVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.mom.MomLogExcelDo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2022-01-21
 */
public interface LogPromptMsgMomService extends IService<LogPromptMsgMom> {

    void saveLogPromptMsgMom(LogPromptMsgMom tinvokCoreLog);

    Result getBackMomLogs(BackMomLogVo backMomLogVo);

    Result<List<MomLogExcelDo>>getMomLogsExcel(AgvLogParms logParms);
}
