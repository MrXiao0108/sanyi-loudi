package com.dzics.common.model.response.down;

import com.dzics.common.model.response.cpk.CPKA;
import com.dzics.common.model.response.cpk.CPKB;
import com.dzics.common.model.response.cpk.CPKC;
import com.dzics.common.model.response.cpk.CPKD;
import lombok.Data;

import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/7/7.
 * @since
 */
@Data
public class ExpCpkAll {
    /**
     * 原数值
     */
    private List<ExpCpkData> expCpkData;
    /**
     * 总样本数：上限值：下限值：标准值：
     */
    private List<ExpCpkOne> expCpkOnes;
    /**
     * cpk 信息
     */
    private List<CPKA> cpkas;
    private List<CPKB> cpkbs;
    private List<CPKC> cpkcs;
    private List<CPKD> cpkds;


    /**
     * info 信息
     */
    private List<ExpCpkInfo> info;
}
