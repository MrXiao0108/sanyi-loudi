package com.dzics.common.model.request.datadevice;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class AddDataDeviceVo  implements Serializable {

    @ApiModelProperty(value = "主键，新增不填，修改必填")
    private Long deviceKey;

    @ApiModelProperty(value = "第三方设备id")
    @NotNull(message = "第三方设备id不能为空")
    private Long deviceId;

    @ApiModelProperty(value = "公司代码")
    @NotEmpty(message = "公司代码不能为空")
    private String companyCode;

    @ApiModelProperty(value = "工厂代码")
    @NotEmpty(message = "工厂代码不能为空")
    private String factoryCode;

    @ApiModelProperty(value = "设备名称")
    @NotEmpty(message = "设备名称不能为空")
    private String deviceName;

    @ApiModelProperty(value = "设备类型（1.搬运机器人，2.焊接机器人，3.数控设备，4.清洗机，5.检测设备,6 热处理机,7 校直机 ）")
    @NotNull(message = "设备类型不能为空")
    private Integer deviceType;

    @ApiModelProperty(value = "设备类型编号")
    @NotEmpty(message = "设备类型编号不能为空")
    private String deviceTypeCode;

    @ApiModelProperty(value = "资产编码")
    private String assetsEncoding;

    @ApiModelProperty(value = "系统型号")
    private String systemProductName;

    @ApiModelProperty(value = "大正设备id")
    @NotNull(message = "大正设备id 不能为空")
    private Long equipmentId;

    @ApiModelProperty(value = "序列号")
    private String serNum;

    @ApiModelProperty(value = "软件版本")
    private String ncVer;

    @ApiModelProperty(value = "焊接类型(1.连续焊，2.组对点）")
    private Integer solderingType;
}
