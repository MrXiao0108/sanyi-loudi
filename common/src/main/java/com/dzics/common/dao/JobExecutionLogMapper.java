package com.dzics.common.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.entity.DzJobExecutionLog;
import com.dzics.common.model.request.workhistory.GetWorkVo;
import com.dzics.common.model.response.workhistory.GetWorkDo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author dzics
 * @since 2021-11-22
 */
public interface JobExecutionLogMapper extends BaseMapper<DzJobExecutionLog> {
    List<GetWorkDo>geiList(GetWorkVo getWorkVo);
}
