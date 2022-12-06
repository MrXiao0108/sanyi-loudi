package com.dzics.business.config.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.dao.DzEquipmentMapper;
import com.dzics.common.dao.DzEquipmentTimeAnalysisMapper;
import com.dzics.common.model.entity.DzEquipment;
import com.dzics.common.model.entity.DzEquipmentTimeAnalysis;
import com.dzics.common.service.DzEquipmentTimeAnalysisService;
import com.dzics.common.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @作者：xnb
 * @时间：2022/8/31 0031  15:35
 */
@Component
@Slf4j
public class TimeAnalysisTask {
    @Autowired
    private DzEquipmentTimeAnalysisMapper timeAnalysisMapper;
    @Autowired
    private DzEquipmentTimeAnalysisService timeAnalysisService;
    @Autowired
    private DzEquipmentMapper equipmentService;
    @Autowired
    private DateUtil dateUtil;

    private final Integer delDay = 30;

    //每3分钟执行一次
    @Scheduled(cron = "0 0/3 * * * ?")
    public void upTimeAnalysisCloseDate(){
        Integer sum = 0;
//            如果当前时间在 早上 08:00 之前 设置开始时间是前一天 08:00 之后
//            如果当前时间在 早上 08:00 之后 设置时间是当前日期 08:00 之后
        try {
            Calendar calenda = Calendar.getInstance();
            calenda.setTime(new Date());
            if (LocalTime.now().getHour() < 8) {
                calenda.add(Calendar.DATE, -1);
            }
            calenda.set(Calendar.HOUR_OF_DAY, 8);
            calenda.set(Calendar.MINUTE, 0);
            calenda.set(Calendar.MILLISECOND, 0);
            calenda.set(Calendar.SECOND, 0);
            Date time = calenda.getTime();
            LocalDate localDate = DateUtil.getLocalDate(time);
            List<DzEquipment> equipmentList = equipmentService.selectList(new QueryWrapper<DzEquipment>().eq("is_show", 1));
            QueryWrapper<DzEquipmentTimeAnalysis>timeWrapper=new QueryWrapper<>();
            for (DzEquipment dzEquipment : equipmentList) {
                timeWrapper.clear();
                timeWrapper.eq("device_id",dzEquipment.getId());
                timeWrapper.ge("stop_time",time);
                timeWrapper.ge("stop_data",localDate);
                timeWrapper.orderByAsc("create_time");
                List<DzEquipmentTimeAnalysis> analysisList = timeAnalysisMapper.selectList(timeWrapper);
                if(CollectionUtils.isNotEmpty(analysisList)){
                    for(int i = 0; i < analysisList.size();i++){
                        if(i!=0 && analysisList.get(i).getDuration()==3000 && analysisList.get(i).getWorkState()!=analysisList.get(i-1).getWorkState()){
                            analysisList.get(i).setWorkState(analysisList.get(i-1).getWorkState());
                            boolean b = timeAnalysisService.updateById(analysisList.get(i));
                            if(b){
                                sum = sum + 1;
                            }
                        }else{
                            continue;
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
//        if(log.isInfoEnabled()){
//            log.info("修改设备用时分析秒级异常数据"+sum);
//        }
    }

    /**
     * 定时清除设备用时分析表 数据
     * */
    @Scheduled(cron = "0 0 12 1/1 * ?")
    public void delTimeAnalysisTable(){
        if(log.isInfoEnabled()){
            log.info("开始定时清除设备用时分析表数据");
        }
        int delete = 0;
        try {
            Date nowDate = dateUtil.getNowDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(nowDate);
            calendar.add(Calendar.DATE,-delDay);
            Date time = calendar.getTime();
            QueryWrapper<DzEquipmentTimeAnalysis>timeAnalysisQueryWrapper=new QueryWrapper<>();
            timeAnalysisQueryWrapper.le("create_time",time);
            delete = timeAnalysisMapper.delete(timeAnalysisQueryWrapper);
        }catch(Exception e){
            e.printStackTrace();
        }
        if(log.isInfoEnabled()){
            log.info("定时清除设备用时分析表数据完成,清除数据{}条",delete);
        }
    }

}
