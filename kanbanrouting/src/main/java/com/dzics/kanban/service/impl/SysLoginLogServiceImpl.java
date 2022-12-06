package com.dzics.kanban.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.kanban.dao.SysLoginLogMapper;
import com.dzics.kanban.model.entity.SysLoginLog;
import com.dzics.kanban.service.SysLoginLogService;
import org.springframework.stereotype.Service;

import java.util.Date;


/**
 * <p>
 * 登陆日志 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-06
 */
@Service
public class SysLoginLogServiceImpl extends ServiceImpl<SysLoginLogMapper, SysLoginLog> implements SysLoginLogService {

    @Override
    public void delLoginLog(int i) {
        long time = System.currentTimeMillis() - (i * (24 * 3600 * 1000));
        Date date = new Date(time);
        QueryWrapper<SysLoginLog> wrapper = new QueryWrapper<>();
        wrapper.le("create_time", date);
        remove(wrapper);
    }
}
