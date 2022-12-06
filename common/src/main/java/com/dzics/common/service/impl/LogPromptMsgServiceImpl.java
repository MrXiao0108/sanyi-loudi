package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.LogPromptMsgMapper;
import com.dzics.common.model.entity.LogPromptMsg;
import com.dzics.common.service.LogPromptMsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2022-01-12
 */
@Service
@Slf4j
public class LogPromptMsgServiceImpl extends ServiceImpl<LogPromptMsgMapper, LogPromptMsg> implements LogPromptMsgService {

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Override
    public void saveLogPromptMsg(LogPromptMsg tinvokCoreLog) {
        save(tinvokCoreLog);
    }

    @Override
    public LogPromptMsg getBtGroupId(String groupId) {
        QueryWrapper<LogPromptMsg> wp = new QueryWrapper<>();
        wp.eq("group_id", groupId);
        LogPromptMsg one = getOne(wp);
        return one;
    }
}
