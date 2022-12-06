package com.dzics.common.model.response.charts.loudi;

import com.dzics.common.model.response.charts.WorkShiftSum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Classname WorkShiftLouDi
 * @Description 描述
 * @Date 2022/4/27 14:09
 * @Created by NeverEnd
 */
@Data
public class WorkShiftLouDi implements Serializable {
    private List<String> x1;
    private List<String> x2;
    private List<WorkShiftSum> workShiftSums;
}
