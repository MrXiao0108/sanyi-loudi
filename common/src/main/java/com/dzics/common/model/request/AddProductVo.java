package com.dzics.common.model.request;

import com.baomidou.mybatisplus.annotation.TableField;
import com.dzics.common.model.response.ImgDo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.List;

@ApiModel("新增(修改)产品vo")
@Data
public class AddProductVo {
    @ApiModelProperty("唯一标识 主键(新增不填，修改必填)")
    private Long productId;

    @ApiModelProperty(value = "站点id",required = true)
    @NotNull(message = "请选择站点")
    private Long departId;

    @ApiModelProperty(value = "产品id",required = true)
    @NotEmpty(message = "请选择产品")
    @Pattern(regexp = "^[0-9]*$",message = "产品编号只能输入数字")
    private String productNo;

    @ApiModelProperty(value = "产品名称",required = true)
    @NotEmpty(message = "产品名称不能为空")
    private String productName;

    @ApiModelProperty(value = "产品图片",required = true)
    private List<ImgDo> pictureList;
    @ApiModelProperty(value = "产品介绍",required = true)
    private String remarks;

    @NotEmpty(message = "三一产品类别不能为空")
    @ApiModelProperty("三一产品类别")
    private String syCategory;

    @NotEmpty(message = "三一产品简码不能为空")
    @ApiModelProperty("三一产品简码")
    private String syProductAlias;

    @NotEmpty(message = "三一产品物料编码不能为空")
    @ApiModelProperty("三一产品物料编码")
    private String syProductNo;
    /**
     * 产品类型 对应产线制作的类型
     */
    @ApiModelProperty(value = "产线类型(2米活塞杆=2HSG，3米活塞杆=3HSG，2米缸筒=2GT，3米钢筒=3GT)")
    @TableField("line_type")
    @NotBlank(message = "产线产品类型必选")
    private String lineType;

    @ApiModelProperty(value = "组件物料集合", required = true)
    @NotNull(message = "请填写全部表单参数")
    List<MaterialVo> materialList;

    @ApiModelProperty("节拍")
    private BigDecimal frequency;
}
