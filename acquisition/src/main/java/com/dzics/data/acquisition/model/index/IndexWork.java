package com.dzics.data.acquisition.model.index;

import lombok.Data;

/**
 * 首页工件信息
 *
 * @author ZhangChengJun
 * Date 2021/3/4.
 * @since
 */
@Data
public class IndexWork {

    /**
     * 产品编号 产品名称
     */
    private String modelNumber;

    /**
     * 产品名称 工件名称
     */
    private String productName;
}
