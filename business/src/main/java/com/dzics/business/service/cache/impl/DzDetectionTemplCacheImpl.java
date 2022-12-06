package com.dzics.business.service.cache.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.business.model.vo.LockScreenPassWord;
import com.dzics.business.model.vo.detectordata.edit.DbProDuctileEditer;
import com.dzics.business.model.vo.detectordata.edit.EditProDuctTemp;
import com.dzics.business.service.cache.DzDetectionTemplCache;
import com.dzics.business.util.RedisUtil;
import com.dzics.common.dao.*;
import com.dzics.common.enums.ConfigType;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.entity.*;
import com.dzics.common.model.constant.MomProgressStatus;
import com.dzics.common.model.request.DBDetectTempVo;
import com.dzics.common.model.request.DzDetectTempVo;
import com.dzics.common.model.request.kb.GetOrderNoLineNo;
import com.dzics.common.model.response.DetectionData;
import com.dzics.common.model.response.ProductParm;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.RunDataModel;
import com.dzics.common.service.DzProductDetectionTemplateService;
import com.dzics.common.service.MomOrderService;
import com.dzics.common.service.SysConfigService;
import com.dzics.common.service.SysDictItemService;
import com.dzics.common.util.RedisKey;
import com.dzics.common.util.md5.Md5Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ZhangChengJun
 * Date 2021/2/5.
 * @since
 */
@SuppressWarnings("ALL")
@Service
@Slf4j
public class DzDetectionTemplCacheImpl implements DzDetectionTemplCache {

    @Autowired
    private DzDetectionTemplateMapper dzDetectionTemplateMapper;
    @Autowired
    private DzProductDetectionTemplateMapper productDetectionTemplateMapper;
    @Autowired
    private DzProductDetectionTemplateService detectionTemplateService;
    @Autowired
    private DzDetectorDataMapper dzDetectorDataMapper;
    @Autowired
    DzProductMapper dzProductMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    DzProductionLineMapper lineMapper;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private SysDictItemService sysDictItemService;
    @Autowired
    private MomOrderService momOrderService;

    @Override
    public List<DzDetectTempVo> list() {
        return dzDetectionTemplateMapper.listDzDetectTempVo();
    }

    @Override
    public List<DzProductDetectionTemplate> getByOrderNoProNo(Long departId, String productNo, String orderId1, String lineId1) {
        QueryWrapper<DzProductDetectionTemplate> wp = new QueryWrapper<>();
        wp.eq("product_no", productNo);
        wp.eq("depart_id", departId);
        wp.eq("order_id", orderId1);
        wp.eq("line_id", lineId1);
        wp.select("table_col_con", "table_col_val", "compensation_value", "detection_id", "group_Id");
        List<DzProductDetectionTemplate> templates = productDetectionTemplateMapper.selectList(wp);
        return templates;
    }


    @Override
    public List<Map<String, Object>> getGroupKey(List<String> groupKey) {
        return dzDetectorDataMapper.getGroupKey(groupKey);
    }

    @Override
    public List<DetectionData> getDataValue(String groupKey) {
        return dzDetectorDataMapper.getDataValue(groupKey);
    }

    @Override
    public List<DzDetectTempVo> getEditDetectorItem(String groupId) {
        List<DzDetectTempVo> vos = productDetectionTemplateMapper.groupById(groupId);
        return vos;
    }

    @Override
    public List<ProductParm> getByDepartId(Long departId) {
        return dzProductMapper.getByDepartId(departId);
    }

    @Override
    public Long getByProeuctNoDepartId(String productNo) {
        return dzProductMapper.getByProeuctNoDepartId(productNo);
    }

    @Override
    public Result editProDetectorItem(EditProDuctTemp templateParm) {
        List<DbProDuctileEditer> dzDetectTempVos = templateParm.getDzDetectTempVos();
        List<DzProductDetectionTemplate> update = new ArrayList<>();
        for (DbProDuctileEditer dzDetectTempVo : dzDetectTempVos) {
            DzProductDetectionTemplate up = new DzProductDetectionTemplate();
            BeanUtils.copyProperties(dzDetectTempVo, up);
            up.setDepartId(templateParm.getDepartId());
            up.setProductNo(templateParm.getProductNo());
            update.add(up);
        }
        detectionTemplateService.updateBatchById(update);
        if (CollectionUtils.isNotEmpty(dzDetectTempVos)) {
            DzProductDetectionTemplate byId = detectionTemplateService.getById(dzDetectTempVos.get(0).getDetectionId());
            if (byId != null) {
                redisUtil.del(com.dzics.common.util.RedisKey.TEST_ITEM + byId.getOrderNo() + byId.getLineNo() + templateParm.getProductNo());
            }
        }

        return new Result(CustomExceptionType.OK);
    }

    @Override
    public Result dbProDetectorItem(EditProDuctTemp editProDuctTemp) {
        List<DbProDuctileEditer> dzDetectTempVos = editProDuctTemp.getDbDetectTempVos();
        List<DzProductDetectionTemplate> update = new ArrayList<>();
        for (DbProDuctileEditer dzDetectTempVo : dzDetectTempVos) {
            DzProductDetectionTemplate up = new DzProductDetectionTemplate();
            BeanUtils.copyProperties(dzDetectTempVo, up);
            up.setDepartId(editProDuctTemp.getDepartId());
            up.setProductNo(editProDuctTemp.getProductNo());
            update.add(up);
        }
        detectionTemplateService.updateBatchById(update);
        return new Result(CustomExceptionType.OK);
    }

    @Override
    public List<DBDetectTempVo> getEditDBDetectorItem(String groupId) {
        List<DBDetectTempVo> vos = productDetectionTemplateMapper.geteditdbdetectoritem(groupId);
        return vos;
    }

    @Override
    public boolean delGroupupId(Long groupId) {
        QueryWrapper<DzProductDetectionTemplate> wp = new QueryWrapper<>();
        wp.eq("group_Id", groupId);
        int delete = productDetectionTemplateMapper.delete(wp);
        return delete > 1 ? true : false;
    }

    @Override
    public List<DzProductDetectionTemplate> getByOrderNoProNoIsShow(long departId, String productNo) {
        QueryWrapper<DzProductDetectionTemplate> wp = new QueryWrapper<>();
        wp.eq("product_no", productNo);
        wp.eq("depart_id", departId);
        wp.eq("is_show", 0);
        wp.select("table_col_con", "table_col_val");
        List<DzProductDetectionTemplate> templates = productDetectionTemplateMapper.selectList(wp);
        return templates;
    }

    @Override
    public Result systemRunModel(String sub) {
        RunDataModel runDataModel = sysConfigService.systemRunModel();
        if (runDataModel == null) {
            runDataModel.setTableName("dz_equipment_pro_num");
            runDataModel.setPlanDay("dz_production_plan_day");
            runDataModel.setRunDataModel("数量累计模式");
        }
        return Result.OK(runDataModel);
    }

    @Override
    public Result editSystemRunModel(String sub, RunDataModel runDataModel) {
        Object keySize = redisUtil.get(RedisKey.KEY_RUN_MODEL_DANGER);
        if (keySize == null) {
            redisUtil.set(RedisKey.KEY_RUN_MODEL_DANGER, Integer.valueOf(1), 5);
            return Result.ok(runDataModel);
        }
        if (keySize != null) {
            Integer ketSi = (Integer) keySize;
            if (ketSi.intValue() < 5) {
                ketSi = ketSi.intValue() + 1;
                redisUtil.set(RedisKey.KEY_RUN_MODEL_DANGER, ketSi, 5);
                return Result.ok(runDataModel);
            } else {
                redisUtil.del(RedisKey.KEY_RUN_MODEL_DANGER);
            }
        }
        if ("dz_equipment_pro_num".equals(runDataModel.getTableName())) {
            runDataModel.setTableName("dz_equipment_pro_num_signal");
            runDataModel.setPlanDay("dz_production_plan_day_signal");
            runDataModel.setRunDataModel("脉冲模式");
        } else {
            runDataModel.setTableName("dz_equipment_pro_num");
            runDataModel.setPlanDay("dz_production_plan_day");
            runDataModel.setRunDataModel("数量累计模式");
        }
        sysConfigService.editSystemRunModel(runDataModel);
        return Result.ok(runDataModel);
    }

    @Override
    public DzProductionLine getLineIdByOrderNoLineNo(GetOrderNoLineNo getOrderNoLineNo) {
        QueryWrapper<DzProductionLine> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", getOrderNoLineNo.getOrderNo());
        wrapper.eq("line_no", getOrderNoLineNo.getLineNo());
        DzProductionLine dzProductionLine = lineMapper.selectOne(wrapper);
        return dzProductionLine;
    }

    @Override
    public void deleteLineIdByOrderNoLineNo() {

    }

    @Override
    public Result getLockScreenPassword(LockScreenPassWord screenPassWord, String sub) {
        SysConfig sysConfig = sysConfigService.getConfig(ConfigType.ConfigPassword);
        String lockPasswordX = screenPassWord.getLockPassword();
        String pass = Md5Util.md5(lockPasswordX);
        if (pass.equals(sysConfig.getConfigValue())) {
            return Result.ok();
        }
        return Result.error(CustomExceptionType.AUTHEN_TICATIIN_FAILURE, CustomResponseCode.ERR47);
    }

    @Override
    public Result putLockScreenPassword(String sub, LockScreenPassWord screenPassWord) {
        String pass = Md5Util.md5(screenPassWord.getLockPassword());
        sysConfigService.updateConfigType(pass);
        return Result.ok();
    }

    @Override
    public List<String> getMouthDate(int year, int monthValue) {
        return sysConfigService.getMouthDate(year, monthValue);
    }

    @Override
    public String getSystemConfigDepart() {
        try {
            SysDictItem dict_code = sysDictItemService.getOne(new QueryWrapper<SysDictItem>().eq("dict_code", "sys_depart"));
            if (dict_code == null) {
                return "SANY";
            }
            return dict_code.getItemText();
        } catch (Exception e) {
            return "SANY";
        }
    }

    @Override
    public String getIndexIsShowNg() {
        try {
            SysDictItem dict_code = sysDictItemService.getOne(new QueryWrapper<SysDictItem>().eq("dict_code", "index_is_show_ng_ok"));
            if (dict_code == null) {
                return "false";
            }
            return dict_code.getItemValue();
        } catch (Throwable e) {
            log.error("获取是否在首页展示NG异常:{}", e.getMessage(), e);
            return "false";
        }
    }

    @Override
    public BigDecimal getProductNameFrequency(String lineType, String productAlias) {
        QueryWrapper<DzProduct> dz = new QueryWrapper<>();
        dz.eq("line_type", lineType);
        dz.eq("product_name", productAlias);
        DzProduct dzProduct = dzProductMapper.selectOne(dz);
        if (dzProduct != null) {
            BigDecimal frequency = dzProduct.getFrequency();
            if (frequency != null) {
                return frequency;
            }
        }
        return new BigDecimal(0);
    }



    @Override
    public MonOrder getNowOrder(String orderNo, String lineNo, String loading) {
        MonOrder orderLine = momOrderService.getMomOrder(orderNo, lineNo, MomProgressStatus.LOADING);
        return orderLine;
    }
}
