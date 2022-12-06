package com.dzics.business.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.business.service.JobStatusTraceLogService;
import com.dzics.common.dao.JobStatusTraceLogMapper;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.entity.JobStatusTraceLog;
import com.dzics.common.model.request.workhistory.GetWorkStatusVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.workhistory.GetWorkStatusDo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dzics
 * @since 2021-11-22
 */
@Service
public class JobStatusTraceLogServiceImpl extends ServiceImpl<JobStatusTraceLogMapper, JobStatusTraceLog> implements JobStatusTraceLogService {
    @Autowired
    private JobStatusTraceLogMapper jobStatusTraceLogMapper;

    @Override
    public Result<List<GetWorkStatusDo>> getList(GetWorkStatusVo getWorkStatusVo) {
        if (getWorkStatusVo.getPage() != -1){
            PageHelper.startPage(getWorkStatusVo.getPage(), getWorkStatusVo.getLimit());
        }
        List <GetWorkStatusDo> list = jobStatusTraceLogMapper.getList(getWorkStatusVo);
        PageInfo pageInfo = new PageInfo(list);
        return new Result(CustomExceptionType.OK,pageInfo.getList(),pageInfo.getTotal());
    }
}
