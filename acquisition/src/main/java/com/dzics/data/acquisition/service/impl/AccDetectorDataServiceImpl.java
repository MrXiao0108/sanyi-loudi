package com.dzics.data.acquisition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.dao.DzProductDetectionTemplateMapper;
import com.dzics.common.dao.DzWorkpieceDataMapper;
import com.dzics.common.enums.DeviceSocketSendStatus;
import com.dzics.common.model.entity.DzDetectorData;
import com.dzics.common.model.entity.DzProductDetectionTemplate;
import com.dzics.common.model.entity.DzWorkpieceData;
import com.dzics.common.model.request.kb.GetOrderNoLineNo;
import com.dzics.common.model.response.GetDetectionOneDo;
import com.dzics.common.model.response.JCEquimentBase;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.productiontask.ProDetection;
import com.dzics.common.service.DzDetectorDataService;
import com.dzics.common.service.DzProductDetectionTemplateService;
import com.dzics.common.service.DzWorkpieceDataService;
import com.dzics.common.util.DateUtil;
import com.dzics.common.util.RedisKey;
import com.dzics.data.acquisition.service.AccDetectorDataService;
import com.dzics.data.acquisition.util.RedisUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author ZhangChengJun
 * Date 2021/2/10.
 * @since
 */
@Service
@Slf4j
public class AccDetectorDataServiceImpl implements AccDetectorDataService {
    @Autowired
    private DzDetectorDataService dzDetectorDataService;
    @Autowired
    private DzWorkpieceDataService workpieceDataService;
    @Autowired
    private DzProductDetectionTemplateService dzProductDetectionTemplateService;
    @Autowired
    private DzProductDetectionTemplateMapper dzProductDetectionTemplateMapper;
    @Autowired
    DzWorkpieceDataMapper dzWorkpieceDataMapper;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    private DateUtil dateUtil;


    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean saveDataList(List<DzDetectorData> dataList) {
        return dzDetectorDataService.saveBatch(dataList);
    }

    @Override
    public Result<JCEquimentBase<ProDetection<List<Map<String, Object>>>>> getDetectorData(String orderNo, String lineNo) {
        List<String> infoList = workpieceDataService.getNewestThreeDataId(orderNo, lineNo, 4);
        JCEquimentBase<ProDetection<List<Map<String, Object>>>> jcEquimentBase = new JCEquimentBase<>();
        if (CollectionUtils.isNotEmpty(infoList)) {
            List<Map<String, Object>> list = workpieceDataService.newestThreeData(infoList);
            if (!list.isEmpty()) {
                for (Map<String, Object> objectMap : list) {
                    Date detectorTime = (Date) objectMap.get("detectorTime");
                    String format = dateUtil.dateFormatToStingYmdHms(detectorTime);
                    objectMap.put("detectorTime", format);
                    for (int i = 1; i < 29; i++) {
                        String key = i < 10 ? "0" + i : i + "";
                        Object val = objectMap.get("out_ok" + key);
                        if (val != null && val.toString().equals("0")) {
                            objectMap.put("detect" + key, String.valueOf(objectMap.get("detect" + key)) + "::");
                        }
                    }
                }
                String productNo = list.get(0).get("productNo").toString();
                ProDetection<List<Map<String, Object>>> proDetection = new ProDetection<>();
                List<Map<String, Object>> templates = dzProductDetectionTemplateService.listProductNo(productNo, orderNo, lineNo);
                if (CollectionUtils.isEmpty(templates)) {
                    templates = dzProductDetectionTemplateService.getDefoutDetectionTemp();
                }
                proDetection.setTableColumn(templates);
                proDetection.setTableData(list);
                jcEquimentBase.setData(proDetection);
            }
        }
        jcEquimentBase.setType(DeviceSocketSendStatus.FOUR_PRODUCT_TEST_DATA.getInfo());
        Result<JCEquimentBase<ProDetection<List<Map<String, Object>>>>> ok = Result.ok(jcEquimentBase);
        return ok;
    }

    @Override
    public List<GetDetectionOneDo> getDetectionOne(GetOrderNoLineNo data) {
        //查询最近一个产品的检测记录
        PageHelper.startPage(1, 1);
        List<DzWorkpieceData> dzWorkpieceDatas = dzWorkpieceDataMapper.selectList(
                new QueryWrapper<DzWorkpieceData>()
                        .eq("order_no", data.getOrderNo())
                        .eq("line_no", data.getLineNo())
                        .orderByDesc("detector_time")
                        .select("product_no")
        );
        PageInfo<DzWorkpieceData> info = new PageInfo<>(dzWorkpieceDatas);
        List<DzWorkpieceData> list1 = info.getList();
        if (list1.size() == 0) {
            log.warn("三米缸筒看板订阅-单项检测项数据订阅-订单:{},产线:{},没有最新的产品检测数据", data.getOrderNo(), data.getLineNo());
            return new ArrayList<>();
        }
        //根据产品编号查询产品检测项
        String productNo = list1.get(0).getProductNo();
        //根据检测项 查询指定检测项的值和 检测结果  检测时间
        List<DzProductDetectionTemplate> templates = dzProductDetectionTemplateMapper.selectList(
                new QueryWrapper<DzProductDetectionTemplate>()
                        .eq("product_no", productNo)
                        .eq("is_show", 0)
                        .eq("order_no", data.getOrderNo())
                        .eq("line_no", data.getLineNo())
        );
        if (templates.size() == 0) {
            log.error("产品检测项单项检测值推送，产品没有绑定检测项,产品编号:{}", productNo);
            return null;
        }
        DzProductDetectionTemplate dzProductDetectionTemplate = templates.get(0);
        String tableColCon = dzProductDetectionTemplate.getTableColCon();//检测项name
        String tableColVal = dzProductDetectionTemplate.getTableColVal();//检测项 表格字段值
        String outOkVal = getOutOk(tableColVal);//获取检测结果字段名
        PageHelper.startPage(1, 9);
        List<GetDetectionOneDo> dataList = dzWorkpieceDataMapper.selectDataList(tableColVal, outOkVal, data.getOrderNo(), data.getLineNo());
        PageInfo<GetDetectionOneDo> mapPageInfo = new PageInfo(dataList);
        List<GetDetectionOneDo> list = mapPageInfo.getList();
        for (GetDetectionOneDo detectionOneDo : list) {
            detectionOneDo.setTableColCon(tableColCon);
        }
        //把检测字段名称  检测结果的字段名称存入缓存
        redisUtil.set(RedisKey.TABLE_COL_CON + data.getOrderNo() + data.getLineNo(), tableColCon);//检测字段名
        redisUtil.set(RedisKey.TABLE_COL_VAL + data.getOrderNo() + data.getLineNo(), tableColVal);//检测字段值
        redisUtil.set(RedisKey.OUT_OK_VAL + data.getOrderNo() + data.getLineNo(), outOkVal);//检测结果字段值
        //返回结果
        return list;
    }

    //根据检测项字段名  获取对应检测结果的字段名
    @Override
    public String getOutOk(String tableColVal) {
        String res = "";//检测结果字段名
        switch (tableColVal) {
            case "detect01":
                res = "out_ok01";
                break;
            case "detect02":
                res = "out_ok02";
                break;
            case "detect03":
                res = "out_ok03";
                break;
            case "detect04":
                res = "out_ok04";
                break;
            case "detect05":
                res = "out_ok05";
                break;
            case "detect06":
                res = "out_ok06";
                break;
            case "detect07":
                res = "out_ok07";
                break;
            case "detect08":
                res = "out_ok08";
                break;
            case "detect09":
                res = "out_ok09";
                break;
            case "detect10":
                res = "out_ok10";
                break;
            case "detect11":
                res = "out_ok11";
                break;
            case "detect12":
                res = "out_ok12";
                break;
            case "detect13":
                res = "out_ok13";
                break;
            case "detect14":
                res = "out_ok14";
                break;
            case "detect15":
                res = "out_ok15";
                break;
            case "detect16":
                res = "out_ok16";
                break;
            case "detect17":
                res = "out_ok17";
                break;
            case "detect18":
                res = "out_ok18";
                break;
            case "detect19":
                res = "out_ok19";
                break;
            case "detect20":
                res = "out_ok20";
                break;
            case "detect21":
                res = "out_ok21";
                break;
            case "detect22":
                res = "out_ok22";
                break;
            case "detect23":
                res = "out_ok23";
                break;
            case "detect24":
                res = "out_ok24";
                break;
            case "detect25":
                res = "out_ok25";
                break;
            case "detect26":
                res = "out_ok26";
                break;
            case "detect27":
                res = "out_ok27";
                break;
            case "detect28":
                res = "out_ok28";
                break;
        }
        return res;
    }


    @Override
    public Result getDetectionRecordMom(List<String> ids, String orderNo, String lineNo) {
//        获取检测记录ID
        JCEquimentBase<ProDetection<List<Map<String, Object>>>> jcEquimentBase = new JCEquimentBase<>();
        if (CollectionUtils.isNotEmpty(ids)) {
//            根据检测记录ID获取检测记录 并包含MOM订单号,MOM主物料号
            List<Map<String, Object>> list = workpieceDataService.newestThreeDataMom(ids);
            for (Map<String, Object> objectMap : list) {
                Date detectorTime = (Date) objectMap.get("detectorTime");
                String format = dateUtil.dateFormatToStingYmdHms(detectorTime);
                objectMap.put("detectorTime", format);
                for (int i = 1; i < 29; i++) {
                    String key = i < 10 ? "0" + i : i + "";
                    Object val = objectMap.get("out_ok" + key);
                    if (val != null && val.toString().equals("0")) {
                        objectMap.put("detect" + key, String.valueOf(objectMap.get("detect" + key)) + "::");
                    }
                }
            }
            String productNo = list.get(0).get("productNo").toString();
            redisUtil.set(RedisKey.MA_ER_BIAO_CHECK_HISTORY + orderNo + lineNo, productNo);
            ProDetection<List<Map<String, Object>>> proDetection = new ProDetection<>();
            List<Map<String, Object>> templates = dzProductDetectionTemplateService.listProductNo(productNo, orderNo, lineNo);
            if (CollectionUtils.isEmpty(templates)) {
                templates = dzProductDetectionTemplateService.getDefoutDetectionTemp();
            }
            proDetection.setTableColumn(templates);
            proDetection.setTableData(list);
            jcEquimentBase.setData(proDetection);
        }
        jcEquimentBase.setType(DeviceSocketSendStatus.FOUR_PRODUCT_TEST_DATA.getInfo());
        Result<JCEquimentBase<ProDetection<List<Map<String, Object>>>>> ok = Result.ok(jcEquimentBase);
        return ok;
    }

    @Override
    public Result<JCEquimentBase<ProDetection<Map<String, Object>>>> getDetectionRecordMomSingle(String id, String orderNo, String lineNo) {
        //        获取检测记录ID
        JCEquimentBase<ProDetection<Map<String, Object>>> jcEquimentBase = new JCEquimentBase<>();
//            根据检测记录ID获取检测记录 并包含MOM订单号,MOM主物料号
        Map<String, Object> objectMap = workpieceDataService.newestThreeDataMomSingle(id);
        Date detectorTime = (Date) objectMap.get("detectorTime");
        String format = dateUtil.dateFormatToStingYmdHms(detectorTime);
        objectMap.put("detectorTime", format);
        for (int i = 1; i < 29; i++) {
            String key = i < 10 ? "0" + i : i + "";
            Object val = objectMap.get("out_ok" + key);
            if (val != null && val.toString().equals("0")) {
                objectMap.put("detect" + key, String.valueOf(objectMap.get("detect" + key)) + "::");
            }
        }
        String productNo = objectMap.get("productNo").toString();
        ProDetection<Map<String, Object>> proDetection = new ProDetection<>();
        List<Map<String, Object>> templates = dzProductDetectionTemplateService.listProductNo(productNo, orderNo, lineNo);
        if (CollectionUtils.isEmpty(templates)) {
            templates = dzProductDetectionTemplateService.getDefoutDetectionTemp();
        }
        proDetection.setTableColumn(templates);
        proDetection.setTableData(objectMap);
        jcEquimentBase.setData(proDetection);
        jcEquimentBase.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_DATA_TREND_SINGLE.getInfo());
        Result<JCEquimentBase<ProDetection<Map<String, Object>>>> ok = Result.ok(jcEquimentBase);
        return ok;
    }

    @Override
    public Result getMaErBiaoDetectionMonitor(String orderNo, String lineNo, String qrCode) {
        JCEquimentBase<ProDetection<List<Map<String, Object>>>> jcEquimentBase = new JCEquimentBase<>();
        ProDetection<List<Map<String, Object>>>proDetection=new ProDetection<>();
        List<Map<String, Object>>maps=new ArrayList();
        Map<String, Object> map = dzWorkpieceDataMapper.getMaErBiaoDetectionMonitor(orderNo, lineNo, qrCode);
        //接受状态
        Object detect28 = map.get("detect28");
        if("9999.999".equals(detect28.toString())){
            map.put("status","未上传");
        }else{
            map.put("status","已上传");
        }
        Map<String, Object>map1=new HashMap<>();
        map1.put("detect28",map.get("detect28"));
        map1.put("productName",map.get("productName"));
        map1.put("qrCode",map.get("qrCode"));
        map1.put("status",map.get("status"));
        map1.put("value",map.get("upperValue"));

        Map<String, Object>map2=new HashMap<>();
        map2.put("detect28",map.get("detect28"));
        map2.put("productName",map.get("productName"));
        map2.put("qrCode",map.get("qrCode"));
        map2.put("status",map.get("status"));
        map2.put("value",map.get("lowerValue"));

        maps.add(map1);
        maps.add(map2);
        proDetection.setTableData(maps);
        jcEquimentBase.setData(proDetection);
        jcEquimentBase.setType(DeviceSocketSendStatus.Get_Detection_Record_Mom.getInfo());
        Result<JCEquimentBase<ProDetection<List<Map<String, Object>>>>> ok = Result.ok(jcEquimentBase);
        return ok;
    }
}
