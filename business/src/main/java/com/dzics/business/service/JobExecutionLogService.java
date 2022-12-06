package com.dzics.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.common.model.entity.DzJobExecutionLog;
import com.dzics.common.model.request.workhistory.GetWorkVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.workhistory.GetWorkDo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dzics
 * @since 2021-11-22
 */
public interface JobExecutionLogService extends IService<DzJobExecutionLog> {
    Result<List<GetWorkDo>>getList(GetWorkVo getWorkVo);
}
