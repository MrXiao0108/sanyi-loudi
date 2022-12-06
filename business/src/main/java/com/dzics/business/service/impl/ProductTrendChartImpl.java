package com.dzics.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.business.service.CpkService;
import com.dzics.business.service.ProductTrendChartService;
import com.dzics.common.dao.DzDetectorDataMapper;
import com.dzics.common.dao.DzProductDetectionTemplateMapper;
import com.dzics.common.enums.Message;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.entity.DzProductDetectionTemplate;
import com.dzics.common.model.request.SelectTrendChartVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.cpk.*;
import com.dzics.common.model.response.down.ExpCpkAll;
import com.dzics.common.model.response.down.ExpCpkData;
import com.dzics.common.model.response.down.ExpCpkInfo;
import com.dzics.common.model.response.down.ExpCpkOne;
import com.dzics.common.service.SysUserServiceDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ProductTrendChartImpl implements ProductTrendChartService {
    @Autowired
    DzDetectorDataMapper dzDetectorDataMapper;
    @Autowired
    DzProductDetectionTemplateMapper dzProductDetectionTemplateMapper;
    @Autowired
    SysUserServiceDao sysUserServiceDao;
    @Autowired
    private CpkService cpkService;

    @Override
    public Result<AnalysisDataVO> list(String sub, SelectTrendChartVo selectTrendChartVo) {
        if (StringUtils.isEmpty(selectTrendChartVo.getProductNo())) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_66);
        }
        if (selectTrendChartVo.getDetectionId() == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_67);
        }
        DzProductDetectionTemplate dzProductDetectionTemplate = dzProductDetectionTemplateMapper.selectById(selectTrendChartVo.getDetectionId());
        if (dzProductDetectionTemplate == null) {
            log.error("关联产品检测配置表 id不存在:{}", selectTrendChartVo.getDetectionId());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_4);
        }
        selectTrendChartVo.setDetectionName(dzProductDetectionTemplate.getTableColVal());
        List<BigDecimal> list = dzDetectorDataMapper.selectTrendChart(selectTrendChartVo);
        BigDecimal standardValue = dzProductDetectionTemplate.getStandardValue();
        BigDecimal upperValue = dzProductDetectionTemplate.getUpperValue();
        BigDecimal lowerValue = dzProductDetectionTemplate.getLowerValue();
        if (CollectionUtils.isNotEmpty(list)) {
            double[] doubles = list.stream().mapToDouble(BigDecimal::doubleValue).toArray();
            AnalysisDataVO analysisDataVO = cpkService.analysisData(doubles, standardValue.doubleValue(), upperValue.doubleValue(), lowerValue.doubleValue());
            return new Result(CustomExceptionType.OK, analysisDataVO);
        }
        return Result.error(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR17);
    }

    @Override
    public Result getByProductId(String productNo) {
        QueryWrapper<DzProductDetectionTemplate> eq = new QueryWrapper<DzProductDetectionTemplate>().eq("product_no", productNo).eq("is_show", 0);
        eq.select("detection_id", "table_col_con", "table_col_val");
        List<DzProductDetectionTemplate> dzProductDetectionTemplates = dzProductDetectionTemplateMapper.selectList(eq);
        return new Result(CustomExceptionType.OK, dzProductDetectionTemplates, dzProductDetectionTemplates.size());
    }

    @Override
    public ExpCpkAll getExpCpkDataAll(AnalysisDataVO analysisDataVO) {
        double[] analysisData = analysisDataVO.getAnalysisData();
        Double standValue = analysisDataVO.getStandValue();
        Double lowerLimitValue = analysisDataVO.getLowerLimitValue();
        Double upperLimitValue = analysisDataVO.getUpperLimitValue();
        CPK cpk = analysisDataVO.getCpk();
        List<ExpCpkData> expCpkDataList = new ArrayList<>();
        for (double analysisDatum : analysisData) {
            ExpCpkData cpkData = new ExpCpkData();
            cpkData.setNumber(analysisDatum);
            expCpkDataList.add(cpkData);
        }
        ExpCpkAll expCpkAll = new ExpCpkAll();
//        设置分析所有数据
        expCpkAll.setExpCpkData(expCpkDataList);
        List<ExpCpkOne> expCpkOnes = new ArrayList<>();
        ExpCpkOne expCpkOne = new ExpCpkOne();
        expCpkOne.setNumberAll(expCpkDataList.size());
        expCpkOne.setStandValue(standValue);
        expCpkOne.setLowerLimitValue(lowerLimitValue);
        expCpkOne.setUpperLimitValue(upperLimitValue);
        expCpkOnes.add(expCpkOne);
//        设备 总样本数：上限值：下限值：标准值：
        expCpkAll.setExpCpkOnes(expCpkOnes);
//        设置第二行 平均值等数据
        List<CPKA> cpkas = new ArrayList<>();
        CPKA cpka = new CPKA();
        cpka.setAverageValue(cpk.getAverageValue());
        cpka.setMaxValue(cpk.getMaxValue());
        cpka.setMinValue(cpk.getMinValue());

        cpkas.add(cpka);
        List<CPKB> cpkbs = new ArrayList<>();
        CPKB cpkb = new CPKB();
        cpkb.setSigma32(cpk.getSigma32());
        cpkb.setSigma31(cpk.getSigma31());
        cpkb.setStdev(cpk.getStdev());

        cpkbs.add(cpkb);
        List<CPKC> cpkcs = new ArrayList<>();
        CPKC cpkc = new CPKC();
        cpkc.setCpk(cpk.getCpk());
        cpkc.setCp(cpk.getCp());
        cpkc.setCpl(cpk.getCpl());
        cpkc.setCpu(cpk.getCpu());

        cpkcs.add(cpkc);
        List<CPKD> cpkds = new ArrayList<>();
        CPKD cpkd = new CPKD();
        cpkd.setPPMLessThanLSL(cpk.getPPMLessThanLSL());
        cpkd.setPPMGreaterThanUSL(cpk.getPPMGreaterThanUSL());
        cpkd.setPPMGreaterTotal(cpk.getPPMGreaterTotal());
        cpkd.setCa(cpk.getCa());

        cpkds.add(cpkd);
        expCpkAll.setCpkas(cpkas);
        expCpkAll.setCpkbs(cpkbs);
        expCpkAll.setCpkcs(cpkcs);
        expCpkAll.setCpkds(cpkds);
        List<ExpCpkInfo> infos = new ArrayList<>();
        ExpCpkInfo expCpkInfo = new ExpCpkInfo();
        expCpkInfo.setInfo(cpk.getInfo());
        infos.add(expCpkInfo);
        expCpkAll.setInfo(infos);
        return expCpkAll;
    }
}
