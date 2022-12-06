package com.dzics.sanymom.model.request.agv;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Classname MaterialParmsDto
 * @Description 物料参数
 * @Date 2022/5/12 14:10
 * @Created by NeverEnd
 */
@Data
public class MaterialParmsDto {

    /**
     * 物料编码
     */
    private String materialNo;

    /**
     * 数量
     */
    private BigDecimal quantity;

    /**
     * 序列号
     */
    private String serialNo;

}
