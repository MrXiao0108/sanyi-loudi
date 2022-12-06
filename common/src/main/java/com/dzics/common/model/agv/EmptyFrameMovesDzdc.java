package com.dzics.common.model.agv;

import com.baomidou.mybatisplus.annotation.TableField;
import com.dzics.common.model.custom.StartWokeOrderMooM;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 空料框移动
 */
@Data
public class EmptyFrameMovesDzdc {


    /**
     * 请求参数：String  字段: basketType
     * 料框种类 A B C
     */
    @ApiModelProperty(value = "小车 A B C", required = true)
    private String basketType;
    /**
     * 请求参数：String  字段: palletType
     * <p>
     * 料框 满料拖出  1
     * 料框 空料拖出  2
     * 料框 青料拖出  3
     * 料框 请求毛坯  4
     * 料框 AGV 到位  5
     * 料框 AGV 离开  6
     * 料框 空料推入   7
     */
    @ApiModelProperty(value = "料框 满料拖出  1 *" +
            "料框 空料拖出  2 *" +
            "料框 清料拖出  3" +
            "料框 请求毛坯  4 **" +
            "料框 AGV 到位  5" +
            "料框 AGV 离开  6" +
            "料框 空料推入  7 *", required = true)
    private String palletType;

    /**
     * 大正订单号
     */
    @ApiModelProperty(value = "大正订单号", required = true)
    private String orderCode;

    /**
     * 大正订产线号
     */
    @ApiModelProperty(value = "大正订产线号", required = true)
    private String lineNo;

    /**
     * 物料数量
     */
    @ApiModelProperty(value = "物料数量 请求MOM工序间配送 的时候传递 默认传递0",required = true)
    private Integer quantity=0;

    /**
     * 料框编码,下料点移出满料框 palletType 为1时必填
     */
    @ApiModelProperty("料框编码,下料点移出满料框 palletType为1时必填")
    private String palletNo;

    /**
     * 物料二维码
     */
    @ApiModelProperty("产品二维码")
    private List<String>  serialNos;

    @ApiModelProperty("物料编码")
    private String syProductNo;

    @JsonIgnore
    private String groupId;

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @JsonIgnore
    private String innerGroupId;
    @JsonIgnore
    private String deviceType;
    /**
     * 料点模式
     * 料点模式, NG （NG物料） TL (退库) ,SL (上料)
     */
    @JsonIgnore
    private String pointModel;
    /**
     * 上料点编码
     */
    @JsonIgnore
    private String externalCode;
    /**
     * 订单号
     */
    @JsonIgnore
    private String wiporderno;

    /**
     * 机器人上发订单号
     */
    private String robotWipOrderNo;

    /**
     * 订单ID
     */
    @JsonIgnore
    private String proTaskOrderId;

    @JsonIgnore
    private StartWokeOrderMooM wokeOrderMooM;
}
