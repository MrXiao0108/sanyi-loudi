package com.dzics.business.service.impl;

import com.dzics.business.service.CpkService;
import com.dzics.business.service.analysis.BaseAnalysis;
import com.dzics.business.service.analysis.CpkAnalysisImpl;
import com.dzics.business.service.analysis.SigmaAnalysis;
import com.dzics.business.service.analysis.SigmaAnalysisImpl;
import com.dzics.common.dao.DzWorkpieceDataMapper;
import com.dzics.common.model.entity.DzProductDetectionTemplate;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.cpk.AnalysisDataVO;
import com.dzics.common.model.response.cpk.CPK;
import com.dzics.common.service.DzProductDetectionTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.IllegalFormatException;

/**
 * @author ZhangChengJun
 * Date 2021/6/28.
 * @since
 */
@Slf4j
@Service
public class CpkServiceImpl implements CpkService {
    @Override
    public AnalysisDataVO analysisData(double[] doubles, double standardValue, double upperValue, double lowerValue) {
        AnalysisDataVO analysisDataVO = new AnalysisDataVO();
        if (doubles.length >= 0) {
            CPK cpk = new CPK();
            SigmaAnalysis sigmaAnalysis = new SigmaAnalysisImpl(doubles);
            BaseAnalysis baseAnalysis = (BaseAnalysis) sigmaAnalysis;
            //计算平均值
            double average = baseAnalysis.calculateAverage();
            //计算最大值
            double max = baseAnalysis.calculateMaxValue();
            //计算最小值
            double min = baseAnalysis.calculateMinValue();
            //计算标准差
            double sd = baseAnalysis.calculateStandardDeviation();
            cpk.setAverageValue(getDouble2(average, 3));
            cpk.setMaxValue(max);
            cpk.setMinValue(min);
            double[] doubleA = sigmaAnalysis.calculateSigma(3);
            cpk.setSigma31(getDouble2(doubleA[0], 2));
            cpk.setSigma32(getDouble2(doubleA[1], 2));
            cpk.setStdev(getDouble2(sd, 6));
            analysisDataVO.setCpk(cpk);
            analysisDataVO.setAnalysisData(doubles);
        }
        analysisDataVO.setLowerLimitValue(lowerValue);
        analysisDataVO.setStandValue(standardValue);
        analysisDataVO.setUpperLimitValue(upperValue);
        if (analysisDataVO.getStandValue() != null && analysisDataVO.getLowerLimitValue() != null && analysisDataVO.getUpperLimitValue() != null && analysisDataVO.getAnalysisData() != null && analysisDataVO.getAnalysisData().length > 0) {
            calculateCPK(analysisDataVO, analysisDataVO.getAnalysisData(), analysisDataVO.getStandValue(), analysisDataVO.getLowerLimitValue(), analysisDataVO.getUpperLimitValue());
        }
        return analysisDataVO;
    }


    /**
     * 计算cpk相关指标，并填充VO
     *
     * @param analysisDataVO
     * @param analysisData
     * @param standValue
     * @param lowerLimitValue
     * @param upperLimitValue
     */
    private void calculateCPK(AnalysisDataVO analysisDataVO, double[] analysisData, double standValue, double lowerLimitValue, double upperLimitValue) {
        CpkAnalysisImpl cpkAnalysis = new CpkAnalysisImpl(analysisData, standValue, lowerLimitValue, upperLimitValue);
        CPK cpk = analysisDataVO.getCpk();
        cpk.setCpk(getDouble2(cpkAnalysis.getCpk(), 2));
        cpk.setCp(getDouble2(cpkAnalysis.getCp(), 2));
        cpk.setCpl(getDouble2(cpkAnalysis.getCpl(), 2));
        cpk.setCpu(getDouble2(cpkAnalysis.getCpu(), 2));
        cpk.setPPMLessThanLSL(getDouble2(cpkAnalysis.calculatePPMLessThanLSL(), 2));
        cpk.setPPMGreaterThanUSL(getDouble2(cpkAnalysis.calculatePPMGreaterThanUSL(), 2));
        cpk.setPPMGreaterTotal(getDouble2(cpkAnalysis.calculatePPMTotal(), 2));
        cpk.setCa(getDouble2(cpkAnalysis.getCa(), 2));
        int quantityLower = cpkAnalysis.quantityLowerLimitValue();
        int quantityUpper = cpkAnalysis.quantityUpperLimitValue();
        int length = analysisData.length;
        String format = "[n=%d] > %1.2f %%%1.2f [n=%d] < %1.2f %%%1.2f [n=%d] 超规格 %%%1.2f";
        try {
            String info = String.format(format, quantityUpper, upperLimitValue, ((double) quantityUpper / length) * 100, quantityLower, lowerLimitValue, ((double) quantityLower / length) * 100, (quantityLower + quantityUpper), ((double) (quantityLower + quantityUpper) / length) * 100);
            cpk.setInfo(info);
        } catch (IllegalFormatException illegalFormatException) {

        }
    }

    //double保留小数位数
    private double getDouble2(double a, int scale) {
        BigDecimal bigDecimal = new BigDecimal(a);
        double b = bigDecimal.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
        return b;
    }


}
