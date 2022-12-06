package com.dzics.sanymom.model.request;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 根据序列号查询订单请求参数
 *
 * @author ZhangChengJun
 * Date 2022/1/19.
 * @since
 */
@Data
@Slf4j
public class PullSearchOrderParms {
    /**
     * 系统编码
     */
    private String reqSys;
    /**
     * 工厂编号
     */
    private String Facility;
    /**
     * 产品序列号
     */
    private String serialNo;
    /**
     * 工位
     */
    private List<String> workStation;

    /**
     * 预留参数1
     */
    private String paramRsrv1;
    /**
     * 预留参数2
     */
    private String paramRsrv2;
    /**
     * 预留参数3
     */
    private String paramRsrv3;

}
