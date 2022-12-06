package com.dzics.business.service.impl;

import com.dzics.business.service.MomDzWorkingFlowService;
import com.dzics.common.dao.DzWorkingFlowMapper;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.request.mom.GetWorkingDetailsVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.mom.GetWorkingDetailsDo;
import com.dzics.common.util.DateUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class MomDzWorkingFlowServiceImpl implements MomDzWorkingFlowService {

    @Autowired
    private DzWorkingFlowMapper dzWorkingFlowMapper;
    @Autowired
    private DateUtil dateUtil;

    @Override
    public Result getWorkingDetails(GetWorkingDetailsVo getWorkingDetailsVo) {
        if (getWorkingDetailsVo.getPage() != -1) {
            PageHelper.startPage(getWorkingDetailsVo.getPage(), getWorkingDetailsVo.getLimit());
        }
        //开始、结束时间都为空的话，日期默认赋值当天
        if(StringUtil.isEmpty(getWorkingDetailsVo.getWorkStartTIme()) && StringUtil.isEmpty(getWorkingDetailsVo.getWorkEndTIme())){
            getWorkingDetailsVo.setWorkDate(dateUtil.getDate());
        }else {
            //只要开始时间不为空，设置日期大于等于开始时间
            if(!StringUtil.isEmpty(getWorkingDetailsVo.getWorkStartTIme())){
                getWorkingDetailsVo.setWorkDate(getWorkingDetailsVo.getWorkStartTIme().substring(0,10));
            }
        }
        List<GetWorkingDetailsDo> list = dzWorkingFlowMapper.getWorkingDetails(getWorkingDetailsVo);
        PageInfo<GetWorkingDetailsDo> info = new PageInfo<>(list);
        List<GetWorkingDetailsDo> detailsDos = info.getList();
        for (GetWorkingDetailsDo getWorkingDetailsDo : detailsDos) {
            Date startTime = getWorkingDetailsDo.getStartTime();
            Date completeTime = getWorkingDetailsDo.getCompleteTime();
            if (startTime != null && completeTime != null) {
                long stLong = startTime.getTime();
                long comTime = completeTime.getTime();
                long dev = (comTime - stLong) / 1000;
                getWorkingDetailsDo.setTaktTime(getGapTime(Long.valueOf(dev).intValue()));
            }

        }
        return new Result(CustomExceptionType.OK, detailsDos, info.getTotal());
    }

    public static String getGapTime(int time) {
        int minutes = time / 60;
        int seconds = time % 60;
        String timeString = minutes + " 分 " + seconds + " 秒";
        if (time < 0) {
            timeString = timeString + " 异常";
        }
        return timeString;
    }


}
