package com.dzics.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.common.model.entity.JobStatusTraceLog;
import com.dzics.common.model.request.workhistory.GetWorkStatusVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.workhistory.GetWorkStatusDo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dzics
 * @since 2021-11-22
 */
public interface JobStatusTraceLogService extends IService<JobStatusTraceLog> {
        Result<List<GetWorkStatusDo>>getList(GetWorkStatusVo getWorkStatusVo);
}
