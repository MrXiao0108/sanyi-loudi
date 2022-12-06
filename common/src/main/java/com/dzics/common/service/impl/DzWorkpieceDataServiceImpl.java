package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.DzWorkpieceDataMapper;
import com.dzics.common.enums.DeviceSocketSendStatus;
import com.dzics.common.model.constant.MomProgressStatus;
import com.dzics.common.model.entity.DzProductionLine;
import com.dzics.common.model.entity.DzWorkpieceData;
import com.dzics.common.model.entity.MonOrder;
import com.dzics.common.model.response.JCEquimentBase;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.productiontask.ProDetection;
import com.dzics.common.model.response.productiontask.stationbg.CheckItems;
import com.dzics.common.service.DzProductionLineService;
import com.dzics.common.service.DzWorkpieceDataService;
import com.dzics.common.service.MomOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 设备检测数据V2新版记录 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-07
 */
@Service
public class DzWorkpieceDataServiceImpl extends ServiceImpl<DzWorkpieceDataMapper, DzWorkpieceData> implements DzWorkpieceDataService {
    @Autowired
    DzWorkpieceDataMapper dzWorkpieceDataMapper;
    @Autowired
    private MomOrderService momOrderService;
    @Autowired
    private DzProductionLineService lineService;

    @Override
    public DzWorkpieceData getQrCodeProduct(String qrCode) {
        List<DzWorkpieceData> qrCodeProduct = baseMapper.getQrCodeProduct(qrCode);
        if (CollectionUtils.isNotEmpty(qrCodeProduct)) {
            for (int i = 0; i < qrCodeProduct.size(); i++) {
                DzWorkpieceData dzWorkpieceData1 = qrCodeProduct.get(i);
                long dzWorkpieceDataI = dzWorkpieceData1.getDetectorTime().getTime();
                for (int i1 = 1; i1 < qrCodeProduct.size(); i1++) {
                    DzWorkpieceData dzWorkpieceData = qrCodeProduct.get(i1);
                    long dzWorkpieceDataJ = dzWorkpieceData.getDetectorTime().getTime();
                    if (dzWorkpieceDataJ > dzWorkpieceDataI) {
                        qrCodeProduct.set(i, dzWorkpieceData);
                        qrCodeProduct.set(i1, dzWorkpieceData1);
                    }
                }
            }
            return qrCodeProduct.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Map<String, List<CheckItems>> getProductIdCheckItems(String productId, Long orderId, Long lineId) {
        List<CheckItems> productIdCheckItems = baseMapper.getProductIdCheckItems(productId, orderId, lineId);
        if (CollectionUtils.isNotEmpty(productIdCheckItems)) {
            Map<String, List<CheckItems>> listMap = new HashMap<>();
            for (CheckItems items : productIdCheckItems) {
                String stationId = items.getStationId();
                List<CheckItems> checkItems = listMap.get(stationId);
                if (CollectionUtils.isNotEmpty(checkItems)) {
                    checkItems.add(items);
                } else {
                    checkItems = new ArrayList<>();
                    checkItems.add(items);
                    listMap.put(stationId, checkItems);
                }
            }
            return listMap;
        }
        return null;
    }

    @Override
    public List<Map<String, Object>> newestThreeData(List<String> infoList) {
        List<Map<String, Object>> maps = baseMapper.newestThreeData(infoList);
        return maps;
    }

    @Override
    public List<String> getNewestThreeDataId(String orderNo, String lineNo, int size) {
        return baseMapper.getNewestThreeDataId(orderNo, lineNo, size);
    }

    @Override
    public DzWorkpieceData getLastDzWorkpieceData(String orderNo, String lineNo, String now) {
        return dzWorkpieceDataMapper.getLastDzWorkpieceData(orderNo, lineNo, now);
    }

    @Override
    public List<Map<String, Object>> newestThreeDataMom(List<String> infoList) {
        List<Map<String, Object>> maps = baseMapper.newestThreeDataMom(infoList);
        return maps;
    }

    @Override
    public List<String> getWorkPieceData(String orderNo, String lineNo, Integer size) {
        List<String> ids = new ArrayList<>();
        DzProductionLine line = lineService.getOne(new QueryWrapper<DzProductionLine>().eq("order_no", orderNo).eq("line_no", lineNo));
        MonOrder monOrder = momOrderService.getOne(new QueryWrapper<MonOrder>().eq("line_id", line.getId()).eq("ProgressStatus", MomProgressStatus.LOADING));
        if (monOrder == null) {
            return ids;
        }
        ids = dzWorkpieceDataMapper.getWorkPieceData(orderNo, lineNo, monOrder.getProductAlias(), size);
        return ids;
    }

    @Override
    public Map<String, Object> newestThreeDataMomSingle(String id) {
        List<Map<String, Object>> maps = baseMapper.newestThreeDataMomSingle(id);
        if (CollectionUtils.isNotEmpty(maps)) {
            return maps.get(0);
        }
        return null;
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
