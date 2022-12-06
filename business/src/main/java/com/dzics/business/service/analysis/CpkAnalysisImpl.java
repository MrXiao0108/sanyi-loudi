package com.dzics.business.service.analysis;

import com.alibaba.excel.ExcelReader;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.ArrayList;
import java.util.List;

public class CpkAnalysisImpl implements CpkAnalysis,PPMAnalysis,QuantityStatistics,BaseAnalysis{
    private double standValue;
    private double lowerLimitValue;
    private double upperLimitValue;
    private double[] analysisDataArr;
    private BaseAnalysis baseAnalysis;

    public CpkAnalysisImpl(double[] analysisData,double standValue,double lowerLimitValue,double upperLimitValue) {
        this.baseAnalysis = new BaseAnalysisImpl(analysisData);
        this.analysisDataArr = analysisData;
        this.standValue = standValue;
        this.lowerLimitValue = lowerLimitValue;
        this.upperLimitValue = upperLimitValue;
    }

    /**
     * cpk:制程能力指数
     * @return
     */
    @Override
    public double getCpk() {
        double result = 0;
        double cp = getCp();
        double ca = getCa();
        result = cp * (1 - ca);
        return result;
    }

    /**
     * cp:制程精密度
     * @return
     */
    @Override
    public double getCp() {
        double result = 0;
        double t = this.upperLimitValue - this.lowerLimitValue;
        double sd = this.baseAnalysis.calculateStandardDeviation();
        if (sd != 0){
            result = t / (6 * sd);
        }
        return result;
    }

    /**
     * ca:制程准确度
     * @return
     */
    @Override
    public double getCa() {
        double result = 0;
        double avg = this.baseAnalysis.calculateAverage();
        double c = this.standValue;
        double t = this.upperLimitValue - this.lowerLimitValue;
        if (t != 0){
            result = (avg - c) / (t / 2);
        }

        return Math.abs(result);
    }

    /**
     * 均值趋近规格下限的程度
     * @return
     */
    @Override
    public double getCpl() {
        double result = 0;
        double avg = this.baseAnalysis.calculateAverage();
        double sd = this.baseAnalysis.calculateStandardDeviation();
        if (sd != 0){
            result = (avg - this.lowerLimitValue) / (3 * sd);
        }
        return Math.abs(result);
    }

    /**
     * 均值趋近规格上限的程度
     * @return
     */
    @Override
    public double getCpu() {
        double result = 0;
        double avg = this.baseAnalysis.calculateAverage();
        double sd = this.baseAnalysis.calculateStandardDeviation();
        if (sd != 0){
            result = (this.upperLimitValue - avg) / (3 * sd);
        }
        return Math.abs(result);
    }

    /**
     * 低于下限值不良率
     * @return
     */
    @Override
    public double calculatePPMLessThanLSL() {
        double result = 0;
        NormalDistribution normalDistribution = new NormalDistribution(0,1);
        double avg = this.baseAnalysis.calculateAverage();
        double sd = this.baseAnalysis.calculateStandardDeviation();
        if (sd != 0){
            double d = (avg - this.lowerLimitValue) / sd;
            result = (1 - normalDistribution.cumulativeProbability(d)) * 1000000;
        }
        return result;
    }

    /**
     * 高于上限值不良率
     * @return
     */
    @Override
    public double calculatePPMGreaterThanUSL() {
        double result = 0;
        NormalDistribution normalDistribution = new NormalDistribution(0,1);
        double avg = this.baseAnalysis.calculateAverage();
        double sd = this.baseAnalysis.calculateStandardDeviation();
        if (sd != 0){
            double d = (this.upperLimitValue - avg) / sd;
            result = (1 - normalDistribution.cumulativeProbability(d)) * 1000000;
        }
        return result;
    }

    /**
     * 计算不良率
     * @return
     */
    @Override
    public double calculatePPMTotal() {
        double result = 0;
        NormalDistribution normalDistribution = new NormalDistribution(0,1);
        double avg = this.baseAnalysis.calculateAverage();
        double sd = this.baseAnalysis.calculateStandardDeviation();
        if (sd != 0){
            double d1 = (this.upperLimitValue - avg) / sd;
            double d2 = (avg - this.lowerLimitValue) / sd;
            result = (2 - normalDistribution.cumulativeProbability(d1) - normalDistribution.cumulativeProbability(d2)) * 1000000;
        }
        return result;
    }

    /**
     * 低于下限值的数量
     * @return
     */
    @Override
    public int quantityLowerLimitValue() {
        int result = 0;
        for (int i = 0; i < this.analysisDataArr.length; i++) {
            double d = this.analysisDataArr[i];
            if (d < this.lowerLimitValue){
                result++;
            }
        }
        return result;
    }

    /**
     * 高于上限值的数量
     * @return
     */
    @Override
    public int quantityUpperLimitValue() {
        int result = 0;
        for (int i = 0; i < this.analysisDataArr.length; i++) {
            double d = this.analysisDataArr[i];
            if (d > this.upperLimitValue){
                result++;
            }
        }
        return result;
    }

    //测试代码
    public static void main(String[] args) {
//        ExcelReader reader = ExcelUtil.getReader("C:\\Users\\sorke\\Desktop\\cpk-01.xlsx");
        List<List<Object>> read = new ArrayList<>();
        double[] doubles = read.stream().mapToDouble(list -> Double.valueOf(list.get(0).toString())).toArray();

        CpkAnalysis cpkAnalysis = new CpkAnalysisImpl(doubles,25.5,23,28);
        double ca = cpkAnalysis.getCa();
        double cp = cpkAnalysis.getCp();
        double cpk = cpkAnalysis.getCpk();

        double cpl = cpkAnalysis.getCpl();
        double cpu = cpkAnalysis.getCpu();

        double ppm1 = ((PPMAnalysis) cpkAnalysis).calculatePPMLessThanLSL();
        double ppm2 = ((PPMAnalysis) cpkAnalysis).calculatePPMGreaterThanUSL();

        double ppmtotal = ((PPMAnalysis) cpkAnalysis).calculatePPMTotal();

        int c1 = ((QuantityStatistics) cpkAnalysis).quantityLowerLimitValue();
        int c2 = ((QuantityStatistics) cpkAnalysis).quantityUpperLimitValue();

        System.out.printf("ca : %s,cp : %s,cpk : %s,cpl : %s,cpu : %s\n",ca,cp,cpk,cpl,cpu);
        System.out.printf("ppm-lessthanLSL : %s,ppm-greaterthanUSL : %s,ppm-total : %s\n",ppm1,ppm2,ppmtotal);

        System.out.printf("c1 : %s,c2 : %s",c1,c2);
    }

    //计算平均值
    @Override
    public double calculateAverage() {
        return this.baseAnalysis.calculateAverage();
    }

    //计算最大值
    @Override
    public double calculateMaxValue() {
        return this.baseAnalysis.calculateMaxValue();
    }

    //计算最小值
    @Override
    public double calculateMinValue() {
        return this.baseAnalysis.calculateMinValue();
    }

    //计算标准差
    @Override
    public double calculateStandardDeviation() {
        return this.baseAnalysis.calculateStandardDeviation();
    }
}
