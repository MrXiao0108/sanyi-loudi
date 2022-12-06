package com.dzics.common.model.agv.materiel;

import lombok.Data;

@Data
public class ProductionMaterialList {
        private int quantity;//数量
        private String materialNo;//物料编码
        private String materialType;//材质
        private String length;//长
        private String width;//宽
        private String height;//高（厚度）
}
