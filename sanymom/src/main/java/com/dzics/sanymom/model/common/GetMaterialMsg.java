package com.dzics.sanymom.model.common;

import lombok.Data;

/**
 * @author ZhangChengJun
 * Date 2021/12/18.
 * @since
 */
@Data
public class GetMaterialMsg {
    /**
     * 料点编码
     */
    private String sourceNo;

    /**
     * 物料编号
     */
    private String paramRsrv1;
}
