package com.dzics.kanban.framework.initconfig;

import com.dzics.kanban.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 系统运行模式初始化
 *
 * @author ZhangChengJun
 * Date 2021/5/17.
 * @since
 */
@Component
public class SystemRunConfig {

    @Autowired
    private RedisUtil redisUtil;

    @PostConstruct
    public void initSysTemConfig() {
        redisUtil.del("sysUserService.getSystemRunConfig::runModel");
    }
}
