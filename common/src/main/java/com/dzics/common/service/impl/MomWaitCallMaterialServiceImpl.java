package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.MomWaitCallMaterialMapper;
import com.dzics.common.exception.CustomException;
import com.dzics.common.exception.RobRequestException;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.constant.OrderCode;
import com.dzics.common.model.custom.CallMaterial;
import com.dzics.common.model.entity.MomWaitCallMaterial;
import com.dzics.common.service.MomWaitCallMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 等待叫料的订单 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-06-10
 */
@Service
@Slf4j
public class MomWaitCallMaterialServiceImpl extends ServiceImpl<MomWaitCallMaterialMapper, MomWaitCallMaterial> implements MomWaitCallMaterialService {

    @Override
    public synchronized List<CallMaterial> getWorkStation(String proTaskId, String stationCode) {
        String[] split = stationCode.split(",");
        for (String station : split) {
            List<CallMaterial> workStation = this.baseMapper.getWorkStation(proTaskId, station);
            if (CollectionUtils.isNotEmpty(workStation)) {
                return workStation;
            }
        }
        return null;
    }

    @Override
    public List<MomWaitCallMaterial> getWorkStationOrderId(String wiporderno, String stationId, String orderCode, String lineNo) {
        String[] split = stationId.split(",");
        for (int i = split.length - 1; i >= 0; i--) {
            QueryWrapper<MomWaitCallMaterial> wp = new QueryWrapper<>();
            wp.eq("order_no",orderCode);
            wp.eq("line_no",lineNo);
            wp.eq("wipOrderNo", wiporderno);
            wp.eq("work_station", split[i]);
            List<MomWaitCallMaterial> momWaitCallMaterials = baseMapper.selectList(wp);
            if (CollectionUtils.isNotEmpty(momWaitCallMaterials)) {
                return momWaitCallMaterials;
            }
        }
        return null;
    }

    @Override
    public String getOprSequenceNo(String workStation, String proTaskOrderId) {
        QueryWrapper<MomWaitCallMaterial> wp = new QueryWrapper<>();
        wp.eq("mom_order_id", proTaskOrderId);
        wp.eq("work_station", workStation);
        List<MomWaitCallMaterial> materials = baseMapper.selectList(wp);
        if (CollectionUtils.isNotEmpty(materials)) {
            return materials.get(0).getOprSequenceNo();
        }
        log.error("开始订单下料点 工位 : {}, proTaskOrderId: {} 查询不到本订单所在工序号", workStation, proTaskOrderId);
        throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR83);
    }

    @Override
    public CallMaterial getCallMaterial(String lineNo, String orderCode, List<CallMaterial> callMaterial) {
        CallMaterial materialDef = null;
        if (callMaterial.size() > 1) {
            for (CallMaterial material : callMaterial) {
                String materialName = material.getMaterialName();
                String maName = getMaterlName(orderCode);
                if (!StringUtils.isEmpty(materialName)) {
                    int i = materialName.indexOf(maName);
                    if (i >= 0) {
                        materialDef = material;
                        break;
                    }
                }
            }
            if (materialDef == null) {
                log.error("查询工序号 或 物料编码 订单：{},产线：{} 在 mom_wait_call_material 表根据 生产任务订单 proTaskId 和 StationCode 没有找到对应的物料信息 callMaterial：{} ", orderCode, lineNo, callMaterial);
                throw new RobRequestException(CustomResponseCode.ERR71);
            }
        } else {
            materialDef = callMaterial.get(0);
        }
        return materialDef;
    }
    private String getMaterlName(String orderCode) {
        if (OrderCode.GT2_1.equals(orderCode) || OrderCode.GT2_2.equals(orderCode) || OrderCode.GT2_3.equals(orderCode)) {
            return "端盖";
        }
        if (OrderCode.GT3_1.equals(orderCode) || OrderCode.GT3_2.equals(orderCode)) {
            return "端盖";
        }
        if (OrderCode.CJG2_1.equals(orderCode) || OrderCode.CJG3_1.equals(orderCode)) {
            return "耳环";
        }
        return "ERROR";
    }
    @Override
    public boolean removeProTaskId(String proTaskOrderId) {
        QueryWrapper<MomWaitCallMaterial> wp = new QueryWrapper<>();
        wp.eq("mom_order_id", proTaskOrderId);
        return remove(wp);
    }
}
