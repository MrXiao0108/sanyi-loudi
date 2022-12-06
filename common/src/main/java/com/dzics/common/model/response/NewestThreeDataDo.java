package com.dzics.common.model.response;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xnb
 * @date 2021年04月07日 16:28
 */
@Data
public class NewestThreeDataDo {

    private String onlyKey;
    /**
     * 产品编码
     */
    @ApiModelProperty("产品编码")
    private String productNo;

    /**
     * 条形码
     */
    @ApiModelProperty(value = "条形码")
    private String producBarcode;

    /**
     * 总状态
     */
    @ApiModelProperty(value = "总状态")
    private Integer outOk;

    /**
     * 检测日期
     */
    @ApiModelProperty(value = "检测日期")
    private String detectorTime;
    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "检测01")
    private String detect01;

    @ApiModelProperty(value = "检测02")
    private String detect02;

    @ApiModelProperty(value = "检测03")
    private String detect03;

    @ApiModelProperty(value = "检测04")
    private String detect04;

    @ApiModelProperty(value = "检测05")
    private String detect05;

    @ApiModelProperty(value = "检测06")
    private String detect06;

    @ApiModelProperty(value = "检测07")
    private String detect07;

    @ApiModelProperty(value = "检测08")
    private String detect08;

    @ApiModelProperty(value = "检测09")
    private String detect09;

    @ApiModelProperty(value = "检测10")
    private String detect10;

    @ApiModelProperty(value = "检测11")
    private String detect11;

    @ApiModelProperty(value = "检测12")
    private String detect12;

    @ApiModelProperty(value = "检测13")
    private String detect13;

    @ApiModelProperty(value = "检测14")
    private String detect14;

    @ApiModelProperty(value = "检测15")
    private String detect15;

    @ApiModelProperty(value = "检测16")
    private String detect16;

    @ApiModelProperty(value = "检测17")
    private String detect17;

    @ApiModelProperty(value = "检测18")
    private String detect18;

    @ApiModelProperty(value = "检测19")
    private String detect19;

    @ApiModelProperty(value = "检测20")
    private String detect20;

    @ApiModelProperty(value = "检测21")
    private String detect21;

    @ApiModelProperty(value = "检测22")
    private String detect22;

    @ApiModelProperty(value = "检测23")
    private String detect23;

    @ApiModelProperty(value = "检测24")
    private String detect24;

    @ApiModelProperty(value = "检测25")
    @TableField("detect25")
    private String detect25;

    @ApiModelProperty(value = "检测26")
    private String detect26;

    @ApiModelProperty(value = "检测27")
    private String detect27;

    @ApiModelProperty(value = "检测28")
    private String detect28;

}
