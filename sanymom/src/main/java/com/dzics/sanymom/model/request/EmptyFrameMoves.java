package com.dzics.sanymom.model.request;

import com.dzics.sanymom.model.request.distribution.MaterialList;
import lombok.Data;

import java.util.List;

/**
 * 空料框移动
 */
@Data
public class EmptyFrameMoves {

    /**
     * 请求ID
     */
    private String reqId;

    /**
     * 系统编码
     */
    private String reqSys;
    /**
     * 工厂编号
     */
    private String Facility;
    /**
     * 操作编码
     */
    private String reqType;
    /**
     * 料框类型
     */
    private String palletType;
    /**
     * 料框编码
     */
    private String palletNo;
    /**
     * 料框编码 请求空料框时 MOM 返回的料框编码
     */
    private String palletNoMomRes;

    /**
     * 起点料点编码
     */
    private String sourceNo;
    /**
     * 目的投料点编码
     */
    private String destNo;
    /**
     * 需求时间
     */
    private String requireTime;
    /**
     * 发送时间
     */
    private String sendTime;
    /**
     * 预留参数1 物料编码
     */
    private String paramRsrv1;
    /**
     * 预留参数2
     * NG（NG物料）、TL（退库）、不填为正常配送（中兴）
     */
    private String paramRsrv2;
    /**
     * 预留参数3
     */
    private String paramRsrv3;

    /**
     *  物料清单 例如料框 满 ，拉料是发送料框中的产品详情有哪些东西
     */
    private List<MaterialList> materialList;
}
