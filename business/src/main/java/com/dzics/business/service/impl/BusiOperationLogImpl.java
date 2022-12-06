package com.dzics.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.business.service.BusiOperationLog;
import com.dzics.common.enums.Message;
import com.dzics.common.enums.UserIdentityEnum;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.entity.SysOperationLogging;
import com.dzics.common.model.entity.SysUser;
import com.dzics.common.model.request.SysOperationLoggingVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.SysOperationLoggingService;
import com.dzics.common.service.SysUserServiceDao;
import com.dzics.common.util.PageLimit;
import com.dzics.common.util.UnderlineTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ZhangChengJun
 * Date 2021/1/15.
 * @since
 */
@Slf4j
@Service
public class BusiOperationLogImpl implements BusiOperationLog {
    @Autowired
    private SysOperationLoggingService operationLoggingService;
    @Autowired
    private SysUserServiceDao sysUserServiceDao;
    @Override
    public Result<SysOperationLogging> queryOperLog(PageLimit pageLimit, String sub, String code, SysOperationLoggingVo sysOperationLoggingVo) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode()) {
            code = null;
        }
        PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        QueryWrapper<SysOperationLogging> wpOperL = new QueryWrapper<>();
        wpOperL.select("id");
        wpOperL.orderByDesc("id");
        if (StringUtils.isNotEmpty(sysOperationLoggingVo.getTitle())) {
            wpOperL.likeRight("title", sysOperationLoggingVo.getTitle());
        }
        if (StringUtils.isNotEmpty(sysOperationLoggingVo.getOperDesc())) {
            wpOperL.likeRight("oper_desc", sysOperationLoggingVo.getOperDesc());
        }
        if (sysOperationLoggingVo.getStatus() != null && (sysOperationLoggingVo.getStatus() == 0 || sysOperationLoggingVo.getStatus() == 1)) {
            wpOperL.eq("status", sysOperationLoggingVo.getStatus());
        }
        if (StringUtils.isNotEmpty(code)) {
            wpOperL.eq("org_code", code);
        }
        if(sysOperationLoggingVo.getStartTime()!=null){
            wpOperL.ge("oper_date",sysOperationLoggingVo.getStartTime());
        }
        if(sysOperationLoggingVo.getEndTime()!=null){
            wpOperL.le("oper_date",sysOperationLoggingVo.getEndTime());
        }
        if(!StringUtils.isEmpty(pageLimit.getType())){
            if("DESC".equals(pageLimit.getType())){
                wpOperL.orderByDesc(UnderlineTool.humpToLine(pageLimit.getField()));
            } else if("ASC".equals(pageLimit.getType())){
                wpOperL.orderByAsc(UnderlineTool.humpToLine(pageLimit.getField()));
            }
        }
        List<SysOperationLogging> list = operationLoggingService.list(wpOperL);
        PageInfo<SysOperationLogging> sysOperationLoggingPageInfo = new PageInfo<>(list);
        if (sysOperationLoggingPageInfo.getList().isEmpty()) {
            return new Result(CustomExceptionType.OK, sysOperationLoggingPageInfo.getList(), sysOperationLoggingPageInfo.getTotal());
        }
//       再次查询
        List<Long> collect = sysOperationLoggingPageInfo.getList().stream().map(dd -> dd.getId()).collect(Collectors.toList());
        QueryWrapper<SysOperationLogging> loggingQueryWrapper = new QueryWrapper<>();
        loggingQueryWrapper.in("id", collect);
        if(!StringUtils.isEmpty(pageLimit.getType())){
            if("DESC".equals(pageLimit.getType())){
                loggingQueryWrapper.orderByDesc(UnderlineTool.humpToLine(pageLimit.getField()));
            } else if("ASC".equals(pageLimit.getType())){
                loggingQueryWrapper.orderByAsc(UnderlineTool.humpToLine(pageLimit.getField()));
            }else{
                loggingQueryWrapper.orderByDesc("id");
            }
        }else{
            loggingQueryWrapper.orderByDesc("id");
        }

        List<SysOperationLogging> list1 = operationLoggingService.list(loggingQueryWrapper);
        return new Result(CustomExceptionType.OK, list1, sysOperationLoggingPageInfo.getTotal());
    }

    @Override
    public Result delOperLog(List<Integer> ids) {
        if (ids == null || ids.size() == 0) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_5);
        }
        operationLoggingService.removeByIds(ids);
        return new Result(CustomExceptionType.OK, Message.OK_2);
    }

   /* @Override
    public Msg<SysOperationLogging> queryOperLog(PageLimit pageLimit, String sub, SysOperationLoggingVo sysOperationLoggingVo) {
//        总数量
        int count = operationLoggingService.count();
        if (count == 0) {
            return new Msg(CustomExceptionType.OK, null, count);
        }
//        分页总数
        int pageTotal = (count + pageLimit.getLimit() - 1) / pageLimit.getLimit();
//
        int stPage = pageTotal - (pageLimit.getPage()-1);
//        当前分页开始行
        int startLimit = stPage * pageLimit.getLimit() - pageLimit.getLimit();

        List<SysOperationLogging> list = operationLoggingService.queryOperLog(startLimit, pageLimit.getLimit());
        return new Msg(CustomExceptionType.OK, list, count);
    }
*/

}
