package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.LogPromptMsgMomMapper;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.entity.LogPromptMsgMom;
import com.dzics.common.model.request.mom.AgvLogParms;
import com.dzics.common.model.request.mom.BackMomLogVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.mom.BackMomLogDo;
import com.dzics.common.model.response.mom.MomLogExcelDo;
import com.dzics.common.service.LogPromptMsgMomService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2022-01-21
 */
@Service
public class LogPromptMsgMomServiceImpl extends ServiceImpl<LogPromptMsgMomMapper, LogPromptMsgMom> implements LogPromptMsgMomService {
    @Autowired
    private LogPromptMsgMomMapper logPromptMsgMomMapper;

    @Transactional(propagation = Propagation.NOT_SUPPORTED )
    @Override
    public void saveLogPromptMsgMom(LogPromptMsgMom tinvokCoreLog) {
        save(tinvokCoreLog);
    }

    @Override
    public Result getBackMomLogs(BackMomLogVo backMomLogVo) {
        PageHelper.startPage(backMomLogVo.getPage(),backMomLogVo.getLimit());
        List<BackMomLogDo> backMonLog = logPromptMsgMomMapper.getBackMonLog(backMomLogVo);
        PageInfo info = new PageInfo(backMonLog);
        return new Result(CustomExceptionType.OK,info.getList(),info.getTotal());
    }

    @Override
    public Result getMomLogsExcel(AgvLogParms logParms) {
        List<MomLogExcelDo> momLogExcel = logPromptMsgMomMapper.getMomLogExcel(logParms);
        for (MomLogExcelDo momLogExcelDo : momLogExcel) {
            if("Y".equals(momLogExcelDo.getInvokStatus())){
                momLogExcelDo.setInvokStatus("成功");
            }else if("N".equals(momLogExcelDo.getInvokStatus())){
                momLogExcelDo.setInvokStatus("失败");
            }

            String invokReturn = momLogExcelDo.getInvokReturn();
            if (!StringUtils.isEmpty(invokReturn)) {
//                找到第一个冒号截取 后边的字符串
                int index = invokReturn.indexOf(":");
                if (index > 0) {
                    invokReturn = invokReturn.substring(index + 1);
                }
                momLogExcelDo.setInvokReturn(invokReturn);
            }
            BigDecimal divide = momLogExcelDo.getInvokCost().divide(new BigDecimal(1000)).setScale(2, RoundingMode.HALF_UP);
            momLogExcelDo.setInvokCost(divide);
        }
        return Result.ok(momLogExcel);
    }

}
