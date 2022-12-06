package com.dzics.sanymom.model.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Material {
    //物料编码
    private String materialNo;
    //数量
    private BigDecimal quantity;
    //材质
    private String materialType;
    //长
    private String length;
    //宽
    private String width;
    //高(厚度)
    private String height;

}
