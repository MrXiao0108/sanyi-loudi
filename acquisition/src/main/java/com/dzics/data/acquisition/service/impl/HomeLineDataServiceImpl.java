package com.dzics.data.acquisition.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.common.dao.DzEquipmentProNumMapper;
import com.dzics.common.dao.DzProductionLineMapper;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.entity.DzProductionLine;
import com.dzics.common.model.response.LineDayAndSumDataDo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.RunDataModel;
import com.dzics.common.model.response.feishi.DayDataDo;
import com.dzics.common.model.response.feishi.DayDataResultDo;
import com.dzics.common.model.response.homepage.QualifiedAndOutputDo;
import com.dzics.common.util.DateUtil;
import com.dzics.data.acquisition.service.CacheService;
import com.dzics.data.acquisition.service.HomeLineDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class HomeLineDataServiceImpl implements HomeLineDataService {

    @Autowired
    CacheService cacheService;
    @Autowired
    DateUtil dateUtil;
    @Autowired
    DzEquipmentProNumMapper dzEquipmentProNumMapper;
    @Autowired
    DzProductionLineMapper dzProductionLineMapper;

    @Override
    public DayDataDo dayData(Long lineId) {
        //合格
        List<BigDecimal> qualified = new ArrayList<>();
        //不合格
        List<BigDecimal> badness = new ArrayList<>();
        String tableKey = ((RunDataModel) cacheService.systemRunModel(null).getData()).getTableName();
        String first = dateUtil.firstDay();
        String last = dateUtil.lastDay();
        List<DayDataResultDo> dayDataResultDoList = dzEquipmentProNumMapper.dayDataByLine(tableKey, first, last,lineId);
        for (DayDataResultDo data : dayDataResultDoList) {
            if (data.getQualifiedNum() != null) {
                qualified.add(data.getQualifiedNum());
            } else {
                qualified.add(new BigDecimal(0));
            }
            if (data.getBadnessNum() != null) {
                badness.add(data.getBadnessNum());
            } else {
                badness.add(new BigDecimal(0));
            }
        }
        DayDataDo dayDataDo = new DayDataDo();
        dayDataDo.setQualifiedNum(qualified);
        dayDataDo.setBadnessNum(badness);
        return dayDataDo;
    }

    @Override
    public DayDataDo monthData(Long lineId) {
        //合格
        List<BigDecimal> qualified = new ArrayList<>();
        //不合格
        List<BigDecimal> badness = new ArrayList<>();
        String tableKey = ((RunDataModel) cacheService.systemRunModel(null).getData()).getTableName();
        List<String> allMonth = dateUtil.getAllMonth();
        for (String month : allMonth) {
            //查询指定月份数据
            DayDataResultDo dayDataResultDo = dzEquipmentProNumMapper.monthDataByLine(month, tableKey,lineId);
            if (dayDataResultDo != null) {
                if (dayDataResultDo.getQualifiedNum() != null) {
                    qualified.add(dayDataResultDo.getQualifiedNum());
                } else {
                    qualified.add(new BigDecimal(0));
                }
                if (dayDataResultDo.getBadnessNum() != null) {
                    badness.add(dayDataResultDo.getBadnessNum());
                } else {
                    badness.add(new BigDecimal(0));
                }
            } else {
                log.error("查询月生产数据异常:{}，表:{}", month, tableKey);
            }
        }
        DayDataDo dayDataDo = new DayDataDo();
        dayDataDo.setQualifiedNum(qualified);
        dayDataDo.setBadnessNum(badness);
        return dayDataDo;
    }


    /**
     * 注：产出率=生产数量/毛坯
     * @param lineId
     * @return
     */
    @Override
    public QualifiedAndOutputDo outputCapacity(Long lineId) {
        String tableKey = ((RunDataModel) cacheService.systemRunModel(null).getData()).getTableName();
        String nowDate= dateUtil.getDate();
        QualifiedAndOutputDo res=dzEquipmentProNumMapper.outputCapacity(tableKey,lineId,nowDate);
        if(res.getRoughNum().intValue()==0){
            res.setOutput(new BigDecimal(0));
        }else{
            BigDecimal roughNum = new BigDecimal(res.getRoughNum());
            BigDecimal nowNum = new BigDecimal(res.getNowNum()*100);
            BigDecimal output=nowNum.divide(roughNum,2,BigDecimal.ROUND_HALF_UP);
            res.setOutput(output);//产出率
        }
        return res;

    }

    /**
     * 注：合格率=合格数量/生产数量
     * @param lineId
     * @return
     */
    @Override
    public QualifiedAndOutputDo percentOfPass(Long lineId) {
        String tableKey = ((RunDataModel) cacheService.systemRunModel(null).getData()).getTableName();
        String nowDate= dateUtil.getDate();
        QualifiedAndOutputDo res=dzEquipmentProNumMapper.outputCapacity(tableKey,lineId,nowDate);
        if(res.getNowNum().intValue()==0){
            res.setQualified(new BigDecimal(0));
        }else{
            BigDecimal qualifiedNum = new BigDecimal(res.getQualifiedNum()*100);
            BigDecimal nowNum = new BigDecimal(res.getNowNum());
            BigDecimal qualified=qualifiedNum.divide(nowNum,2,BigDecimal.ROUND_HALF_UP);
            res.setQualified(qualified);//合格率
        }
        return res;
    }


}
