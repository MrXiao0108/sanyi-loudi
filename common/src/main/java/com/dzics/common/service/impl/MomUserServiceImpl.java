package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.MomUserMapper;
import com.dzics.common.model.entity.MomUser;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.MomUserService;
import com.dzics.common.util.PageLimitBase;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2022-01-10
 */
@Service
public class MomUserServiceImpl extends ServiceImpl<MomUserMapper, MomUser> implements MomUserService {

    @Override
    public boolean isSaveUser(String employeeNo) {
        QueryWrapper<MomUser> wp = new QueryWrapper<>();
        wp.eq("employee_no", employeeNo);
        MomUser one = getOne(wp);
        if (one != null) {
            return false;
        }
        return true;
    }

    @Override
    public Result getMomUser(PageLimitBase limit) {
        PageHelper.startPage(limit.getPage(), limit.getLimit());
        QueryWrapper<MomUser> wp = new QueryWrapper<>();
        String type = limit.getType();
        String field = limit.getField();
        if (!StringUtils.isEmpty(type) || StringUtils.isEmpty(field)) {
            String s = type.toUpperCase();
            if ("ASC".equals(s)) {
                wp.orderByAsc(field);
            } else {
                wp.orderByDesc(field);
            }
        }
        List<MomUser> list = list(wp);
        PageInfo<MomUser> info = new PageInfo<>(list);
        return Result.ok(info.getList(), info.getTotal());
    }

    @Override
    public MomUser getEmployeeNo(String employeeNo) {
        QueryWrapper<MomUser> wp = new QueryWrapper<>();
        wp.eq("employee_no", employeeNo);
        MomUser one = getOne(wp);
        return one;
    }

    @Override
    public MomUser getLineIsLogin(String orderNo, String lineNo, String orderCodeSys) {
        QueryWrapper<MomUser> wp = new QueryWrapper<>();
        wp.eq("login_order_no", orderNo);
        wp.eq("login_line_no", lineNo);
        wp.eq("login_state", true);
        MomUser one = getOne(wp);
        if (one != null) {
            return one;
        }
        return null;
    }

    @Override
    public MomUser updateByIdCahce(MomUser momUser, String orderCodeSys) {
        updateById(momUser);
        return momUser;
    }
}
