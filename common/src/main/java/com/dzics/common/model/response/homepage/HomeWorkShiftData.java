package com.dzics.common.model.response.homepage;

import com.dzics.common.model.response.charts.WorkShiftSum;
import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/9/27.
 * @since
 */
@Data
public class HomeWorkShiftData<T> {
    private List<WorkShiftSum> dayWorkShiftSum;
    private T mouthWorkShiftSum;
    private String mouthValue;
}
