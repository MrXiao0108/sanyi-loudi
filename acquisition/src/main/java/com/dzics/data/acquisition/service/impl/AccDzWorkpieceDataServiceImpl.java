package com.dzics.data.acquisition.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.enums.CmdStateClassification;
import com.dzics.common.model.constant.MomProgressStatus;
import com.dzics.common.model.custom.CmdTcp;
import com.dzics.common.model.custom.DzTcpDateID;
import com.dzics.common.model.custom.OrderIdLineId;
import com.dzics.common.model.custom.RabbitmqMessage;
import com.dzics.common.model.entity.DzProduct;
import com.dzics.common.model.entity.DzWorkpieceData;
import com.dzics.common.model.entity.MonOrder;
import com.dzics.common.service.DzWorkpieceDataService;
import com.dzics.common.service.MomOrderService;
import com.dzics.common.util.DateUtil;
import com.dzics.data.acquisition.service.AccDzWorkpieceDataService;
import com.dzics.data.acquisition.service.AccqDzProductService;
import com.dzics.data.acquisition.service.CacheService;
import com.dzics.data.acquisition.util.TcpStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author ZhangChengJun
 * Date 2021/4/7.
 * @since
 */
@Slf4j
@Service
public class AccDzWorkpieceDataServiceImpl implements AccDzWorkpieceDataService {
    @Autowired
    private DzWorkpieceDataService dzWorkpieceDataService;
    @Autowired
    private TcpStringUtil tcpStringUtil;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private MomOrderService momOrderService;
    @Autowired
    private AccqDzProductService dzProductService;


    /**
     * {"MessageId":"5541cab4-a440-497c-b048-f80929f84251","QueueName":"dzics-dev-gather-v1-checkout-equipment","ClientId":"DZROBOT","OrderCode":"DZ-1875","LineNo":"1","DeviceType":"3","DeviceCode":"01","Message":"A809|[9f709fea3d994f9c8ea0154ee00c4f65,1017,4,A1,0,5,8952.071,0,7415.005,0,3467.137,0,68.565,1,9999.999,0]","Timestamp":"2022-01-05 10:12:37.4722"}
     * {"MessageId":"5541cab4-a440-497c-b048-f80929f84251","QueueName":"dzics-dev-gather-v1-checkout-equipment","ClientId":"DZROBOT","OrderCode":"DZ-1882","LineNo":"1","DeviceType":"2","DeviceCode":"01","Message":"A809|[9f709fea3d994f9c8ea0154ee00c4f65,1030,4,0,4,8952.071,0,7415.005,0,3467.137,0,68.565,1]","Timestamp":"2021-02-08 17:21:37.4722"}
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * {"MessageId":"5541cab4-a440-497c-b048-f80929f84251","QueueName":"dzics-dev-gather-v1-checkout-equipment",
     * "ClientId":"DZROBOT","OrderCode":"DZ-1882","LineNo":"1","DeviceType":"2","DeviceCode":"01",
     * "Message":"A809|[9f709fea3d994f9c8ea0154ee00c4f65,
     * 1030,
     * 4,
     * 0
     * ,4
     * ,8952.071,0,
     * 7415.005,0,
     * 3467.137,0,
     * 68.565,1]",
     * "Timestamp":"2021-02-08 17:21:37.4722"}
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * 产品条码，产品ID，工位编号，机床编号，总状态(0=NG,1=OK,100)，检测项数量，检测值1，检测1状态，检测值2，检测2状态，检测值3，检测3状态，检测值4，检测4状态
     * A809|[4c1e55c02ca04480975b9b0d4bb4a79f,1030,2,0,4,5835.125,1,6922.305,1,1744.856,0,8869.579,0]
     *
     * @param cmd
     */
    @Override
    public synchronized DzWorkpieceData queueCheckoutEquipment(RabbitmqMessage cmd) {
        log.debug("检测设备数据：{}", cmd);
        Map<String, Object> map = tcpStringUtil.analysisCmdV2(cmd);
        if (CollectionUtils.isNotEmpty(map)) {
//            底层设备上传时间时间
            Long senDate = (Long) map.get(CmdStateClassification.DATA_STATE_TIME.getCode());
//            分类唯一属性值
            DzTcpDateID tcpDateId = (DzTcpDateID) map.get(CmdStateClassification.TCP_ID.getCode());
            if (tcpDateId == null) {
                log.error("分类唯一属性值不存在：DzTcpDateID：{}", tcpDateId);
                return null;
            }
            String lineNum = tcpDateId.getProductionLineNumber();
            String deviceType = tcpDateId.getDeviceType();
            String deviceNum = tcpDateId.getDeviceNumber();
            String orderNumber = tcpDateId.getOrderNumber();
            String lineType = getLintType(orderNumber);
            List<CmdTcp> cmdTcps = (List<CmdTcp>) map.get(CmdStateClassification.TCP_CHECK_EQMENT.getCode());
            if (CollectionUtils.isNotEmpty(cmdTcps)) {
                CmdTcp cmdTcp = cmdTcps.get(0);
                String deviceItemValue = org.apache.commons.lang3.StringUtils.strip(cmdTcp.getDeviceItemValue(), "[]");
                String tcpValue = cmdTcp.getTcpValue();
                if (!StringUtils.isEmpty(deviceItemValue) && !StringUtils.isEmpty(tcpValue)) {
                    String[] split = deviceItemValue.split(",");
                    if (split.length > 6) {
                        String producBarcodeX = split[0];
                        String name = "";
                        String productNoSave = "";
                        String producBarcode = producBarcodeX;
                        String productNo = split[1]; // 之前是产品编号 1030 三一需求变更，是订单号
                        String workNumber = split[2];
                        String machineNumber = split[3];
                        Integer allState = Integer.valueOf(split[4]);
                        Integer itemNumber = Integer.valueOf(split[5]);
                        Date dateCj = new Date(senDate);
                        LocalDate nowLocalDate = DateUtil.dataToLocalDate(dateCj);
                        DzWorkpieceData dzWorkpieceData = new DzWorkpieceData();
//                        //如果接收到的是空码
//                        if(producBarcodeX==null || "".equals(producBarcodeX)){
//                            log.error("处理 检测设备数据异常：{},检测设备二维码为空：{}", cmd,producBarcodeX);
//                            return null;
//                        }
                        if (producBarcodeX.contains("_")) {
                            String[] splitSy = producBarcodeX.split("_");
                            int i = producBarcodeX.indexOf("_")+1;
                            String qrCode = producBarcodeX.substring(i, producBarcodeX.length());
//                            if (splitSy.length >= 2) {
                            name = splitSy[0].trim();
                            producBarcode = qrCode;
                            productNoSave = dzProductService.getNameAndOrder(name, lineType);
                            dzWorkpieceData.setProductNo(productNoSave);
                            dzWorkpieceData.setName(name);
//                            } else if (splitSy.length == 1) {
//                                name = splitSy[0].trim();
//                                producBarcode = "";
//                                productNoSave = dzProductService.getNameAndOrder(name, lineType);
//                                dzWorkpieceData.setProductNo(productNoSave);
//                                dzWorkpieceData.setName(name);
//                            }
                        } else {
                            log.warn("产品检测数据的二维码中未携带当前在做的产品数据:{}", JSONObject.toJSONString(cmd));
                            OrderIdLineId orderIdLineId = cacheService.getOrderNoLineNoId(orderNumber, lineNum);
                            if (orderIdLineId != null) {
                                MonOrder monOrder = momOrderService.getOrderOperationResult(orderIdLineId.getOrderId(), orderIdLineId.getLineId(), MomProgressStatus.OperationResultLoading);
                                if (monOrder != null) {
                                    DzProduct dzProduct = dzProductService.getById(monOrder.getProductId());
                                    dzWorkpieceData.setProductNo(dzProduct.getProductNo());
                                    dzWorkpieceData.setName(dzProduct.getProductName());
                                } else {
                                    log.error("当前生产的订单不存在: orderNO: {},lineNO :{}", orderNumber, lineNum);
                                    dzWorkpieceData.setProductNo("unknown");
                                    dzWorkpieceData.setName("unknown");
                                }
                            } else {
                                log.error("订单产线不存在：orderNO: {},lineNO :{}", orderNumber, lineNum);
                                dzWorkpieceData.setProductNo("unknown");
                                dzWorkpieceData.setName("unknown");
                            }
                        }
                        dzWorkpieceData.setEquipmentNo(deviceNum);
                        dzWorkpieceData.setEquipmentType(Integer.valueOf(deviceType));
                        dzWorkpieceData.setDate(nowLocalDate);
                        dzWorkpieceData.setCheckMonth(nowLocalDate.toString().substring(0, 7));
                        dzWorkpieceData.setDetectorTime(dateCj);
                        dzWorkpieceData.setCreateTime(new Date());
                        dzWorkpieceData.setProducBarcode(producBarcode);

                        dzWorkpieceData.setWorkNumber(workNumber);
                        dzWorkpieceData.setMachineNumber(machineNumber);
                        dzWorkpieceData.setOrderNo(orderNumber);
                        dzWorkpieceData.setLineNo(lineNum);
                        dzWorkpieceData.setOutOk(allState);
                        Integer falg = 6;
                        for (int i = 0; i < itemNumber; i++) {
                            String dataVal = split[falg];
                            falg = falg + 1;
                            Integer isQualified = Integer.valueOf(split[falg]);
                            falg = falg + 1;
                            if (i == 0) {
                                dzWorkpieceData.setDetect01(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk01(isQualified);
                            }
                            if (i == 1) {
                                dzWorkpieceData.setDetect02(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk02(isQualified);
                            }
                            if (i == 2) {
                                dzWorkpieceData.setDetect03(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk03(isQualified);
                            }
                            if (i == 3) {
                                dzWorkpieceData.setDetect04(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk04(isQualified);
                            }
                            if (i == 4) {
                                dzWorkpieceData.setDetect05(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk05(isQualified);
                            }
                            if (i == 5) {
                                dzWorkpieceData.setDetect06(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk06(isQualified);
                            }
                            if (i == 6) {
                                dzWorkpieceData.setDetect07(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk07(isQualified);
                            }
                            if (i == 7) {
                                dzWorkpieceData.setDetect08(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk08(isQualified);
                            }
                            if (i == 8) {
                                dzWorkpieceData.setDetect09(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk09(isQualified);
                            }
                            if (i == 9) {
                                dzWorkpieceData.setDetect10(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk10(isQualified);
                            }
                            if (i == 10) {
                                dzWorkpieceData.setDetect11(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk11(isQualified);
                            }
                            if (i == 11) {
                                dzWorkpieceData.setDetect12(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk12(isQualified);
                            }
                            if (i == 12) {
                                dzWorkpieceData.setDetect13(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk13(isQualified);
                            }
                            if (i == 13) {
                                dzWorkpieceData.setDetect14(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk14(isQualified);
                            }
                            if (i == 14) {
                                dzWorkpieceData.setDetect15(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk15(isQualified);
                            }
                            if (i == 15) {
                                dzWorkpieceData.setDetect16(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk16(isQualified);
                            }
                            if (i == 16) {
                                dzWorkpieceData.setDetect17(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk17(isQualified);
                            }
                            if (i == 17) {
                                dzWorkpieceData.setDetect18(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk18(isQualified);
                            }
                            if (i == 18) {
                                dzWorkpieceData.setDetect19(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk19(isQualified);
                            }
                            if (i == 19) {
                                dzWorkpieceData.setDetect20(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk20(isQualified);
                            }
                            if (i == 20) {
                                dzWorkpieceData.setDetect21(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk21(isQualified);
                            }
                            if (i == 21) {
                                dzWorkpieceData.setDetect22(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk22(isQualified);
                            }
                            if (i == 22) {
                                dzWorkpieceData.setDetect23(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk23(isQualified);
                            }
                            if (i == 23) {
                                dzWorkpieceData.setDetect24(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk24(isQualified);
                            }
                            if (i == 24) {
                                dzWorkpieceData.setDetect25(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk25(isQualified);
                            }
                            if (i == 25) {
                                dzWorkpieceData.setDetect26(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk26(isQualified);
                            }
                            if (i == 26) {
                                dzWorkpieceData.setDetect27(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk27(isQualified);
                            }
                            if (i == 27) {
                                dzWorkpieceData.setDetect28(new BigDecimal(dataVal));
                                dzWorkpieceData.setOutOk28(isQualified);
                            }
                        }
                        dzWorkpieceDataService.save(dzWorkpieceData);
                        return dzWorkpieceData;
                    } else {
                        log.error("检测数据数组长度小于5：split：{}", split.toString());
                    }
                } else {
                    log.warn("指令数据信息无内容：device_item_value：{}，tcp_value：{}", deviceItemValue, tcpValue);
                }
            } else {
                log.warn("检测数据指令值无数据：cmdTcps：{}", cmdTcps);
            }
        } else {
            log.warn("处理检测设备数据解析Map为为空：data：{}", map);
        }
        return null;
    }

    /**
     * 产线类型(2米活塞杆=2HSG，3米活塞杆=3HSG，2米缸筒=2GT，3米钢筒=3GT)
     *
     * @param orderNumber
     * @return
     */
    private String getLintType(String orderNumber) {
        if ("DZ-1871".equals(orderNumber) || "DZ-1872".equals(orderNumber) || "DZ-1873".equals(orderNumber) || "DZ-1874".equals(orderNumber) || "DZ-1875".equals(orderNumber)
                || "DZ-1876".equals(orderNumber) || "DZ-1877".equals(orderNumber) || "DZ-1955".equals(orderNumber)) {
            return "2MHSG";
        } else if ("DZ-1878".equals(orderNumber) || "DZ-1879".equals(orderNumber) || "DZ-1880".equals(orderNumber) || "DZ-1956".equals(orderNumber)) {
            return "3MHSG";
        } else if ("DZ-1887".equals(orderNumber) || "DZ-1888".equals(orderNumber) || "DZ-1889".equals(orderNumber)) {
            return "2MGT";
        } else if ("DZ-1890".equals(orderNumber) || "DZ-1891".equals(orderNumber)) {
            return "3MGT";
        } else {
            return "未知";
        }
    }

    /**
     * Q,6,1300,DZ-1887,1,W72B-2-2110030080,123.35,1
     *
     * @param split
     * @return
     */
    @Override
    public synchronized DzWorkpieceData maerbiao(String[] split) {
        try {
            String orderNo = split[3];
            String lineNo = split[4];
            String qrCode = "";
            if(split[5].contains("_")==true){
                qrCode = split[5].split("_")[1];
            }else{
                qrCode = split[5];
            }
            String value = split[6];
            String satate = split[7];
            Integer st = Integer.valueOf(satate);
            QueryWrapper<DzWorkpieceData> wp = new QueryWrapper<>();
            wp.eq("produc_barcode", qrCode);
            wp.eq("order_no", orderNo);
            wp.eq("line_no", lineNo);
            wp.orderByDesc("detector_time");
            List<DzWorkpieceData> dzWorkpieceDataList = dzWorkpieceDataService.list(wp);
            if (CollectionUtils.isNotEmpty(dzWorkpieceDataList)) {
                if ("DZ-1890".equals(orderNo) || "DZ-1891".equals(orderNo)) {
                    return getDzWorkpieceDataSave(orderNo, lineNo, qrCode, value, st);
                } else {
                    if (dzWorkpieceDataList.size() > 1) {
                        log.warn("码儿表检测数据存在多条记录：qrCode: {}", qrCode);
                    }
                    for (DzWorkpieceData workpieceData : dzWorkpieceDataList) {
//                检测台检测的 总状态 ，例如检测 了五项值 都 正常 ，总状态 肯定是OK。
                        Integer outOk = workpieceData.getOutOk();
//              如果 码儿表检测的第二十八项 是错误的， 则总状态 就是 错误的。
                        if (st == 0 && outOk == 1) {
                            workpieceData.setOutOk(st);
                        }
                        workpieceData.setOutOk28(st);
                        workpieceData.setDetect28(new BigDecimal(value));
                        workpieceData.setDetectorTime(new Date());
                        LocalDate now = LocalDate.now();
                        workpieceData.setDate(now);
                        workpieceData.setCheckMonth(now.toString().substring(0, 7));
                    }
                    dzWorkpieceDataService.updateBatchById(dzWorkpieceDataList);
                    return dzWorkpieceDataList.get(0);
                }
            } else {
                return getDzWorkpieceDataSave(orderNo, lineNo, qrCode, value, st);
            }
        } catch (Throwable throwable) {
            log.error("处理码儿表检测记录异常: {}", throwable.getMessage(), throwable);
            return null;
        }

    }

    private DzWorkpieceData getDzWorkpieceDataSave(String orderNo, String lineNo, String qrCode, String value, Integer st) {
        DzWorkpieceData dzWorkpieceData = new DzWorkpieceData();
        MonOrder momOrderNo = cacheService.getMomOrderNoProducBarcode(qrCode, orderNo, lineNo);
        if (momOrderNo == null) {
            dzWorkpieceData.setProductNo("unknown");
            dzWorkpieceData.setName("unknown");
        } else {
            DzProduct dzProduct = dzProductService.getById(momOrderNo.getProductId());
            if (dzProduct == null) {
                dzWorkpieceData.setProductNo("unknown");
                dzWorkpieceData.setName("unknown");
            } else {
                dzWorkpieceData.setProductNo(dzProduct.getProductNo());
//                dzWorkpieceData.setName(dzProduct.getProductName());
                dzWorkpieceData.setName(momOrderNo.getProductAlias());
            }

        }
        LocalDate now = LocalDate.now();
        Date date = new Date();
        dzWorkpieceData.setEquipmentNo("-1");
        dzWorkpieceData.setEquipmentType(-1);
        dzWorkpieceData.setDate(now);
        dzWorkpieceData.setCheckMonth(now.toString().substring(0, 7));
        dzWorkpieceData.setDetectorTime(date);
        dzWorkpieceData.setCreateTime(date);
        dzWorkpieceData.setProducBarcode(qrCode);
        dzWorkpieceData.setWorkNumber("workNumber");
        dzWorkpieceData.setMachineNumber("machineNumber");
        dzWorkpieceData.setOrderNo(orderNo);
        dzWorkpieceData.setLineNo(lineNo);
        dzWorkpieceData.setOutOk(st);
        dzWorkpieceData.setOutOk28(st);
        dzWorkpieceData.setDetect28(new BigDecimal(value));
        dzWorkpieceDataService.save(dzWorkpieceData);
        return dzWorkpieceData;
    }
}
