package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.DzProductionLineMapper;
import com.dzics.common.dao.DzWorkStationManagementMapper;
import com.dzics.common.dao.MomMaterialPointMapper;
import com.dzics.common.exception.CustomException;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.entity.MomMaterialPoint;
import com.dzics.common.model.constant.PointType;
import com.dzics.common.model.request.mom.GetFeedingAgvVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.mom.DzicsStationCode;
import com.dzics.common.model.response.mom.GetFeedingAgvDo;
import com.dzics.common.service.MomMaterialPointService;
import com.dzics.common.service.SysUserServiceDao;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 料点编码 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-11-02
 */
@SuppressWarnings("ALL")
@Service
@Slf4j
public class MomMaterialPointServiceImpl extends ServiceImpl<MomMaterialPointMapper, MomMaterialPoint> implements MomMaterialPointService {
    @Autowired
    private DzProductionLineMapper productionLineMapper;
    @Autowired
    private MomMaterialPointMapper materialPointMapper;
    @Autowired
    private MomMaterialPointService materialPointService;
    @Autowired
    private SysUserServiceDao userServiceDao;
    @Autowired
    private DzWorkStationManagementMapper stationManagementMapper;

    @Override
    public Result getFeedingPoints(GetFeedingAgvVo getFeedingAgvVo) {
        if (getFeedingAgvVo.getPage() != -1) {
            PageHelper.startPage(getFeedingAgvVo.getPage(), getFeedingAgvVo.getLimit());
        }
        List<GetFeedingAgvDo> allPoints = materialPointMapper.getAllPoints(getFeedingAgvVo);
        PageInfo<GetFeedingAgvDo> getFeedingAgvDoPageInfo = new PageInfo<>(allPoints);
        return Result.ok(getFeedingAgvDoPageInfo.getList(), getFeedingAgvDoPageInfo.getTotal());
    }


    @Override
    public Result delFeedingPoint(String materialPointId) {
        QueryWrapper<MomMaterialPoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("material_point_id", materialPointId);
        materialPointMapper.delete(queryWrapper);
        return Result.ok();
    }

    @Override
    public MomMaterialPoint getOrderLineNoBasketType(String orderCode, String lineNo, String basketType) {
        QueryWrapper<MomMaterialPoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", orderCode);
        queryWrapper.eq("line_no", lineNo);
        queryWrapper.eq("in_island_code", basketType);
        return materialPointMapper.selectOne(queryWrapper);
    }

    @Override
    public MomMaterialPoint getOrderLineNoBasketTypeNg(String orderCode, String lineNo) {
        QueryWrapper<MomMaterialPoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", orderCode);
        queryWrapper.eq("line_no", lineNo);
        queryWrapper.eq("point_model", PointType.NG);
        return materialPointMapper.selectOne(queryWrapper);
    }

    @Override
    public String getNextPoint(Long orderId, Long id) {
        QueryWrapper<MomMaterialPoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        queryWrapper.eq("line_id", id);
        queryWrapper.eq("next_point", true);
        List<MomMaterialPoint> list = list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0).getStationId();
        }
        throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR81);
    }

    @Override
    public String getDzStationCode(Long orderId, Long lineId, String stationCode, String orderNo, String lineNo) {
        QueryWrapper<MomMaterialPoint> wp1 = new QueryWrapper<>();
        wp1.eq("order_id", orderId);
        wp1.eq("line_id", lineId);
        wp1.eq("dz_station_code", stationCode);
        List<MomMaterialPoint> list = list(wp1);
        if (CollectionUtils.isNotEmpty(list)) {
            if (list.size() > 1) {
                log.warn("订单:{} 产线：{}, list:{} 存在多个MOM工位：{}", orderNo, lineNo, list);
            }
            return list.get(0).getStationId();
        } else {
            log.warn("根据订单:{} ,产线:{} ,DZICS系统定义工位: {} ,未查询到SANY报工工位", orderNo, lineNo, stationCode);
            return null;
        }
    }

    @Override
    public Result getDzicsStationCode(String lineId) {
        List<DzicsStationCode> stationCodes = stationManagementMapper.getDzicsStationCode(lineId);
        return Result.ok(stationCodes);
    }

    @Override
    public MomMaterialPoint getProint(String destNo, String lineNo, String orderNo) {
        QueryWrapper<MomMaterialPoint> wp= new QueryWrapper<>();
        wp.eq("order_no",orderNo);
        wp.eq("line_no",lineNo);
        wp.eq("external_code",destNo);
        MomMaterialPoint one = getOne(wp);
        return one;
    }

    @Override
    public String getNextPoint(String orderCode, String lineNo, String basketType) {
        QueryWrapper<MomMaterialPoint> wp1 = new QueryWrapper<>();
        wp1.eq("order_no", orderCode);
        wp1.eq("line_no", lineNo);
        wp1.eq("in_island_code", basketType);
        List<MomMaterialPoint> list = list(wp1);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0).getStationId();
        }
        throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR81);
    }
}
