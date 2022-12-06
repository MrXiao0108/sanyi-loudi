package com.dzics.data.acquisition.service.impl;
import com.dzics.common.dao.MomOrderMapper;
import com.dzics.common.enums.Message;
import com.dzics.common.exception.CustomException;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.entity.DzProductDetectionTemplate;
import com.dzics.common.model.entity.DzProductionLine;
import com.dzics.common.service.DzProductDetectionTemplateService;
import com.dzics.common.service.DzProductionLineService;
import com.dzics.common.util.DateUtil;
import com.dzics.common.util.StringToUpcase;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.dao.DzWorkpieceDataMapper;
import com.dzics.common.model.entity.DzProduct;
import com.dzics.common.model.entity.DzWorkpieceData;
import com.dzics.common.model.request.kb.GetOrderNoLineNo;
import com.dzics.common.model.response.GetDetectionLineChartDo;
import com.dzics.common.model.response.SelectTrendChartDo;
import com.dzics.common.service.DzProductService;
import com.dzics.common.util.RedisKey;
import com.dzics.data.acquisition.service.AccqDzProductService;
import com.dzics.data.acquisition.service.LineDataService;
import com.dzics.data.acquisition.util.RedisUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.*;

/**
 * @author ZhangChengJun
 * Date 2021/6/3.
 * @since
 */
@Slf4j
@Service
public class AccqDzProductServiceImpl implements AccqDzProductService {
    @Autowired
    private DzProductService dzProductService;
    @Autowired
    private LineDataService lineDataService;
    @Autowired
    DzWorkpieceDataMapper dzWorkpieceDataMapper;
    @Autowired
    DzProductDetectionTemplateService dzProductDetectionTemplateService;
    @Autowired
    DzProductionLineService dzProductionLineService;
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    MomOrderMapper momOrderMapper;
    @Override
    public SelectTrendChartDo getSanYiDetectionCurve(String orderNo, String lineNo) {
        PageHelper.startPage(1, 1);
        QueryWrapper<DzWorkpieceData> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", orderNo);
        wrapper.eq("line_no", lineNo);
        wrapper.orderByDesc("detector_time");
        List<DzWorkpieceData> dzWorkpieceDatas = dzWorkpieceDataMapper.selectList(wrapper);
        PageInfo<DzWorkpieceData> info = new PageInfo<>(dzWorkpieceDatas);
        List<DzWorkpieceData> list = info.getList();
        if (CollectionUtils.isNotEmpty(list)) {
            SelectTrendChartDo charts = lineDataService.getSelectTrendChartDo(list.get(0), orderNo, lineNo);
            return charts;
        }
        return null;
    }

    @Override
    public DzProduct getById(String productId) {
        return dzProductService.getById(productId);
    }

    @Override
    public List<SelectTrendChartDo> getDetectionByMachine(String orderNo, String lineNo, GetOrderNoLineNo data) {
        List<SelectTrendChartDo> dataList = new ArrayList<>();
        List<String> machineNo = data.getMachineNo();
        for (String machine : machineNo) {
            PageHelper.startPage(1, 1);
            QueryWrapper<DzWorkpieceData> wrapper = new QueryWrapper<>();
            wrapper.eq("order_no", orderNo);
            wrapper.eq("line_no", lineNo);
            wrapper.eq("machine_number", machine);
            wrapper.orderByDesc("detector_time");
            List<DzWorkpieceData> dzWorkpieceDatas = dzWorkpieceDataMapper.selectList(wrapper);
            PageInfo<DzWorkpieceData> info = new PageInfo<>(dzWorkpieceDatas);
            List<DzWorkpieceData> list = info.getList();
            if (CollectionUtils.isNotEmpty(list)) {
                SelectTrendChartDo charts = lineDataService.getDetectionByMachine(list.get(0), orderNo, lineNo);
                dataList.add(charts);
            }
        }
        return dataList;
    }

    @Override
    public GetDetectionLineChartDo getDetectionLineChart(GetOrderNoLineNo data) {
        PageHelper.startPage(1, 1);
        QueryWrapper<DzWorkpieceData> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", data.getOrderNo()).eq("line_no", data.getLineNo()).orderByDesc("detector_time");
        List<DzWorkpieceData> dzWorkpieceData = dzWorkpieceDataMapper.selectList(wrapper);
        PageInfo<DzWorkpieceData> pageInfo = new PageInfo(dzWorkpieceData);
        List<DzWorkpieceData> list = pageInfo.getList();
        if (list.size() == 0) {
            return null;
        }
        DzWorkpieceData workpieceData = list.get(0);

        //查询改产品配置的检测项
        String productNo = workpieceData.getProductNo();
        //根据产品序号和订单产线 查询该产品绑定的检测项
        List<Map<String, String>> productNoShowDetection = getProductNoShowDetection(productNo, data.getOrderNo(), data.getLineNo());
        if (CollectionUtils.isNotEmpty(productNoShowDetection)) {
            redisUtil.set(RedisKey.TEST_ITEM + data.getOrderNo() + data.getLineNo() + productNo, productNoShowDetection);
        }
        //根据检测项 查询产品的检测值
        PageHelper.startPage(1, 6);
        QueryWrapper<DzWorkpieceData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", data.getOrderNo()).eq("line_no", data.getLineNo()).eq("product_no", productNo).orderByDesc("detector_time");
        List<DzWorkpieceData> dataList = dzWorkpieceDataMapper.selectList(queryWrapper);
        PageInfo<DzWorkpieceData> info = new PageInfo<>(dataList);
        List<DzWorkpieceData> dzWorkpieceDataList = info.getList();
        //查询该产品最近的检测数据
        try {
            return getProductDetection(dzWorkpieceDataList, productNoShowDetection, data, workpieceData);
        } catch (Exception e) {
            log.error("检测数多检测项折线图查询数据异常{}", e.getMessage(), e);
        }
        return null;
    }

    //根据产品序号和订单产线 查询该产品绑定的检测项
    @Override
    public List<Map<String, String>> getProductNoShowDetection(String productNo, String orderNo, String lineNo) {
        List<Map<String, Object>> mapList = dzProductDetectionTemplateService.listProductNo(productNo, orderNo, lineNo);
        List<Map<String, String>> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(mapList)) {
            for (Map<String, Object> map : mapList) {
                if ("0".equals(map.get("is_show").toString())) {
                    Map<String, String> myMap = new HashMap<>();
                    myMap.put("colName", map.get("colName").toString());
                    myMap.put("colData", map.get("colData").toString());
                    list.add(myMap);
                }
            }
        }
        return list;
    }

    @Override
    public String getNameAndOrder(String name, String lineType) {
        try {
            DzProduct dzProduct = dzProductService.getNameAndOrder(name, lineType);
            if (dzProduct != null) {
                return dzProduct.getProductNo();
            }
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR88);
        } catch (Throwable throwable) {
            log.error("根据产品名称：{}，产线类型：{} 查询产品失败", name, lineType, throwable);
        }
        return "unknown";
    }

    @Override
    public Map<String,Object> getIntelligentDetection(DzWorkpieceData dzWorkpieceData ) throws Exception {
        Map<String,Object>map=new HashMap<>();

        DzProductionLine dzProductionLine =dzProductionLineService.getOne(new QueryWrapper<DzProductionLine>().eq("order_no",dzWorkpieceData.getOrderNo()).eq("line_no",dzWorkpieceData.getLineNo()));
        if(dzProductionLine==null){
            log.error("智能检测系统查询异常,产线不存在,订单号:{}，产线号:{}",dzWorkpieceData.getOrderNo(),dzWorkpieceData.getLineNo());
            throw  new Exception( Message.ERR_28);
        }
        Date startDate=null;
        Date endDate=null;
        //如果日期是当前
        LocalDate now = LocalDate.now();
        try {
                Date localDateTime = DateUtil.getNowDate(now, "08:00:00");
                if(new Date().after(localDateTime)){
                    startDate = DateUtil.getNowDate(now, "08:00:00");
                    endDate = DateUtil.getNowDate(now.plusDays(1), "08:00:00");
                }else{
                    startDate = DateUtil.getNowDate(now.plusDays(-1), "08:00:00");
                    endDate = DateUtil.getNowDate(now, "08:00:00");
                }
        }catch (Exception e){
            log.error("智能检测系统推送，时间转换异常:{}",now,e);
            throw  new Exception( "智能检测系统推送");
        }

        List<DzWorkpieceData> resDzWorkpieceData=dzWorkpieceDataMapper.getOneWorkpieceData(dzProductionLine.getOrderNo(),dzProductionLine.getLineNo(),dzWorkpieceData.getName(),null,null,startDate,endDate,-1);

        String productNo = dzWorkpieceData.getProductNo();
        DzProduct product_no = dzProductService.getOne(new QueryWrapper<DzProduct>().eq("product_no", productNo));
        if(product_no==null){
            log.error("产品编号不存在：{}",productNo);
            map.put("dzWorkpieceData",null);
            map.put("dzWorkpieceList",new ArrayList<>());
            return map;
        }
        Map<String,Object>product=new HashMap<>();
        //生产订单号
        String wipOrderNo=momOrderMapper.getMomOrderByProducBarcode(dzWorkpieceData.getProducBarcode());
        if(wipOrderNo==null){
            wipOrderNo="未知订单";
        }
        product.put("wipOrderNo",wipOrderNo);
        //二维码
        product.put("producBarcode",dzWorkpieceData.getProducBarcode());
        //物料编号
        product.put("syProductNo",product_no.getSyProductNo());
        //检测时间
        product.put("detectorTime",DateUtil.getDateStr(dzWorkpieceData.getDetectorTime()));
        //检测结果
        product.put("outOk",dzWorkpieceData.getOutOk());
        //产品名称
        product.put("productName",dzWorkpieceData.getName());

        product.put("img",product_no.getPicture());
        //查询检测项---------------------------------
        QueryWrapper<DzProductDetectionTemplate> wrapper = new QueryWrapper<DzProductDetectionTemplate>()
                .eq("product_no", productNo)
                .eq("whether_show", 0)
                .eq("order_no", dzProductionLine.getOrderNo())
                .eq("line_no", dzProductionLine.getLineNo());
        List<DzProductDetectionTemplate> dzProductDetectionTemplates = dzProductDetectionTemplateService.list(wrapper);
        Collections.reverse(resDzWorkpieceData);
        List<Map<String,Object>> list=toIntelligentDetection(dzProductDetectionTemplates,resDzWorkpieceData,null);

        //最新一条检测记录各项检测值 表格数据
        DzWorkpieceData dzWorkpiece = resDzWorkpieceData.get(resDzWorkpieceData.size() - 1);
        List<Map<String,Object>>testItemResult=new ArrayList<>();
        for (int i=0;i<dzProductDetectionTemplates.size();i++) {
            DzProductDetectionTemplate dzProductDetectionTemplate=dzProductDetectionTemplates.get(i);

            Map<String,Object>testItem=new HashMap<>();
            testItem.put("testItemName",dzProductDetectionTemplate.getTableColCon());
            //第一个对象表示检测值
            testItem.put("detectValue",reflect(dzWorkpiece,dzProductDetectionTemplate.getTableColVal()));
            testItem.put("outOk",reflect(dzWorkpiece,"outOk"+dzProductDetectionTemplate.getTableColVal().substring(6)));
            testItem.put("data",list.get(i).get("data"));
            testItemResult.add(testItem);

            Map<String,Object>testItem2=new HashMap<>();
            testItem2.put("testItemName",dzProductDetectionTemplate.getTableColCon());
//            testItem2.put("standardValue",dzProductDetectionTemplate.getStandardValue());
            //第二个对象表示标准值
            testItem2.put("detectValue",dzProductDetectionTemplate.getStandardValue());
            testItem2.put("outOk",reflect(dzWorkpiece,"outOk"+dzProductDetectionTemplate.getTableColVal().substring(6)));
            testItem2.put("data",list.get(i).get("data"));
            testItemResult.add(testItem2);


        }
        map.put("testItemResult",testItemResult);
        //最新检测记录详情
        map.put("dzWorkpieceData",product);
        //当日8-20点所有检测项 检测详情
        map.put("dzWorkpieceList",list);
        return map;
    }

    //封装检测项数据
    public GetDetectionLineChartDo getProductDetection(List<DzWorkpieceData> dzWorkpieceDataList, List<Map<String, String>> productNoShowDetection, GetOrderNoLineNo data, DzWorkpieceData workpieceData) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (CollectionUtils.isEmpty(dzWorkpieceDataList)) {
            return null;
        }
        Collections.reverse(dzWorkpieceDataList);
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Map<String, String> map : productNoShowDetection) {
            Map<String, Object> myMap = new HashMap<>();
            String colData = map.get("colData");
            String colName = map.get("colName");
            List<Object> list = new ArrayList<>();
            for (DzWorkpieceData myWorkpieceData : dzWorkpieceDataList) {
                Method method = myWorkpieceData.getClass().getMethod("get" + StringToUpcase.toUpperCase(colData));
                Object invoke = method.invoke(myWorkpieceData);
                list.add(invoke);
            }
            myMap.put("data", list);
            myMap.put("name", colName);
            //存储数据到缓存
            redisUtil.set(data.getOrderNo() + data.getLineNo() + workpieceData.getProductNo() + colData, list);
            dataList.add(myMap);
        }
        GetDetectionLineChartDo getDetectionLineChartDo = new GetDetectionLineChartDo();
        getDetectionLineChartDo.setData(dataList);
        getDetectionLineChartDo.setProductNo(workpieceData.getProductNo());
        getDetectionLineChartDo.setProductName(workpieceData.getName());
        return getDetectionLineChartDo;
    }
    public Object reflect(DzWorkpieceData obj,String str){

        try {
            Class quantityServiceImpl = obj.getClass();
            String method = StringToUpcase.toUpperCase(str);
            Method m = quantityServiceImpl.getDeclaredMethod("get"+method);
            Object invoke = m.invoke(obj);
            return invoke;
        } catch (NoSuchMethodException e) {
            log.error("获取Bean中的方法失败：{}", e);
        } catch (IllegalAccessException e) {
            log.error("执行方法参数异常：{}", e);
        } catch (InvocationTargetException e) {
            log.error("构造方法异常:{}", e);
        }
        return null;
    }
    /**
     * 根据检测项集合  检测数据  封装检测数据
     * @param dzProductDetectionTemplates
     * @param dzWorkpieceDataList
     */
    public List<Map<String,Object>> toIntelligentDetection(List<DzProductDetectionTemplate> dzProductDetectionTemplates,List<DzWorkpieceData> dzWorkpieceDataList,String code){
        List<Map<String,Object>>list=new ArrayList<>();
        for (DzProductDetectionTemplate dzProductDetectionTemplate:dzProductDetectionTemplates) {
            Map<String,Object>resList=new HashMap<>();
            //检测项  标准值 上限值 下限值
            Map<String,Object>jxcValue=new HashMap<>();
            jxcValue.put("standardValue",dzProductDetectionTemplate.getStandardValue());
            jxcValue.put("upperValue",dzProductDetectionTemplate.getUpperValue());
            jxcValue.put("lowerValue",dzProductDetectionTemplate.getLowerValue());
            resList.put("table",jxcValue);
            resList.put("title",dzProductDetectionTemplate.getTableColCon());

            //折线图数据
            List<Object>data=new ArrayList<>();
            for (int i = 0; i <dzWorkpieceDataList.size() ; i++) {
                DzWorkpieceData dzWorkpieceData=dzWorkpieceDataList.get(i);
                data.add(reflect(dzWorkpieceData,dzProductDetectionTemplate.getTableColVal()));
                if(!StringUtil.isEmpty(code)&&code.equals(dzWorkpieceData.getProducBarcode())){
                    resList.put("index",i);
                }
            }
            resList.put("data",data);
            list.add(resList);
        }

        return list;
    }
}
