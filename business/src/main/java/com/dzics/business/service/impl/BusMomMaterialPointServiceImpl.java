package com.dzics.business.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.business.config.MapConfig;
import com.dzics.business.service.BusMomMaterialPointService;
import com.dzics.common.dao.DzProductionLineMapper;
import com.dzics.common.dao.MomMaterialPointMapper;
import com.dzics.common.exception.CustomException;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.entity.DzProductionLine;
import com.dzics.common.model.entity.MomMaterialPoint;
import com.dzics.common.model.entity.SysUser;
import com.dzics.common.model.request.mom.AddFeedingVo;
import com.dzics.common.model.request.mom.UpdateFeedingVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.MomMaterialPointService;
import com.dzics.common.service.SysUserServiceDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Map;

/**
 * @author ZhangChengJun
 * Date 2021/12/30.
 * @since
 */
@Slf4j
@Service
public class BusMomMaterialPointServiceImpl implements BusMomMaterialPointService {
    @Autowired
    private DzProductionLineMapper productionLineMapper;
    @Autowired
    private MomMaterialPointMapper materialPointMapper;
    @Autowired
    private MomMaterialPointService materialPointService;
    @Autowired
    private SysUserServiceDao userServiceDao;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MapConfig mapConfig;

    @Override
    public Result addFeedingPoint(String sub, AddFeedingVo addFeedingVo) {
        SysUser byUserName = userServiceDao.getByUserName(sub);
        MomMaterialPoint momMaterialPoint = new MomMaterialPoint();
        DzProductionLine dzProductionLine = productionLineMapper.selectOne(new QueryWrapper<DzProductionLine>().eq("id", addFeedingVo.getLineId()));
        if (dzProductionLine == null) {
            throw new CustomException(CustomExceptionType.OK_NO_DATA, CustomResponseCode.ERR59);
        }
        momMaterialPoint.setOrderId(dzProductionLine.getOrderId());
        momMaterialPoint.setLineId(dzProductionLine.getId());
        momMaterialPoint.setLineNo(dzProductionLine.getLineNo());
        momMaterialPoint.setOrderNo(dzProductionLine.getOrderNo());
        momMaterialPoint.setExternalCode(addFeedingVo.getExternalCode().trim());
        momMaterialPoint.setExternalRegion(addFeedingVo.getExternalRegion());
        momMaterialPoint.setPointModel(addFeedingVo.getPointModel());
        momMaterialPoint.setLineNode(addFeedingVo.getLineNode());
        momMaterialPoint.setInIslandCode(addFeedingVo.getInIslandCode());
        momMaterialPoint.setStationId(addFeedingVo.getStationName());
        momMaterialPoint.setNextPoint(addFeedingVo.getNextPoint());
        momMaterialPoint.setDelFlag(true);
        momMaterialPoint.setCreateBy(byUserName.getUsername());
        materialPointService.save(momMaterialPoint);
        return cleanCache(momMaterialPoint);
    }


    @Override
    public Result putFeedingPoint(String sub, UpdateFeedingVo addFeedingVo) {
        DzProductionLine dzProductionLine = productionLineMapper.selectOne(new QueryWrapper<DzProductionLine>().eq("id", addFeedingVo.getLineId()));
        if (dzProductionLine == null) {
            throw new CustomException(CustomExceptionType.OK_NO_DATA, CustomResponseCode.ERR59);
        }
        String lineNo = dzProductionLine.getLineNo();
        String orderNo = dzProductionLine.getOrderNo();
        Long orderId = dzProductionLine.getOrderId();
        Long lineId = dzProductionLine.getId();
        if (StringUtils.isEmpty(addFeedingVo.getMaterialPointId())) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR58);
        }
        //创建人、创建时间、删除状态、备注不需要修改，其他全部重置
        MomMaterialPoint newmom = materialPointMapper.selectById(addFeedingVo.getMaterialPointId());
        if (newmom == null) {
            throw new CustomException(CustomExceptionType.OK_NO_DATA, CustomResponseCode.ERR17);
        }
        String inIslandCode = addFeedingVo.getInIslandCode();
        newmom.setOrderId(orderId);
        newmom.setLineId(lineId);
        newmom.setLineNo(lineNo);
        newmom.setPointModel(addFeedingVo.getPointModel());
        newmom.setOrderNo(orderNo);
        newmom.setExternalCode(addFeedingVo.getExternalCode());
        newmom.setExternalRegion(addFeedingVo.getExternalRegion());
        newmom.setLineNode(addFeedingVo.getLineNode());
        newmom.setInIslandCode(inIslandCode);
        newmom.setStationId(addFeedingVo.getStationName());
        newmom.setUpdateBy(sub);
        newmom.setUpdateTime(new Date());
        newmom.setNextPoint(addFeedingVo.getNextPoint());
        materialPointMapper.updateById(newmom);
        return cleanCache(newmom);
    }


    public Result cleanCache(MomMaterialPoint point) {
        String url = "";
        String orderNo = point.getOrderNo();
        String lineNo = point.getLineNo();
        try {
            Map<String, String> mapIps = mapConfig.getMaps();
            String plcIp = mapIps.get(orderNo + lineNo);
            if (CollectionUtils.isNotEmpty(mapIps) && !StringUtils.isEmpty(plcIp)) {
                url = "http://" + plcIp + ":8107/api/receive/data/get/position";
                ResponseEntity<Result> resultResponseEntity = restTemplate.postForEntity(url, point, Result.class);
                Result body = resultResponseEntity.getBody();
                log.info("清除SANYMOM工位配置模块缓存 到单岛 订单:{} ,产线：{} ,响应参数：{}, 请求参数:{}", orderNo, lineNo, JSONObject.toJSONString(body), JSONObject.toJSONString(point));
                return body;
            } else {
                log.error("清除SANYMOM工位配置模块缓存 IP 配置不存在orderNo : {}, lineNo: {} , mapIps: {}", orderNo, lineNo, mapIps);
                return Result.error(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
            }
        } catch (Throwable throwable) {
            log.error("清除SANYMOM工位配置模块缓存 订单:{} ,产线：{} ,错误信息：{}, 请求参数:{}", orderNo, lineNo, throwable.getMessage(), JSONObject.toJSONString(point), throwable);
            return Result.ok(throwable.getMessage());
        }
    }
}
