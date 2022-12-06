package com.dzics.common.model.response;

import lombok.Data;

import java.util.List;

/**
 * 设备名称ID
 *
 * @author ZhangChengJun
 * Date 2021/6/3.
 * @since
 */
@Data
public class DevcieNameHourSum {
    /**
     * 名称
     */
    private String name;

    /**
     * 曲线类型
     */
    private String type;
    /**
     * 数量
     */
    private List<Integer> data;
}
