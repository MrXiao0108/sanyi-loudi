package com.dzics.common.model.constant;

/**
 * @author ZhangChengJun
 * Date 2021/5/19.
 * @since
 */
public class QrCode {
    /**
     * 2=去工位取料  出
     */
    public static final String QR_CODE_OUT = "2";
    /**
     * 1=去工位放料 进
     */
    public static final String QR_CODE_IN = "1";
    public static  final String RRAMARKS = "根据订单产线 二维码 工位 未设置开始时间,已经收到结束时间";

    /**
     * mom 开工
     */
    public static final String QR_CODE_IN_MOM = "0";

    /**
     * mom 完工
     */
    public static final String QR_CODE_OUT_MOM = "1";
}
