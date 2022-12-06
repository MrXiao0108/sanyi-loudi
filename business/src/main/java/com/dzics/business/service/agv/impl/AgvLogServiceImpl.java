package com.dzics.business.service.agv.impl;import com.alibaba.fastjson.JSON;import com.alibaba.fastjson.JSONObject;import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;import com.dzics.business.service.BusLogPromptMsgService;import com.dzics.common.model.custom.WorkReportDto;import com.dzics.common.model.dto.check.LogPromptMsgDto;import com.dzics.common.model.entity.DzWorkStationManagement;import com.dzics.common.model.mom.response.*;import com.dzics.common.model.request.mom.AgvLogParms;import com.dzics.business.service.agv.AgvLogAbs;import com.dzics.common.model.entity.LogPromptMsgMom;import com.dzics.common.model.response.Result;import com.dzics.common.service.DzWorkStationManagementService;import com.dzics.common.service.LogPromptMsgMomService;import com.dzics.common.util.DateUtil;import com.dzics.common.util.PageLimitBase;import com.github.pagehelper.PageHelper;import com.github.pagehelper.PageInfo;import com.github.pagehelper.util.StringUtil;import lombok.extern.slf4j.Slf4j;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.stereotype.Service;import org.springframework.util.CollectionUtils;import org.springframework.util.StringUtils;import java.math.BigDecimal;import java.math.RoundingMode;import java.util.List;/** * @author ZhangChengJun * Date 2022/1/13. */@Service@Slf4jpublic class AgvLogServiceImpl extends AgvLogAbs {    @Autowired    private LogPromptMsgMomService logPromptMsgMomService;    @Autowired    private DateUtil dateUtil;    @Autowired    private BusLogPromptMsgService busLogPromptMsgService;    @Autowired    private DzWorkStationManagementService stationManagementService;    @Override    public Result getLogPropMsg(AgvLogParms logParms) {        PageHelper.startPage(logParms.getPage(), logParms.getLimit());        if(StringUtil.isEmpty(logParms.getBeginTime()) && StringUtil.isEmpty(logParms.getEndTime())){            logParms.setCreateDate(dateUtil.getDate());        }        List<LogPromptMsgDto> momLogs = busLogPromptMsgService.getMomLogs(logParms.getOrderNo(), logParms.getCreateDate(), logParms.getWipOrderNo(), logParms.getPointCode(), logParms.getBrief(), logParms.getBeginTime(), logParms.getEndTime(),logParms.getField(),logParms.getType());        PageInfo<LogPromptMsgDto> info = new PageInfo<>(momLogs);        List<LogPromptMsgDto> msgs = info.getList();        Result result = null;        WorkReportDto workReportDto = new WorkReportDto();        for (LogPromptMsgDto msg : msgs) {            workReportDto = JSONObject.parseObject(msg.getInvokParm(),WorkReportDto.class);            String invokReturn = msg.getInvokReturn();            if (!StringUtils.isEmpty(invokReturn)) {//                找到第一个冒号截取 后边的字符串                int index = invokReturn.indexOf(":");                if (index > 0) {                    invokReturn = invokReturn.substring(index + 1);                }                msg.setInvokReturn(invokReturn);            }            BigDecimal divide = msg.getInvokCost().divide(new BigDecimal(1000)).setScale(2, RoundingMode.HALF_UP);            msg.setInvokCost(divide);//            只要返回信息为空,设置失败            if(StringUtils.isEmpty(msg.getInvokReturn())){                msg.setStatus("失败");            }else{                if("报工".equals(msg.getBrief())){                    //获取当前产线的最后一道报工工位                    List<DzWorkStationManagement> managements = stationManagementService.list(new QueryWrapper<DzWorkStationManagement>()                            .eq("line_id", workReportDto.getLineId()).isNotNull("dz_station_code").orderByDesc("sort_code"));                    DzWorkStationManagement management = managements.get(0);                    //获取最后一条待报工表中记录                    List<LogPromptMsgMom> list = logPromptMsgMomService.list(                            new QueryWrapper<LogPromptMsgMom>().eq("group_id",msg.getGroupId()).orderByDesc("create_time"));                    if(CollectionUtils.isEmpty(list)){                        msg.setStatus("失败");                        continue;                    }                    //获取最新mom_log表数据                    LogPromptMsgMom msgMom = list.get(0);                    //请求参数                    String invokeParam = msgMom.getInvokParm();                    //返回信息                    String invokReturnMom = msgMom.getInvokReturn();                    if(invokReturnMom.contains("code")==false){                        msg.setStatus("失败");                        continue;                    }                    try {                        //返序列化                        RequestHeaderVo headerVo = JSONObject.parseObject(invokeParam,RequestHeaderVo.class);                        ResultVo vo = JSONObject.parseObject(invokReturnMom, ResultVo.class);                        List<GeneralControlModel> generalControlModels = JSONObject.parseArray(JSONObject.toJSONString(headerVo.getReported()), GeneralControlModel.class);                        //如果是报工最后一道工位的、并且是完工请求、并且mom返回响应为成功，方才设置成功                        if(management.getDzStationCode().equals(generalControlModels.get(0).getWorkStation()) && "1".equals(generalControlModels.get(0).getProgressType()) && "0".equals(vo.getCode())){                            msg.setStatus("成功");                        }else{                            msg.setStatus("失败");                        }                    }catch(Throwable throwable){                        msg.setStatus("失败");                        throwable.printStackTrace();                        continue;                    }                }else if("检测记录上传".equals(msg.getBrief())){                    if(msg.getInvokReturn().contains("true")){                        msg.setStatus("成功");                    }else{                        msg.setStatus("失败");                    }                }else{//                    返回信息中没有code,说明已经抛出异常,设置失败                    if(msg.getInvokReturn().contains("code")){                        result = JSON.parseObject(msg.getInvokReturn(),Result.class);//                        只要返回code中不为0,说明失败                        if(0==result.getCode()){                            msg.setStatus("成功");                        }else{                            msg.setStatus("失败");                        }                    }else{                        msg.setStatus("失败");                    }                }            }        }        return Result.ok(msgs, info.getTotal());    }    @Override    public Result getLogPropMsgMom(String groupId, PageLimitBase pageLimitBase) {        PageHelper.startPage(pageLimitBase.getPage(), pageLimitBase.getLimit());        QueryWrapper<LogPromptMsgMom> wp = new QueryWrapper<>();        wp.eq("group_id", groupId);        wp.orderByAsc("start_time");        List<LogPromptMsgMom> list = logPromptMsgMomService.list(wp);        PageInfo<LogPromptMsgMom> info = new PageInfo<>(list);        return Result.ok(info.getList(), info.getTotal());    }}