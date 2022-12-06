package com.dzics.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author NeverEnd
 * @since 2021-07-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_data_collection")
@ApiModel(value = "DzDataCollection对象", description = "")
public class DzDataCollection implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "设备id")
    @TableField("device_id")
    private Long deviceId;

    @ApiModelProperty("气体流量")
    @TableField("B814")
    private String B814;

    @ApiModelProperty("气体流量更新时间")
    @TableField("db814")
    private Date db814;

    @ApiModelProperty("安全门状态")
    @TableField("S561")
    private String s561;

    @ApiModelProperty(value = "报警信息")
    @TableField("W101")
    private String w101;

    @ApiModelProperty(value = "系统型号")
    @TableField("B401")
    private String b401;

    @ApiModelProperty(value = "序列号")
    @TableField("B402")
    private String b402;

    @ApiModelProperty(value = "软件版本")
    @TableField("B403")
    private String b403;

    @ApiModelProperty(value = "伺服轴数")
    @TableField("B404")
    private String b404;

    @ApiModelProperty(value = "主轴数")
    @TableField("B405")
    private String b405;

    @ApiModelProperty(value = "轴名称")
    @TableField("B406")
    private String b406;

    @ApiModelProperty(value = "绝对坐标")
    @TableField("B501")
    private String b501;

    @ApiModelProperty(value = "机械坐标")
    @TableField("B502")
    private String b502;

    @ApiModelProperty(value = "工件坐标")
    @TableField("B503")
    private String b503;

    @ApiModelProperty(value = "相对坐标")
    @TableField("B504")
    private String b504;

    @ApiModelProperty(value = "剩余距离")
    @TableField("B505")
    private String b505;

    @ApiModelProperty(value = "机床时钟")
    @TableField("B521")
    private String b521;

    @ApiModelProperty(value = "在线时间")
    @TableField("B522")
    private String b522;

    @ApiModelProperty(value = "运行时间")
    @TableField("B523")
    private String b523;

    @ApiModelProperty(value = "启动时间")
    @TableField("B524")
    private String b524;

    @ApiModelProperty(value = "预估时间")
    @TableField("B525")
    private String b525;

    @ApiModelProperty(value = "切削时间")
    @TableField("B526")
    private String b526;

    @ApiModelProperty(value = "加工节拍")
    @TableField("B527")
    private String b527;

    @ApiModelProperty(value = "进给速度")
    @TableField("B541")
    private String b541;

    @ApiModelProperty(value = "进给速度 FA")
    @TableField("B542")
    private String b542;

    @ApiModelProperty(value = "进给速度 FM")
    @TableField("B543")
    private String b543;

    @ApiModelProperty(value = "进给速度 FS")
    @TableField("B544")
    private String b544;

    @ApiModelProperty(value = "进给速度 FC")
    @TableField("B545")
    private String b545;

    @ApiModelProperty(value = "进给速度 FE")
    @TableField("B546")
    private String b546;

    @ApiModelProperty(value = "TCP速度")
    @TableField("B550")
    private String b550;

    @ApiModelProperty(value = "主轴转速")
    @TableField("B551")
    private String b551;

    @ApiModelProperty(value = "进给倍率")
    @TableField("B552")
    private String b552;

    @ApiModelProperty(value = "主轴倍率")
    @TableField("B553")
    private String b553;

    @ApiModelProperty(value = "切削速度")
    @TableField("B554")
    private String b554;

    @ApiModelProperty(value = "切削指定速度")
    @TableField("B555")
    private String b555;

    @ApiModelProperty(value = "主轴指定速度")
    @TableField("B556")
    private String b556;

    @ApiModelProperty(value = "主轴最高速度")
    @TableField("B557")
    private String b557;

    @ApiModelProperty(value = "连接状态")
    @TableField("B561")
    private String b561;

    @ApiModelProperty(value = "运行状态")
    @TableField("B562")
    private String b562;

    @ApiModelProperty(value = "手轮状态")
    @TableField("B563")
    private String b563;

    @ApiModelProperty(value = "T/M模式选择")
    @TableField("B564")
    private String b564;

    @ApiModelProperty(value = "自动/手动模式选择")
    @TableField("B565")
    private String b565;

    @ApiModelProperty(value = "轴移动/停留")
    @TableField("B566")
    private String b566;

    @ApiModelProperty(value = "M,S,T,B函数状态")
    @TableField("B567")
    private String b567;

    @ApiModelProperty(value = "急停状态")
    @TableField("B568")
    private String b568;

    @ApiModelProperty(value = "告警状态")
    @TableField("B569")
    private String b569;

    @ApiModelProperty(value = "程序编辑状态")
    @TableField("B570")
    private String b570;

    @ApiModelProperty(value = "切削模式")
    @TableField("B571")
    private String b571;

    @ApiModelProperty(value = "工作状态")
    @TableField("B572")
    private String b572;

    @ApiModelProperty(value = "程序运行状态")
    @TableField("B591")
    private String b591;

    @ApiModelProperty(value = "主程序号")
    @TableField("B592")
    private String b592;

    @ApiModelProperty(value = "主程序注释")
    @TableField("B593")
    private String b593;

    @ApiModelProperty(value = "当前程序号")
    @TableField("B594")
    private String b594;

    @ApiModelProperty(value = "当前程序注释")
    @TableField("B595")
    private String b595;

    @ApiModelProperty(value = "程序行号")
    @TableField("B596")
    private String b596;

    @ApiModelProperty(value = "当前程序块")
    @TableField("B597")
    private String b597;

    @ApiModelProperty(value = "刀具号")
    @TableField("B651")
    private String b651;

    @ApiModelProperty(value = "M-CODE计数")
    @TableField("B691")
    private String b691;

    @ApiModelProperty(value = "加工计数")
    @TableField("B692")
    private String b692;

    @ApiModelProperty(value = "生产总量")
    @TableField("B693")
    private String b693;

    @ApiModelProperty(value = "设定工件计数")
    @TableField("B694")
    private String b694;

    @ApiModelProperty(value = "主轴负载")
    @TableField("B801")
    private String b801;

    @ApiModelProperty(value = "成品数量")
    @TableField("B802")
    private String b802;

    @ApiModelProperty(value = "刀具形状数据")
    @TableField("B803")
    private String b803;

    @ApiModelProperty(value = "刀具寿命数据")
    @TableField("B804")
    private String b804;

    @ApiModelProperty(value = "告警信息")
    @TableField("B805")
    private String b805;

    @ApiModelProperty(value = "断刀检测")
    @TableField("B806")
    private String b806;

    @ApiModelProperty(value = "伺服负载（X/Z轴）")
    @TableField("B807")
    private String b807;

    @ApiModelProperty(value = "告警履历")
    @TableField("B808")
    private String b808;

    @ApiModelProperty(value = "刀具告警代码")
    @TableField("B809")
    private String b809;

    @ApiModelProperty(value = "主轴温度")
    @TableField("B810")
    private String b810;

    @ApiModelProperty(value = "伺服温度")
    @TableField("B811")
    private String b811;

    @ApiModelProperty(value = "数量清零")
    @TableField("A148")
    private String a148;

    @ApiModelProperty(value = "关节坐标")
    @TableField("A501")
    private String a501;

    @ApiModelProperty(value = "世界坐标")
    @TableField("A502")
    private String a502;

    @ApiModelProperty(value = "工具坐标")
    @TableField("A503")
    private String a503;

    @ApiModelProperty(value = "工件坐标")
    @TableField("A504")
    private String a504;

    @ApiModelProperty(value = "速度倍率")
    @TableField("A521")
    private String a521;

    @ApiModelProperty(value = "运行时间")
    @TableField("A541")
    private String a541;

    @ApiModelProperty(value = "连接状态")
    @TableField("A561")
    private String a561;

    @ApiModelProperty(value = "操作模式")
    @TableField("A562")
    private String a562;

    @ApiModelProperty(value = "运行状态")
    @TableField("A563")
    private String a563;

    @ApiModelProperty(value = "轴移动/停留")
    @TableField("A564")
    private String a564;

    @ApiModelProperty(value = "急停状态")
    @TableField("A565")
    private String a565;

    @ApiModelProperty(value = "告警状态")
    @TableField("A566")
    private String a566;

    @ApiModelProperty(value = "主程序名称")
    @TableField("A591")
    private String a591;

    @ApiModelProperty(value = "主程序注释")
    @TableField("A592")
    private String a592;

    @ApiModelProperty(value = "当前程序名称")
    @TableField("A593")
    private String a593;

    @ApiModelProperty(value = "当前程序注释")
    @TableField("A594")
    private String a594;

    @ApiModelProperty(value = "程序行号")
    @TableField("A595")
    private String a595;

    @ApiModelProperty(value = "夹具状态")
    @TableField("A620")
    private String a620;

    @ApiModelProperty(value = "错误代码")
    @TableField("A801")
    private String a801;

    @ApiModelProperty(value = "加工节拍")
    @TableField("A802")
    private String a802;

    @ApiModelProperty(value = "成品数量")
    @TableField("A803")
    private String a803;

    @ApiModelProperty(value = "程序行号")
    @TableField("A804")
    private String a804;

    @ApiModelProperty(value = "毛坯数量")
    @TableField("A805")
    private String a805;

    @ApiModelProperty(value = "合格品数量")
    @TableField("A806")
    private String a806;

    @ApiModelProperty(value = "基恩士检测数据")
    @TableField("A807")
    private String a807;

    @ApiModelProperty(value = "运行信息")
    @TableField("A808")
    private String a808;

    @ApiModelProperty(value = "探针检测数据")
    @TableField("A809")
    private String a809;

    @ApiModelProperty(value = "计数脉冲")
    @TableField("A810")
    private String a810;

    @ApiModelProperty(value = "满料数据")
    @TableField("A811")
    private String a811;

    @ApiModelProperty(value = "停机次数")
    @TableField("A812")
    private String a812;

    @ApiModelProperty(value = "日志信息")
    @TableField("A813")
    private String a813;

    @ApiModelProperty(value = "累计数量")
    @TableField("A814")
    private String a814;

    @ApiModelProperty(value = "扫码追踪")
    @TableField("A815")
    private String a815;

    @ApiModelProperty(value = "二维码记录")
    @TableField("A816")
    private String a816;

    @TableField("ds561")
    @ApiModelProperty("安全门更新状态时间")
    private Date ds561;

    @ApiModelProperty(value = "报警信息时间")
    @TableField("dw101")
    private Date dw101;

    @ApiModelProperty(value = "日期:系统型号")
    @TableField("db401")
    private Date db401;

    @ApiModelProperty(value = "日期:序列号")
    @TableField("db402")
    private Date db402;

    @ApiModelProperty(value = "日期:软件版本")
    @TableField("db403")
    private Date db403;

    @ApiModelProperty(value = "日期:伺服轴数")
    @TableField("db404")
    private Date db404;

    @ApiModelProperty(value = "日期:主轴数")
    @TableField("db405")
    private Date db405;

    @ApiModelProperty(value = "日期:轴名称")
    @TableField("db406")
    private Date db406;

    @ApiModelProperty(value = "日期:绝对坐标")
    @TableField("db501")
    private Date db501;

    @ApiModelProperty(value = "日期:机械坐标")
    @TableField("db502")
    private Date db502;

    @ApiModelProperty(value = "日期:工件坐标")
    @TableField("db503")
    private Date db503;

    @ApiModelProperty(value = "日期:相对坐标")
    @TableField("db504")
    private Date db504;

    @ApiModelProperty(value = "日期:剩余距离")
    @TableField("db505")
    private Date db505;

    @ApiModelProperty(value = "日期:机床时钟")
    @TableField("db521")
    private Date db521;

    @ApiModelProperty(value = "日期:在线时间")
    @TableField("db522")
    private Date db522;

    @ApiModelProperty(value = "日期:运行时间")
    @TableField("db523")
    private Date db523;

    @ApiModelProperty(value = "日期:启动时间")
    @TableField("db524")
    private Date db524;

    @ApiModelProperty(value = "日期:预估时间")
    @TableField("db525")
    private Date db525;

    @ApiModelProperty(value = "日期:切削时间")
    @TableField("db526")
    private Date db526;

    @ApiModelProperty(value = "日期:加工节拍")
    @TableField("db527")
    private Date db527;

    @ApiModelProperty(value = "日期:进给速度")
    @TableField("db541")
    private Date db541;

    @ApiModelProperty(value = "日期:进给速度 FA")
    @TableField("db542")
    private Date db542;

    @ApiModelProperty(value = "日期:进给速度 FM")
    @TableField("db543")
    private Date db543;

    @ApiModelProperty(value = "日期:进给速度 FS")
    @TableField("db544")
    private Date db544;

    @ApiModelProperty(value = "日期:进给速度 FC")
    @TableField("db545")
    private Date db545;

    @ApiModelProperty(value = "日期:进给速度 FE")
    @TableField("db546")
    private Date db546;

    @ApiModelProperty(value = "日期:TCP速度")
    @TableField("db550")
    private Date db550;

    @ApiModelProperty(value = "日期:主轴转速")
    @TableField("db551")
    private Date db551;

    @ApiModelProperty(value = "日期:进给倍率")
    @TableField("db552")
    private Date db552;

    @ApiModelProperty(value = "日期:主轴倍率")
    @TableField("db553")
    private Date db553;

    @ApiModelProperty(value = "日期:切削速度")
    @TableField("db554")
    private Date db554;

    @ApiModelProperty(value = "日期:切削指定速度")
    @TableField("db555")
    private Date db555;

    @ApiModelProperty(value = "日期:主轴指定速度")
    @TableField("db556")
    private Date db556;

    @ApiModelProperty(value = "日期:主轴最高速度")
    @TableField("db557")
    private Date db557;

    @ApiModelProperty(value = "日期:连接状态")
    @TableField("db561")
    private Date db561;

    @ApiModelProperty(value = "日期:运行状态")
    @TableField("db562")
    private Date db562;

    @ApiModelProperty(value = "日期:手轮状态")
    @TableField("db563")
    private Date db563;

    @ApiModelProperty(value = "日期:T/M模式选择")
    @TableField("db564")
    private Date db564;

    @ApiModelProperty(value = "日期:自动/手动模式选择")
    @TableField("db565")
    private Date db565;

    @ApiModelProperty(value = "日期:轴移动/停留")
    @TableField("db566")
    private Date db566;

    @ApiModelProperty(value = "日期:M,S,T,B函数状态")
    @TableField("db567")
    private Date db567;

    @ApiModelProperty(value = "日期:急停状态")
    @TableField("db568")
    private Date db568;

    @ApiModelProperty(value = "日期:告警状态")
    @TableField("db569")
    private Date db569;

    @ApiModelProperty(value = "日期:程序编辑状态")
    @TableField("db570")
    private Date db570;

    @ApiModelProperty(value = "日期:切削模式")
    @TableField("db571")
    private Date db571;

    @ApiModelProperty(value = "日期:工作状态")
    @TableField("db572")
    private Date db572;

    @ApiModelProperty(value = "日期:程序运行状态")
    @TableField("db591")
    private Date db591;

    @ApiModelProperty(value = "日期:主程序号")
    @TableField("db592")
    private Date db592;

    @ApiModelProperty(value = "日期:主程序注释")
    @TableField("db593")
    private Date db593;

    @ApiModelProperty(value = "日期:当前程序号")
    @TableField("db594")
    private Date db594;

    @ApiModelProperty(value = "日期:当前程序注释")
    @TableField("db595")
    private Date db595;

    @ApiModelProperty(value = "日期:程序行号")
    @TableField("db596")
    private Date db596;

    @ApiModelProperty(value = "日期:当前程序块")
    @TableField("db597")
    private Date db597;

    @ApiModelProperty(value = "日期:刀具号")
    @TableField("db651")
    private Date db651;

    @ApiModelProperty(value = "日期:M-CODE计数")
    @TableField("db691")
    private Date db691;

    @ApiModelProperty(value = "日期:加工计数")
    @TableField("db692")
    private Date db692;

    @ApiModelProperty(value = "日期:生产总量")
    @TableField("db693")
    private Date db693;

    @ApiModelProperty(value = "日期:设定工件计数")
    @TableField("db694")
    private Date db694;

    @ApiModelProperty(value = "日期:主轴负载")
    @TableField("db801")
    private Date db801;

    @ApiModelProperty(value = "日期:成品数量")
    @TableField("db802")
    private Date db802;

    @ApiModelProperty(value = "日期:刀具形状数据")
    @TableField("db803")
    private Date db803;

    @ApiModelProperty(value = "日期:刀具寿命数据")
    @TableField("db804")
    private Date db804;

    @ApiModelProperty(value = "日期:告警信息")
    @TableField("db805")
    private Date db805;

    @ApiModelProperty(value = "日期:断刀检测")
    @TableField("db806")
    private Date db806;

    @ApiModelProperty(value = "日期:伺服负载（X/Z轴）")
    @TableField("db807")
    private Date db807;

    @ApiModelProperty(value = "日期:告警履历")
    @TableField("db808")
    private Date db808;

    @ApiModelProperty(value = "日期:刀具告警代码")
    @TableField("db809")
    private Date db809;

    @ApiModelProperty(value = "日期:主轴温度")
    @TableField("db810")
    private Date db810;

    @ApiModelProperty(value = "日期:伺服温度")
    @TableField("db811")
    private Date db811;

    @ApiModelProperty(value = "日期:数量清零")
    @TableField("da148")
    private Date da148;

    @ApiModelProperty(value = "日期:关节坐标")
    @TableField("da501")
    private Date da501;

    @ApiModelProperty(value = "日期:世界坐标")
    @TableField("da502")
    private Date da502;

    @ApiModelProperty(value = "日期:工具坐标")
    @TableField("da503")
    private Date da503;

    @ApiModelProperty(value = "日期:工件坐标")
    @TableField("da504")
    private Date da504;

    @ApiModelProperty(value = "日期:速度倍率")
    @TableField("da521")
    private Date da521;

    @ApiModelProperty(value = "日期:运行时间")
    @TableField("da541")
    private Date da541;

    @ApiModelProperty(value = "日期:连接状态")
    @TableField("da561")
    private Date da561;

    @ApiModelProperty(value = "日期:操作模式")
    @TableField("da562")
    private Date da562;

    @ApiModelProperty(value = "日期:运行状态")
    @TableField("da563")
    private Date da563;

    @ApiModelProperty(value = "日期:轴移动/停留")
    @TableField("da564")
    private Date da564;

    @ApiModelProperty(value = "日期:急停状态")
    @TableField("da565")
    private Date da565;

    @ApiModelProperty(value = "日期:告警状态")
    @TableField("da566")
    private Date da566;

    @ApiModelProperty(value = "日期:主程序名称")
    @TableField("da591")
    private Date da591;

    @ApiModelProperty(value = "日期:主程序注释")
    @TableField("da592")
    private Date da592;

    @ApiModelProperty(value = "日期:当前程序名称")
    @TableField("da593")
    private Date da593;

    @ApiModelProperty(value = "日期:当前程序注释")
    @TableField("da594")
    private Date da594;

    @ApiModelProperty(value = "日期:程序行号")
    @TableField("da595")
    private Date da595;

    @ApiModelProperty(value = "日期:夹具状态")
    @TableField("da620")
    private Date da620;

    @ApiModelProperty(value = "日期:错误代码")
    @TableField("da801")
    private Date da801;

    @ApiModelProperty(value = "日期:加工节拍")
    @TableField("da802")
    private Date da802;

    @ApiModelProperty(value = "日期:成品数量")
    @TableField("da803")
    private Date da803;

    @ApiModelProperty(value = "日期:程序行号")
    @TableField("da804")
    private Date da804;

    @ApiModelProperty(value = "日期:毛坯数量")
    @TableField("da805")
    private Date da805;

    @ApiModelProperty(value = "日期:合格品数量")
    @TableField("da806")
    private Date da806;

    @ApiModelProperty(value = "日期:基恩士检测数据")
    @TableField("da807")
    private Date da807;

    @ApiModelProperty(value = "日期:运行信息")
    @TableField("da808")
    private Date da808;

    @ApiModelProperty(value = "日期:探针检测数据")
    @TableField("da809")
    private Date da809;

    @ApiModelProperty(value = "日期:计数脉冲")
    @TableField("da810")
    private Date da810;

    @ApiModelProperty(value = "日期:满料数据")
    @TableField("da811")
    private Date da811;

    @ApiModelProperty(value = "日期:停机次数")
    @TableField("da812")
    private Date da812;

    @ApiModelProperty(value = "日期:日志信息")
    @TableField("da813")
    private Date da813;

    @ApiModelProperty(value = "日期:累计数量")
    @TableField("da814")
    private Date da814;

    @ApiModelProperty(value = "日期:扫码追踪")
    @TableField("da815")
    private Date da815;

    @ApiModelProperty(value = "日期:二维码记录")
    @TableField("da816")
    private Date da816;

    @ApiModelProperty(value = "删除状态(0-正常,1-已删除)")
    @TableField("del_flag")
    private Boolean delFlag;

    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


    @ApiModelProperty(value = "连接状态")
    @TableField("H561")
    private String h561;
    @ApiModelProperty(value = "运行状态")
    @TableField("H562")
    private String h562;
    @ApiModelProperty(value = "工作状态")
    @TableField("H563")
    private String h563;
    @ApiModelProperty(value = "急停状态")
    @TableField("H564")
    private String h564;
    @ApiModelProperty(value = "告警状态")
    @TableField("H565")
    private String h565;
    @ApiModelProperty(value = "自动/手动模式选择")
    @TableField("H566")
    private String h566;

    @ApiModelProperty("用电量")
    @TableField("H591")
    private String h591;

    @ApiModelProperty("感应器的输入电压 V")
    @TableField("H701")
    private String h701;

    @ApiModelProperty("感应器的输入电流 A")
    @TableField("H702")
    private String h702;

    @ApiModelProperty("输入电流频率Hz")
    @TableField("H703")
    private String h703;

    @ApiModelProperty("设定时间s")
    @TableField("H704")
    private String h704;

    @ApiModelProperty("实际时间s")
    @TableField("H705")
    private String h705;

    @ApiModelProperty(value = "移动速度  mm/s ")
    @TableField("H706")
    private String h706;
    @ApiModelProperty(value = "工件转速 Rad/min")
    @TableField("H707")
    private String h707;
    @ApiModelProperty(value = "冷却液温度 ℃")
    @TableField("H801")
    private String h801;

    @ApiModelProperty("冷却超温报警")
    @TableField("H802")
    private String h802;

    @ApiModelProperty("冷却低温报警")
    @TableField("H803")
    private String h803;


    @ApiModelProperty(value = "冷却液压力 MPa")
    @TableField("H804")
    private String h804;

    @ApiModelProperty(value = "冷却液流量 L/s")
    @TableField("H805")
    private String h805;

    @ApiModelProperty(value = "设定冷却时间s")
    @TableField("H806")
    private String h806;

    @ApiModelProperty(value = "实际冷却时间s")
    @TableField("H807")
    private String h807;


    @ApiModelProperty("报警信息")
    @TableField("H901")
    private String h901;

    @ApiModelProperty(value = "连接状态")
    @TableField("K561")
    private String k561;
    @ApiModelProperty(value = "运行状态")
    @TableField("K562")
    private String k562;
    @ApiModelProperty(value = "工作状态")
    @TableField("K563")
    private String k563;
    @ApiModelProperty(value = "急停状态")
    @TableField("K564")
    private String k564;
    @ApiModelProperty(value = "告警状态")
    @TableField("K565")
    private String k565;
    @ApiModelProperty(value = "自动/手动模式选择")
    @TableField("K566")
    private String k566;

    @ApiModelProperty("上辊行程")
    @TableField("K801")
    private String k801;

    @ApiModelProperty("上辊角度")
    @TableField("K802")
    private String k802;

    @ApiModelProperty(value = "压头上下位置")
    @TableField("K803")
    private String k803;
    @ApiModelProperty(value = "压头左右位置")
    @TableField("K804")
    private String k804;

    @ApiModelProperty(value = "日期:连接状态")
    @TableField("dh561")
    private Date dh561;
    @ApiModelProperty(value = "日期:运行状态")
    @TableField("dh562")
    private Date dh562;
    @ApiModelProperty(value = "日期:工作状态")
    @TableField("dh563")
    private Date dh563;
    @ApiModelProperty(value = "日期:急停状态")
    @TableField("dh564")
    private Date dh564;
    @ApiModelProperty(value = "日期:告警状态")
    @TableField("dh565")
    private Date dh565;
    @ApiModelProperty(value = "日期:自动/手动模式选择")
    @TableField("dh566")
    private Date dh566;

    @ApiModelProperty(value = "日期:连接状态")
    @TableField("dk561")
    private Date dk561;
    @ApiModelProperty(value = "日期:运行状态")
    @TableField("dk562")
    private Date dk562;
    @ApiModelProperty(value = "日期:工作状态")
    @TableField("dk563")
    private Date dk563;
    @ApiModelProperty(value = "日期:急停状态")
    @TableField("dk564")
    private Date dk564;
    @ApiModelProperty(value = "日期:告警状态")
    @TableField("dk565")
    private Date dk565;
    @ApiModelProperty(value = "日期:自动/手动模式选择")
    @TableField("dk566")
    private Date dk566;

    @ApiModelProperty(value = "待机状态:未待机/待机")
    @TableField("A567")
    private String a567;

    @ApiModelProperty(value = "日期:待机状态")
    @TableField("da567")
    private Date da567;

}
