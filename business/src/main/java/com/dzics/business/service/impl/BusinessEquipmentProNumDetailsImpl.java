package com.dzics.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.business.service.BusinessEquipmentProNumDetailsService;
import com.dzics.business.service.cache.DzDetectionTemplCache;
import com.dzics.common.dao.*;
import com.dzics.common.enums.Message;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.entity.DzEquipment;
import com.dzics.common.model.entity.DzOrder;
import com.dzics.common.model.entity.DzProductionLine;
import com.dzics.common.model.request.charts.RobotDataChartsListVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.RunDataModel;
import com.dzics.common.model.response.charts.EquipmentDataChartsListDo;
import com.dzics.common.model.response.charts.EquipmentDataDetailsDo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BusinessEquipmentProNumDetailsImpl implements BusinessEquipmentProNumDetailsService {
    @Autowired
    DzEquipmentMapper dzEquipmentMapper;
    @Autowired
    DzEquipmentProNumMapper dzEquipmentProNumMapper;
    @Autowired
    DzOrderMapper dzOrderMapper;
    @Autowired
    DzProductionLineMapper dzProductionLineMapper;
    @Autowired
    DzDetectionTemplCache dzDetectionTemplCache;
    @Override
    public Result list(RobotDataChartsListVo robotDataChartsListVo) {
        String tableKey = ((RunDataModel) dzDetectionTemplCache.systemRunModel(null).getData()).getTableName();

        Long orderId = robotDataChartsListVo.getOrderId();
        Long lineId = robotDataChartsListVo.getLineId();
        DzOrder dzOrder = dzOrderMapper.selectById(orderId);
        DzProductionLine dzProductionLine = dzProductionLineMapper.selectById(lineId);
        if(dzOrder==null){
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_33);
        }
        if(dzProductionLine==null){
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_28);

        }
        List<EquipmentDataChartsListDo>list=new ArrayList<>();
        String orderNo = dzOrder.getOrderNo();
        String lineNo = dzProductionLine.getLineNo();
        QueryWrapper<DzEquipment> eq = new QueryWrapper<>();
        eq.eq("order_no",orderNo);
        eq.eq("line_no",lineNo);
        eq.in("equipment_type",2,3);
        eq.select("id","equipment_type","equipment_no","equipment_name");
        List<DzEquipment> dzEquipments = dzEquipmentMapper.selectList(eq);
        for (DzEquipment dzEquipment:dzEquipments) {
            EquipmentDataChartsListDo equipmentDataChartsListDo=new EquipmentDataChartsListDo();
            equipmentDataChartsListDo.setEquipmentId(dzEquipment.getId());
            equipmentDataChartsListDo.setEquipmentType(dzEquipment.getEquipmentType());
            equipmentDataChartsListDo.setEquipmentName(dzEquipment.getEquipmentName());
            if(robotDataChartsListVo.getEquipmentIdList().contains(dzEquipment.getId())){
                equipmentDataChartsListDo.setShow(true);
            }else{
                equipmentDataChartsListDo.setShow(false);
            }
            List<Long>resData=new ArrayList<>();
            List<String>daData=new ArrayList<>();
            //添加设备数据
            List<EquipmentDataDetailsDo>data=dzEquipmentProNumMapper.getEquipmentDataDetails(dzEquipment.getId(),robotDataChartsListVo.getStartTime(),robotDataChartsListVo.getEndTime(),tableKey);
            for (EquipmentDataDetailsDo d:data) {
                daData.add(d.getWorkDate());//日期
                if(d.getWorkNum()==null){
                    resData.add(0l);//数据
                }else {
                    resData.add(d.getWorkNum());//数据
                }
            }
            equipmentDataChartsListDo.setDateData(daData);
            equipmentDataChartsListDo.setEquipmentData(resData);
            list.add(equipmentDataChartsListDo);
        }
        //查询产线下面所有设备
        return new Result(CustomExceptionType.OK,list,list.size());
    }


}
