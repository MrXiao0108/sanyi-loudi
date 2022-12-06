package com.dzics.common.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.entity.JobStatusTraceLog;
import com.dzics.common.model.request.workhistory.GetWorkStatusVo;
import com.dzics.common.model.response.workhistory.GetWorkStatusDo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author dzics
 * @since 2021-11-22
 */
public interface JobStatusTraceLogMapper extends BaseMapper<JobStatusTraceLog> {
    List<GetWorkStatusDo>getList(GetWorkStatusVo getWorkStatusVo);

}
