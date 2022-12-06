package com.dzics.business.service.impl;

import com.dzics.business.service.JobExecutionLogService;
import com.dzics.common.dao.JobExecutionLogMapper;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.entity.DzJobExecutionLog;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.model.request.workhistory.GetWorkVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.workhistory.GetWorkDo;
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
public class JobExecutionLogServiceImpl extends ServiceImpl<JobExecutionLogMapper, DzJobExecutionLog> implements JobExecutionLogService {

    @Autowired
    private JobExecutionLogMapper jobExecutionLogMapper;

    @Override
    public Result<List<GetWorkDo>> getList(GetWorkVo getWorkVo) {
        if(getWorkVo.getPage() != -1){
            PageHelper.startPage(getWorkVo.getPage(), getWorkVo.getLimit());
        }
        List<GetWorkDo> getWorkDos = jobExecutionLogMapper.geiList(getWorkVo);
        PageInfo pageInfo = new PageInfo(getWorkDos);
        return new Result(CustomExceptionType.OK,pageInfo.getList(),pageInfo.getTotal());

    }
}
