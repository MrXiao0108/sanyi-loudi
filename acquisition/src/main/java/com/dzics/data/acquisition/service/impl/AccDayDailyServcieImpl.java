package com.dzics.data.acquisition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.common.enums.EquiTypeEnum;
import com.dzics.common.model.entity.DayDailyReport;
import com.dzics.common.model.request.base.BaseTimeLimit;
import com.dzics.common.model.response.DayReportForm;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.plan.DayDailyReportExcel;
import com.dzics.common.service.DayDailyReportService;
import com.dzics.data.acquisition.service.AccDayDailyServcie;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/11/19.
 * @since
 */
@Service
@Slf4j
public class AccDayDailyServcieImpl implements AccDayDailyServcie {
    @Autowired
    private DayDailyReportService dayDailyReportService;

    @Override
    public boolean saveDayDayReport(List<DayReportForm> dayReportForms) {
        List<DayDailyReport> dayDailyReports = new ArrayList<>();
        for (DayReportForm reportForm : dayReportForms) {
            Long nowNum = reportForm.getNowNum() != null ? reportForm.getNowNum() : 0L;
            Long qualifiedNum = reportForm.getQualifiedNum() != null ? reportForm.getQualifiedNum() : 0L;
            Long roughNum = reportForm.getRoughNum() != null ? reportForm.getRoughNum() : 0L;
            Long badnessNum = reportForm.getBadnessNum() != null ? reportForm.getBadnessNum() : 0L;
            DayDailyReport dayDailyReport = new DayDailyReport();
            dayDailyReport.setLinename(reportForm.getLineName());
            int i = reportForm.getEquipmentType().intValue();
            if (i == EquiTypeEnum.JC.getCode()) {
                dayDailyReport.setEquipmentType(EquiTypeEnum.JC);
            } else if (i == EquiTypeEnum.JQR.getCode()) {
                dayDailyReport.setEquipmentType(EquiTypeEnum.JQR);
            } else if (i == EquiTypeEnum.XJ.getCode()) {
                dayDailyReport.setEquipmentType(EquiTypeEnum.XJ);
            } else if (i == EquiTypeEnum.JCSB.getCode()) {
                dayDailyReport.setEquipmentType(EquiTypeEnum.JCSB);
            } else if (i == EquiTypeEnum.EQCODE.getCode()) {
                dayDailyReport.setEquipmentType(EquiTypeEnum.EQCODE);
            }
            dayDailyReport.setEquipmentcode(reportForm.getEquipmentCode());
            dayDailyReport.setEquipmentname(reportForm.getEquipmentName());
            dayDailyReport.setWorkname(reportForm.getWorkName());
            dayDailyReport.setTimeRange(reportForm.getStartTime() + "-" + reportForm.getEndTime());
            dayDailyReport.setWorkdata(reportForm.getWorkData());
            dayDailyReport.setNownum(nowNum);
            dayDailyReport.setRoughnum(roughNum);
            dayDailyReport.setQualifiednum(qualifiedNum);
            dayDailyReport.setBadnessnum(badnessNum);
//            ????????????
            BigDecimal nowBigDeci = new BigDecimal(nowNum);
//            ??????
            BigDecimal roughBigDeci = new BigDecimal(roughNum);
//            ????????? = ????????????/??????
            BigDecimal divide = new BigDecimal(0);
            if (roughBigDeci.compareTo(new BigDecimal(0)) != 0) {
                divide = nowBigDeci.divide(roughBigDeci, 2, BigDecimal.ROUND_HALF_UP);
            }
            dayDailyReport.setOutputRate(divide);
//            ??????
            BigDecimal qualiDecimal = new BigDecimal(qualifiedNum);
//            ????????? = ??????/ ??????
            BigDecimal passRate = new BigDecimal(0);
            if (nowBigDeci.compareTo(new BigDecimal(0)) != 0) {
                passRate = qualiDecimal.divide(nowBigDeci, 2, BigDecimal.ROUND_HALF_UP);
            }
            dayDailyReport.setPassRate(passRate);
            dayDailyReport.setEquimentid(reportForm.getEquimentId());
            dayDailyReport.setLineid(reportForm.getLineId());
            dayDailyReport.setLineno(reportForm.getLineNo());
            dayDailyReport.setOrderno(reportForm.getOrderNo());
            dayDailyReport.setOrgCode("A001");
            dayDailyReport.setDelFlag(false);
            dayDailyReport.setCreateBy("system");
            dayDailyReports.add(dayDailyReport);
        }
        dayDailyReportService.saveBatch(dayDailyReports);
        return false;
    }

    @Override
    public Result<List<DayDailyReportExcel>> dayDailyReport(String sub, BaseTimeLimit timeBase) {
        if (timeBase.getPage() != -1) {
            PageHelper.startPage(timeBase.getPage(), timeBase.getLimit());
        }
        List<DayDailyReportExcel> list = dayDailyReportService.getDayDailyReport(timeBase.getField(), timeBase.getType(), timeBase.getEndTime(), timeBase.getStartTime());
        PageInfo<DayDailyReportExcel> info = new PageInfo<>(list);
        Result<List<DayDailyReportExcel>> ok = Result.OK(info.getList());
        ok.setCount(info.getTotal());
        return ok;
    }

    @Override
    public boolean getWorkDate(LocalDate now) {
        QueryWrapper<DayDailyReport> wp = new QueryWrapper<>();
        wp.eq("workData", now);
        int count = dayDailyReportService.count(wp);
        return count <= 0;
    }
}
