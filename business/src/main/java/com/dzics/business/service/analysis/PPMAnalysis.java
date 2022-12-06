package com.dzics.business.service.analysis;

public interface PPMAnalysis {
    double calculatePPMLessThanLSL();
    double calculatePPMGreaterThanUSL();
    double calculatePPMTotal();
}
