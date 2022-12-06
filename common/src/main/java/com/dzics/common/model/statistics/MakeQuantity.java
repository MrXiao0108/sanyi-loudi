package com.dzics.common.model.statistics;

import lombok.Data;

import java.io.Serializable;

/**
 * @Classname ProNumber
 * @Description 制作产品数据
 * @Date 2022/4/1 16:58
 * @Created by NeverEnd
 */
@Data
public class MakeQuantity implements Serializable {
    private Long nowNum;
    private Long qualifiedNum;
    private Long roughNum;
    private Long badnessNum;
    private Long equimentId;
}
