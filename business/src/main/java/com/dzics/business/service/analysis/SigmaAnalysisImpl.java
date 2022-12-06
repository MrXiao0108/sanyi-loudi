package com.dzics.business.service.analysis;

public class SigmaAnalysisImpl implements SigmaAnalysis,BaseAnalysis{
    private BaseAnalysis baseAnalysis;
    public SigmaAnalysisImpl(double[] analysisData) {
        this.baseAnalysis = new BaseAnalysisImpl(analysisData);
    }

    //根据sigma计算sigma值，一般sigma值为3
    @Override
    public double[] calculateSigma(int sigma) {
        double[] result = new double[2];
        double avg = this.baseAnalysis.calculateAverage();
        double sd = this.baseAnalysis.calculateStandardDeviation();
        result[0] = avg - sd * sigma;
        result[1] = avg + sd * sigma;
        return result;
    }

    @Override
    public double calculateAverage() {
        return this.baseAnalysis.calculateAverage();
    }

    @Override
    public double calculateMaxValue() {
        return this.baseAnalysis.calculateMaxValue();
    }

    @Override
    public double calculateMinValue() {
        return this.baseAnalysis.calculateMinValue();
    }

    @Override
    public double calculateStandardDeviation() {
        return this.baseAnalysis.calculateStandardDeviation();
    }
}
