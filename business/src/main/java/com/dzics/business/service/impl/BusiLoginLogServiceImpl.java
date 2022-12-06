package com.dzics.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.business.service.BusiLoginLogService;
import com.dzics.common.enums.Message;
import com.dzics.common.enums.UserIdentityEnum;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.entity.SysLoginLog;
import com.dzics.common.model.entity.SysUser;
import com.dzics.common.model.request.SysloginVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.SysLoginLogService;
import com.dzics.common.service.SysUserServiceDao;
import com.dzics.common.util.DateUtil;
import com.dzics.common.util.PageLimit;
import com.dzics.common.util.UnderlineTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/1/18.
 * @since
 */
@Service
public class BusiLoginLogServiceImpl implements BusiLoginLogService {
    @Autowired
    private SysLoginLogService sysLoginLogService;
    @Autowired
    private SysUserServiceDao sysUserServiceDao;
    @Autowired
    DateUtil dateUtil;
    /**
     * 登录日志查询
     *
     * @param pageLimit
     * @param sysloginVo
     * @param sub
     * @param code
     * @return
     */
    @Override
    public Result<SysLoginLog> queryLogin(PageLimit pageLimit, SysloginVo sysloginVo, String sub, String code) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode()) {
            code = null;
        }
        PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        QueryWrapper<SysLoginLog> wpOperL = new QueryWrapper<>();

        if (!StringUtils.isEmpty(sysloginVo.getUserName())) {
            wpOperL.likeRight("user_name", sysloginVo.getUserName());
        }
        if (!StringUtils.isEmpty(sysloginVo.getLoginStatus())) {
            wpOperL.eq("login_status", sysloginVo.getLoginStatus());
        }
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(code)) {
            wpOperL.eq("org_code", code);
        }
        if(sysloginVo.getStartTime()!=null){
            wpOperL.ge("create_time",sysloginVo.getStartTime());
        }
        if(sysloginVo.getEndTime()!=null){
            wpOperL.le("create_time",dateUtil.dayjiaday(sysloginVo.getEndTime(),1));
        }
        if(!StringUtils.isEmpty(pageLimit.getType())){
            if("DESC".equals(pageLimit.getType())){
                wpOperL.orderByDesc(UnderlineTool.humpToLine(pageLimit.getField()));
            } else if("ASC".equals(pageLimit.getType())){
                wpOperL.orderByAsc(UnderlineTool.humpToLine(pageLimit.getField()));
            }
        }else{
            wpOperL.orderByDesc("create_time");
        }
        List<SysLoginLog> list = sysLoginLogService.list(wpOperL);
        PageInfo<SysLoginLog> sysLoginLogPageInfo = new PageInfo<>(list);
        return new Result(CustomExceptionType.OK, sysLoginLogPageInfo.getList(), sysLoginLogPageInfo.getTotal());
    }

    @Override
    public Result delLoginLog(List<Integer> ids) {
        if (ids == null || ids.size() == 0) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_5);
        }
        sysLoginLogService.removeByIds(ids);
        return new Result(CustomExceptionType.OK, Message.OK_2);
    }
}
