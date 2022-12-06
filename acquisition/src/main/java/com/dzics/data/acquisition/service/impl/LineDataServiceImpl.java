package com.dzics.data.acquisition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.dao.*;
import com.dzics.common.model.entity.DzProduct;
import com.dzics.common.model.entity.DzProductDetectionTemplate;
import com.dzics.common.model.entity.DzProductionLine;
import com.dzics.common.model.entity.DzWorkpieceData;
import com.dzics.common.model.request.kb.GetOrderNoLineNo;
import com.dzics.common.model.response.GetDetectionLineChartDo;
import com.dzics.common.model.response.RunDataModel;
import com.dzics.common.model.response.SelectTrendChartDo;
import com.dzics.common.model.response.feishi.DayDataDo;
import com.dzics.common.model.response.feishi.DayDataResultDo;
import com.dzics.common.util.DateUtil;
import com.dzics.common.util.RedisKey;
import com.dzics.common.util.StringToUpcase;
import com.dzics.data.acquisition.service.AccqDzProductService;
import com.dzics.data.acquisition.service.CacheService;
import com.dzics.data.acquisition.service.LineDataService;
import com.dzics.data.acquisition.util.RedisUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
public class LineDataServiceImpl implements LineDataService {
    @Autowired
    DzDetectorDataMapper dzDetectorDataMapper;
    @Autowired
    DzProductDetectionTemplateMapper dzProductDetectionTemplateMapper;
    @Autowired
    DateUtil dateUtil;
    @Autowired
    DzEquipmentProNumMapper dzEquipmentProNumMapper;
    @Autowired
    CacheService cacheService;
    @Autowired
    DzProductionLineMapper dzProductionLineMapper;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    DzWorkpieceDataMapper dzWorkpieceDataMapper;
    @Autowired
    DzProductMapper dzProductMapper;
    @Autowired
    AccqDzProductService accqDzProductService;
    //产品检测项 需要展示的最近数据条数
    int num = 10;

    @Override
    public GetDetectionLineChartDo charts(DzWorkpieceData data) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        GetDetectionLineChartDo getDetectionLineChartDo=new GetDetectionLineChartDo();
        //获取产品检测项
        Object productTestNameObj = redisUtil.get(RedisKey.TEST_ITEM + data.getOrderNo() + data.getLineNo() + data.getProductNo());
        List<Map<String, String>>productTestName=null;
        if(productTestNameObj==null){
            productTestName= accqDzProductService.getProductNoShowDetection(data.getProductNo(), data.getOrderNo(), data.getLineNo());
        }else{
            productTestName=(List<Map<String, String>>)productTestNameObj;
        }
        if(productTestName==null){
            log.error("检测项多项数据折线图推送异常 ，产品未配置检测项,订单:{}，产品:{}",data.getOrderNo(),data.getProductNo());
            return null;
        }
        List<Map<String,Object>>dataList=new ArrayList<>();
        for (Map<String, String> map:productTestName) {
            Map<String,Object>myMap=new HashMap<>();
            String colData = map.get("colData");
            String colName = map.get("colName");
            Method method=data.getClass().getMethod("get"+ StringToUpcase.toUpperCase(colData));
            Object invoke = method.invoke(data);

            Object  productDetectionObj= redisUtil.get(data.getOrderNo() + data.getLineNo() + data.getProductNo() + colData);
            if(productDetectionObj!=null){
                List<Object>productDetection=(List<Object>)productDetectionObj;
                productDetection.add(invoke);
                if(productDetection.size()>6){
                    productDetection.remove(0);
                }
                myMap.put("data",productDetection);
                myMap.put("name",colName);
            }else{
                GetOrderNoLineNo getOrderNoLineNo=new GetOrderNoLineNo();
                getOrderNoLineNo.setOrderNo(data.getOrderNo());
                getOrderNoLineNo.setLineNo(getOrderNoLineNo.getLineNo());
                return accqDzProductService.getDetectionLineChart(getOrderNoLineNo);
            }
            dataList.add(myMap);
        }
        getDetectionLineChartDo.setProductName(data.getName());
        getDetectionLineChartDo.setProductNo(data.getProductNo());
        getDetectionLineChartDo.setData(dataList);

        return getDetectionLineChartDo;
    }

    @Override
    public SelectTrendChartDo getSelectTrendChartDo(DzWorkpieceData dzWorkpieceData, String orderNo, String lineNo) {
        SelectTrendChartDo selectTrendChartDo = new SelectTrendChartDo();
        DzProductDetectionTemplate dzProductDetectionTemplate1 = dzProductDetectionTemplateMapper.selectOne(new QueryWrapper<DzProductDetectionTemplate>()
                .eq("product_no", dzWorkpieceData.getProductNo())
                .eq("order_no",dzWorkpieceData.getOrderNo())
                .eq("table_col_val", "detect01"));
        DzProductDetectionTemplate dzProductDetectionTemplate2 = dzProductDetectionTemplateMapper.selectOne(new QueryWrapper<DzProductDetectionTemplate>()
                .eq("product_no", dzWorkpieceData.getProductNo())
                .eq("order_no",dzWorkpieceData.getOrderNo())
                .eq("table_col_val", "detect02"));
        selectTrendChartDo.setNameOne(dzProductDetectionTemplate1.getTableColCon());
        selectTrendChartDo.setNameTwo(dzProductDetectionTemplate2.getTableColCon());
        //获取检测项值
        PageHelper.startPage(1,num);
        List<BigDecimal> listCharData = dzWorkpieceDataMapper.getChartsData(dzWorkpieceData.getProductNo(),orderNo,lineNo,"detect01");
        PageInfo<BigDecimal> bigDecimalPageInfo = new PageInfo<>(listCharData);
        List<BigDecimal> list = bigDecimalPageInfo.getList();
        Collections.reverse(list);//倒叙排序   (sql查询的时候是按照时间降序写的 ，这里给倒一下顺序)
        selectTrendChartDo.setData(list);

        PageHelper.startPage(1,num);
        List<BigDecimal> listCharData2 = dzWorkpieceDataMapper.getChartsData(dzWorkpieceData.getProductNo(),orderNo,lineNo,"detect02");
        PageInfo<BigDecimal> bigDecimalPageInfo2 = new PageInfo<>(listCharData2);
        List<BigDecimal> list2 = bigDecimalPageInfo2.getList();
        Collections.reverse(list2);//倒叙排序   (sql查询的时候是按照时间降序写的 ，这里给倒一下顺序)
        selectTrendChartDo.setData2(list2);
        //产品名称
        DzProduct product_no = dzProductMapper.selectOne(new QueryWrapper<DzProduct>().eq("product_no", dzWorkpieceData.getProductNo()));
        selectTrendChartDo.setProductName(product_no.getProductName());
        redisUtil.set(RedisKey.INSPECTION_DATA + dzWorkpieceData.getProductNo(),selectTrendChartDo);
        return selectTrendChartDo;
    }


    @Override
    public DayDataDo dayData() {
        //合格
        List<BigDecimal> qualified = new ArrayList<>();
        //不合格
        List<BigDecimal> badness = new ArrayList<>();
        List<DzProductionLine> dzProductionLines = dzProductionLineMapper.selectList(new QueryWrapper<DzProductionLine>());
        if (dzProductionLines.size() == 0) {
            log.error("没有查询到产线");
            return null;
        }
        if (dzProductionLines.size() > 1) {
            log.error("查询到多条产线");
            return null;
        }
        Long equimentId = dzProductionLines.get(0).getStatisticsEquimentId();
        String tableKey = ((RunDataModel) cacheService.systemRunModel(null).getData()).getTableName();
        String first = dateUtil.firstDay();
        String last = dateUtil.lastDay();
        List<DayDataResultDo> dayDataResultDoList = dzEquipmentProNumMapper.dayData(tableKey, first, last, equimentId);
        for (DayDataResultDo data : dayDataResultDoList) {
            if (data.getQualifiedNum() != null) {
                qualified.add(data.getQualifiedNum());
            } else {
                qualified.add(new BigDecimal(0));
            }
            if (data.getBadnessNum() != null) {
                badness.add(data.getBadnessNum());
            } else {
                badness.add(new BigDecimal(0));
            }
        }
        DayDataDo dayDataDo = new DayDataDo();
        dayDataDo.setQualifiedNum(qualified);
        dayDataDo.setBadnessNum(badness);
        return dayDataDo;
    }

    @Override
    public DayDataDo monthData() {
        //合格
        List<BigDecimal> qualified = new ArrayList<>();
        //不合格
        List<BigDecimal> badness = new ArrayList<>();
        List<DzProductionLine> dzProductionLines = dzProductionLineMapper.selectList(new QueryWrapper<DzProductionLine>());
        if (dzProductionLines.size() == 0) {
            log.error("没有查询到产线");
            return null;
        }
        if (dzProductionLines.size() > 1) {
            log.error("查询到多条产线");
            return null;
        }
        Long equimentId = dzProductionLines.get(0).getStatisticsEquimentId();
        String tableKey = ((RunDataModel) cacheService.systemRunModel(null).getData()).getTableName();
        List<String> allMonth = dateUtil.getAllMonth();
        for (String month : allMonth) {
            //查询指定月份数据
            DayDataResultDo dayDataResultDo = dzEquipmentProNumMapper.monthData(month, tableKey, equimentId);
            if (dayDataResultDo != null) {
                if (dayDataResultDo.getQualifiedNum() != null) {
                    qualified.add(dayDataResultDo.getQualifiedNum());
                } else {
                    qualified.add(new BigDecimal(0));
                }
                if (dayDataResultDo.getBadnessNum() != null) {
                    badness.add(dayDataResultDo.getBadnessNum());
                } else {
                    badness.add(new BigDecimal(0));
                }
            } else {
                log.error("查询月生产数据异常:{}，表:{}", month, tableKey);
            }
        }
        DayDataDo dayDataDo = new DayDataDo();
        dayDataDo.setQualifiedNum(qualified);
        dayDataDo.setBadnessNum(badness);
        return dayDataDo;
    }

    @Override
    public Long getLineId() {
        List<DzProductionLine> dzProductionLines = dzProductionLineMapper.selectList(new QueryWrapper<>());
        if(dzProductionLines.size()>0){
         return   dzProductionLines.get(0).getId();
        }
        return -1L;
    }

    @Override
    public SelectTrendChartDo getDetectionByMachine(DzWorkpieceData dzWorkpieceData, String orderNo, String lineNo) {
        SelectTrendChartDo selectTrendChartDo = new SelectTrendChartDo();
        DzProductDetectionTemplate dzProductDetectionTemplate1 = dzProductDetectionTemplateMapper.selectOne(new QueryWrapper<DzProductDetectionTemplate>()
                .eq("product_no", dzWorkpieceData.getProductNo())
                .eq("order_no",dzWorkpieceData.getOrderNo())
                .eq("table_col_val", "detect01"));
        DzProductDetectionTemplate dzProductDetectionTemplate2 = dzProductDetectionTemplateMapper.selectOne(new QueryWrapper<DzProductDetectionTemplate>()
                .eq("product_no", dzWorkpieceData.getProductNo())
                .eq("order_no",dzWorkpieceData.getOrderNo())
                .eq("table_col_val", "detect02"));
        selectTrendChartDo.setNameOne(dzProductDetectionTemplate1.getTableColCon());
        selectTrendChartDo.setNameTwo(dzProductDetectionTemplate2.getTableColCon());
        //获取检测项值
        PageHelper.startPage(1,num);
        List<BigDecimal> listCharData = dzWorkpieceDataMapper.getDetectionByMachine(dzWorkpieceData.getProductNo(),orderNo,lineNo,"detect01",dzWorkpieceData.getMachineNumber());
        PageInfo<BigDecimal> bigDecimalPageInfo = new PageInfo<>(listCharData);
        List<BigDecimal> list = bigDecimalPageInfo.getList();
        Collections.reverse(list);//倒叙排序   (sql查询的时候是按照时间降序写的 ，这里给倒一下顺序)
        selectTrendChartDo.setData(list);

        PageHelper.startPage(1,num);
        List<BigDecimal> listCharData2 = dzWorkpieceDataMapper.getDetectionByMachine(dzWorkpieceData.getProductNo(),orderNo,lineNo,"detect02",dzWorkpieceData.getMachineNumber());
        PageInfo<BigDecimal> bigDecimalPageInfo2 = new PageInfo<>(listCharData2);
        List<BigDecimal> list2 = bigDecimalPageInfo2.getList();
        Collections.reverse(list2);//倒叙排序   (sql查询的时候是按照时间降序写的 ，这里给倒一下顺序)
        selectTrendChartDo.setData2(list2);
        //产品名称
        DzProduct product_no = dzProductMapper.selectOne(new QueryWrapper<DzProduct>().eq("product_no", dzWorkpieceData.getProductNo()));
        selectTrendChartDo.setProductName(product_no.getProductName());
        if("1".equals(dzWorkpieceData.getMachineNumber())){
            selectTrendChartDo.setEquipmentNo("A1");
        }else if("2".equals(dzWorkpieceData.getMachineNumber())){
            selectTrendChartDo.setEquipmentNo("A2");
        }
        redisUtil.set(RedisKey.INSPECTION_DATA + dzWorkpieceData.getProductNo()+dzWorkpieceData.getMachineNumber(),selectTrendChartDo);
        return selectTrendChartDo;
    }

    @Override
    public SelectTrendChartDo getCharts(DzWorkpieceData dzWorkpieceData) {

        Object o = redisUtil.get(RedisKey.INSPECTION_DATA + dzWorkpieceData.getProductNo()+dzWorkpieceData.getMachineNumber());
        //缓存里面有，直接取值 并更新缓存
        if(o!=null){
            SelectTrendChartDo selectTrendChartDo= (SelectTrendChartDo) o;
            try {

                List<BigDecimal> data1 = selectTrendChartDo.getData();//检测项1的值
                List<BigDecimal> data2 = selectTrendChartDo.getData2();//检测项2的值
                data1.add(dzWorkpieceData.getDetect01());
                data2.add(dzWorkpieceData.getDetect02());
                if(data1.size()>num){
                    data1.remove(0);
                }
                if(data2.size()>num){
                    data2.remove(0);
                }
                selectTrendChartDo.setData(data1);
                selectTrendChartDo.setData2(data2);
                redisUtil.set(RedisKey.INSPECTION_DATA + dzWorkpieceData.getProductNo()+dzWorkpieceData.getMachineNumber(),selectTrendChartDo);
                return selectTrendChartDo;
            }catch (Exception e){
                log.error("检测数据推送，缓存数据解析异常:{}",dzWorkpieceData,e);
                return null;
            }
        }
        return getDetectionByMachine(dzWorkpieceData,dzWorkpieceData.getOrderNo(),dzWorkpieceData.getLineNo());
    }
}
