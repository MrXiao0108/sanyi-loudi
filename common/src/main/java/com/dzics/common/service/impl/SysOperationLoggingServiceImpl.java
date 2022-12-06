package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.SysOperationLoggingMapper;
import com.dzics.common.model.entity.SysOperationLogging;
import com.dzics.common.service.SysOperationLoggingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 操作日志 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-06
 */
@Service
@Slf4j
public class SysOperationLoggingServiceImpl extends ServiceImpl<SysOperationLoggingMapper, SysOperationLogging> implements SysOperationLoggingService {
    @Autowired
    private SysOperationLoggingMapper loggingMapper;



    @Override
    public void delOperationLog(int i) {
        long time = System.currentTimeMillis() - ((long)i * (24 * 3600 * 1000));
        Date date = new Date(time);
        QueryWrapper<SysOperationLogging> wrapper = new QueryWrapper<>();
        wrapper.le("oper_time", date);
        remove(wrapper);
    }

}
