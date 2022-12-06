package com.dzics.business.config.task;

import com.dzics.business.controller.agvmanage.AgvManageController;
import com.dzics.common.model.entity.DzLineShiftDay;
import com.dzics.common.service.DzLineShiftDayService;
import com.dzics.common.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

/**
 * 模拟agv 来料请求
 */
@Component
@Slf4j
public class AgvTask {
    @Autowired
    private DateUtil dateUtil;
    @Autowired
    private DzLineShiftDayService dzLineShiftDayService;
    /**
     * 模拟agv来料请求
     */
//    @Scheduled(fixedRate = 15000)
    public void dayReportFormTask() {
        System.out.println("定时任务触发了-----------------------------");
        if(AgvManageController.data==null){
            int i = new Random().nextInt(2);
            List<String> list = Arrays.asList("w1001", "w1002");
            Map<String, String> map1 = new HashMap<>();
            map1.put("orderNo", "DZ-6666");
            map1.put("workNumber", "5");
            map1.put("workpieceCode", list.get(i));
            map1.put("confirmTime", DateUtil.getDateStr(new Date()));
            AgvManageController.data=map1;
        }
    }


    /**
     * 设置排班表工作年份，工作月份 信息
     */
//    @PostConstruct
//    @Deprecated
    public void dayInitWorkMouth() {
        List<DzLineShiftDay> list = dzLineShiftDayService.list();
        for (DzLineShiftDay day : list) {
            LocalDate workData = day.getWorkData();
            String string = workData.toString();
            String substring = string.substring(0, string.length() - 3);
            day.setWorkMouth(substring);
            day.setWorkYear(2021);
        }
        dzLineShiftDayService.updateBatchById(list);
    }
}
