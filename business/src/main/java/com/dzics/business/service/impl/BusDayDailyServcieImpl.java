package com.dzics.business.service.impl;

import com.dzics.business.service.BusDayDailyServcie;
import com.dzics.common.enums.EquiTypeEnum;
import com.dzics.common.model.entity.DayDailyReport;
import com.dzics.common.model.request.base.BaseTimeLimit;
import com.dzics.common.model.response.DayReportForm;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.plan.DayDailyReportExcel;
import com.dzics.common.service.DayDailyReportService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/6/23.
 * @since
 */
@Slf4j
@Service
public class BusDayDailyServcieImpl implements BusDayDailyServcie {
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
//            生产数量
            BigDecimal nowBigDeci = new BigDecimal(nowNum);
//            毛坯
            BigDecimal roughBigDeci = new BigDecimal(roughNum);
//            产出率 = 生产数量/毛坯
            BigDecimal divide = new BigDecimal(0);
            if (roughBigDeci.compareTo(new BigDecimal(0)) != 0) {
                divide = nowBigDeci.divide(roughBigDeci, 2, BigDecimal.ROUND_HALF_UP);
            }
            dayDailyReport.setOutputRate(divide);
//            合格
            BigDecimal qualiDecimal = new BigDecimal(qualifiedNum);
//            合格率 = 合格/ 产出
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
        List<DayDailyReportExcel> list = dayDailyReportService.getDayDailyReport(timeBase.getField(),timeBase.getType(),timeBase.getEndTime(), timeBase.getStartTime());
        PageInfo<DayDailyReportExcel> info = new PageInfo<>(list);
        Result<List<DayDailyReportExcel>> ok = Result.OK(info.getList());
        ok.setCount(info.getTotal());
        return ok;
    }
}
