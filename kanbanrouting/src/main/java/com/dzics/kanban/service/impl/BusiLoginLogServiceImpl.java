package com.dzics.kanban.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.kanban.enums.Message;
import com.dzics.kanban.enums.UserIdentityEnum;
import com.dzics.kanban.exception.enums.CustomExceptionType;
import com.dzics.kanban.model.entity.SysLoginLog;
import com.dzics.kanban.model.entity.SysUser;
import com.dzics.kanban.model.request.SysloginVo;
import com.dzics.kanban.model.response.Result;
import com.dzics.kanban.service.BusiLoginLogService;
import com.dzics.kanban.service.SysLoginLogService;
import com.dzics.kanban.service.SysUserServiceDao;
import com.dzics.kanban.util.DateUtil;
import com.dzics.kanban.util.PageLimit;
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
        wpOperL.orderByDesc("create_time");
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
            wpOperL.le("create_time",dateUtil.dayjiaDay(sysloginVo.getEndTime(),1));
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
