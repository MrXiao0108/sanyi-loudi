package com.dzics.sanymom.model.response.searchframe;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/11/11.
 * @since 查询料框信息 MOM 返回参数
 */
@Data
public class MaterialFrameRes implements Serializable {

    /**
     * 料框类型
     */
    private String palletType;
    /**
     *料框编码
     */
    private String palletNo;
    /**
     *投料组编号
     */
    private String packCode;
    /**
     *台套数量
     */
    private String setQuantity;
    /**
     *起点编码
     */
    private String sourceNo;
    /**
     *终点编码
     */
    private String destNo;
    /**
     *炉批号
     */
    private String lotNo;
    /**
     *预留参数1
     */
    private String paramRsrv1;
    /**
     *预留参数2
     */
    private String paramRsrv2;
    /**
     *物料清单
     */
    private List<SearchMaterialList> materialList;
}
