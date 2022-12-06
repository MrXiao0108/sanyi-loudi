package com.dzics.common.model.response.charts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/9/23.
 * 统计图生产数量
 * @since
 */
@Data
public class WorkShiftSum<T> {
    /**
     * 数据名称
     */
    private String name;

    /**
     * 数据
     */
    private List<T> data;
    /**
     * 数据类型
     */
    private String stack;

    /*
    * 当前月份
    * */
    @JsonIgnore
    private String mouthValue;

}
