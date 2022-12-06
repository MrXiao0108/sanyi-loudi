package com.dzics.common.model.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UploadProductDetectionVo {

    //产品编号
    @NotEmpty(message = "productNo 不能为空")
    private String productNo;
    //检测工位编号
    @NotEmpty(message = "stationNo 不能为空")
    private String stationNo;
    //机床编号
    @NotEmpty(message = "machineNo 不能为空")
    private String machineNo;
    //检测标记
    @NotNull(message = "detectionFlag 不能为空")
    private Integer detectionFlag;
    //产品唯一码
    @NotEmpty(message = "productCode 不能为空")
    private String productCode;
    //检测总项数
    @NotNull(message = "detectionNum 不能为空")
    private Integer detectionNum;
    //检测总项数
    @NotNull(message = "detectionTime 不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private String detectionTime;
    //检测明细
    @NotNull(message = "detectionDetails 不能为空")
    private List<DetectionDetailsDo> detectionDetails;
}
