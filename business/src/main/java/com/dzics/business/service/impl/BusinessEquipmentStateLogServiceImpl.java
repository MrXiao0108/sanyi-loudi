package com.dzics.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.business.service.BusinessEquipmentStateLogService;
import com.dzics.common.dao.DzEquipmentStateLogMapper;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.entity.DzEquipmentStateLog;
import com.dzics.common.model.request.SelectEquipmentStateVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.SysOperationLoggingService;
import com.dzics.common.service.SysUserServiceDao;
import com.dzics.common.util.DateUtil;
import com.dzics.common.util.PageLimit;
import com.dzics.common.util.UnderlineTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class BusinessEquipmentStateLogServiceImpl extends ServiceImpl<DzEquipmentStateLogMapper,DzEquipmentStateLog> implements BusinessEquipmentStateLogService {

    @Autowired
    DzEquipmentStateLogMapper dzEquipmentStateLogMapper;
    @Autowired
    SysUserServiceDao sysUserServiceDao;
    @Autowired
    DateUtil dateUtil;
    @Autowired
    SysOperationLoggingService sysOperationLoggingService;
    @Override
    public Result list(String sub, PageLimit pageLimit, SelectEquipmentStateVo stateVo) {

        String userOrgCode = sysUserServiceDao.getUserOrgCode(sub);
        QueryWrapper<DzEquipmentStateLog> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no",stateVo.getOrderNo());
        wrapper.eq("line_no",stateVo.getLineNo());
        if(stateVo.getStartTime()!=null){
            wrapper.ge("create_time",stateVo.getStartTime());
        }
        if(stateVo.getEndTime()!=null){
            wrapper.le("create_time",dateUtil.dayjiaday(stateVo.getEndTime(),1));

        }
        if(!StringUtils.isEmpty(stateVo.getEquipmentNo())){
            wrapper.like("equipment_no",stateVo.getEquipmentNo());
        }
        if(!StringUtils.isEmpty(stateVo.getEquipmentType())){
            wrapper.eq("equipment_type",stateVo.getEquipmentType());
        }
        if(userOrgCode!=null){
            wrapper.eq("org_code",userOrgCode);
        }
        if(!StringUtils.isEmpty(pageLimit.getType())){
            if("DESC".equals(pageLimit.getType())){
                wrapper.orderByDesc(UnderlineTool.humpToLine(pageLimit.getField()));
            } else if("ASC".equals(pageLimit.getType())){
                wrapper.orderByAsc(UnderlineTool.humpToLine(pageLimit.getField()));
            }
        }
        PageHelper.startPage(pageLimit.getPage(),pageLimit.getLimit());
        List<DzEquipmentStateLog> dzEquipmentStateLogs = dzEquipmentStateLogMapper.selectList(wrapper);
        PageInfo<DzEquipmentStateLog> info=new PageInfo<>(dzEquipmentStateLogs);
        return new Result(CustomExceptionType.OK,info.getList(),info.getTotal());
    }

    /**
    * 删除设备运行日志
    * */
    @Override
    public void delEquimentLog(Integer i) {
        long time = System.currentTimeMillis() - ((long)i * (24 * 3600 * 1000));
        Date date = new Date(time);
        QueryWrapper<DzEquipmentStateLog> wrapper = new QueryWrapper<>();
        wrapper.le("create_time", date);
        remove(wrapper);
    }
}
