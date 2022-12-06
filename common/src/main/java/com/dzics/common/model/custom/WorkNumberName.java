package com.dzics.common.model.custom;

import lombok.Data;

import java.io.Serializable;

/**
 * 返回工件名称，工件编号
 *
 * @author ZhangChengJun
 * Date 2021/3/4.
 * @since
 */
@Data
public class WorkNumberName implements Serializable {
    /**
     * 工件编号
     */
    private String modelNumber;

    /**
     * 工件名称
     */
    private String productName;
}
