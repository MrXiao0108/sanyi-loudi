package com.dzics.business.service.analysis;

import java.util.Arrays;

public class BaseAnalysisImpl implements BaseAnalysis{
    private double[] analysisData;
    public BaseAnalysisImpl(double[] analysisData){
        this.analysisData = analysisData;
    }

    /**
     * 计算平均值
     * @return
     */
    @Override
    public double calculateAverage() {
        double result = 0;
        if (analysisData != null && analysisData.length > 1){
            result = Arrays.stream(analysisData).average().getAsDouble();
        }
        return result;
    }

    /**
     * 计算最大值
     * @return
     */
    @Override
    public double calculateMaxValue() {
        double result = 0;
        if (analysisData != null && analysisData.length > 0){
            result = Arrays.stream(analysisData).max().getAsDouble();
        }
        return result;
    }

    /**
     * 计算最小值
     * @return
     */
    @Override
    public double calculateMinValue() {
        double result = 0;
        if (analysisData != null && analysisData.length > 0){
            result = Arrays.stream(analysisData).min().getAsDouble();
        }
        return result;
    }

    /**
     * 标准差
     * @return
     */
    @Override
    public double calculateStandardDeviation() {
        double result = 0;
        if (analysisData != null && analysisData.length > 1){
            double v = calculateAverage();
            double sum = 0;
            for (int i = 0; i < this.analysisData.length; i++) {
                sum += (this.analysisData[i] - v) * (this.analysisData[i] - v);
            }
            double v1 = sum / (this.analysisData.length - 1);
            result = Math.sqrt(v1);
        }
        return result;
    }
}
