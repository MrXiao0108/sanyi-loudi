package com.dzics.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.business.service.WorkingStationProductService;
import com.dzics.common.dao.DzWorkingStationProductMapper;
import com.dzics.common.enums.Message;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.entity.DzProduct;
import com.dzics.common.model.entity.DzWorkStationTemplate;
import com.dzics.common.model.entity.DzWorkingStationProduct;
import com.dzics.common.model.entity.SysUser;
import com.dzics.common.model.request.DzDetectTempVo;
import com.dzics.common.model.request.locationartifacts.AddLocationArtifactsVo;
import com.dzics.common.model.request.locationartifacts.LocationArtifactsVo;
import com.dzics.common.model.request.locationartifacts.PutLocationArtifactsVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.commons.Products;
import com.dzics.common.model.response.locationartifacts.GetLocationArtifactsByIdDo;
import com.dzics.common.model.response.locationartifacts.LocationArtifactsDo;
import com.dzics.common.service.DzProductDetectionTemplateService;
import com.dzics.common.service.DzProductService;
import com.dzics.common.service.DzWorkStationTemplateService;
import com.dzics.common.service.SysUserServiceDao;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class WorkingStationProductServiceImpl implements WorkingStationProductService {
    @Autowired
    private DzWorkingStationProductMapper dzWorkingStationProductMapper;
    @Autowired
    private DzProductService dzProductService;
    @Autowired
    private SysUserServiceDao sysUserServiceDao;
    @Autowired
    private DzWorkStationTemplateService dzWorkStationTemplateService;
    @Autowired
    private DzProductDetectionTemplateService detectionTemplateService;


    @Override
    public Result locationArtifactsList(LocationArtifactsVo locationArtifactsVo, String sub) {
        PageHelper.startPage(locationArtifactsVo.getPage(), locationArtifactsVo.getLimit());
        List<LocationArtifactsDo> product = dzWorkingStationProductMapper.locationArtifactsList(locationArtifactsVo);
        PageInfo<LocationArtifactsDo> info = new PageInfo<>(product);
        return Result.ok(info.getList(), Long.valueOf(info.getTotal()));
    }

    @Override
    public Result add(AddLocationArtifactsVo addLocationArtifactsVo, String sub) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
//        工件id
        String productId = addLocationArtifactsVo.getProductId();
//        工位id
        String workingProcedureId = addLocationArtifactsVo.getWorkingStationId();
        QueryWrapper<DzWorkingStationProduct> wrapper = new QueryWrapper<>();
        wrapper.eq("working_station_id",workingProcedureId);
        wrapper.eq("product_id",productId);
        List<DzWorkingStationProduct> dzWorkingStationProducts = dzWorkingStationProductMapper.selectList(wrapper);
        if(dzWorkingStationProducts.size()>0){
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_118);
        }
//        工位ID-产品ID 关系
        List<DzDetectTempVo> dzDetectTempVos = addLocationArtifactsVo.getDzDetectTempVos();
        DzWorkingStationProduct workingProcedureProduct = new DzWorkingStationProduct();
        workingProcedureProduct.setProductId(Long.valueOf(productId));
        workingProcedureProduct.setWorkingStationId(workingProcedureId);
//      保存工位产品关系
        dzWorkingStationProductMapper.insert(workingProcedureProduct);
//      保存检测项 和产品关系 ，  （工位ID 和 产品ID） 关系表 ID
        List<DzWorkStationTemplate> workDetectionTemplates = new ArrayList<>();
        dzDetectTempVos.stream().forEach(dzDetectTempVo -> {
            if (dzDetectTempVo.getIsShow() != null && dzDetectTempVo.getIsShow().intValue() == 0) {
                DzWorkStationTemplate workDetectionTemplate = new DzWorkStationTemplate();
                workDetectionTemplate.setWorkStationProductId(workingProcedureProduct.getWorkStationProductId());
                workDetectionTemplate.setDetectionId(Long.valueOf(dzDetectTempVo.getDetectionId()));
                workDetectionTemplate.setDetectionGroupId(dzDetectTempVo.getGroupId());
                workDetectionTemplate.setOrgCode(byUserName.getUseOrgCode());
                workDetectionTemplate.setDelFlag(false);
                workDetectionTemplate.setCreateBy(byUserName.getUsername());
                workDetectionTemplates.add(workDetectionTemplate);
            }
        });
        dzWorkStationTemplateService.saveBatch(workDetectionTemplates);
        return Result.ok();
    }

    @Override
    public Result<GetLocationArtifactsByIdDo> selEditProcedureProduct(String orderId, String lineId, String workStationProductId, String sub) {
        DzWorkingStationProduct procedureProduct = dzWorkingStationProductMapper.selectById(workStationProductId);
        DzProduct byId = dzProductService.getById(procedureProduct.getProductId());
//        获取该产品所有检测配置项
        List<DzDetectTempVo> tempVos = detectionTemplateService.selProductTemplateProductId(orderId, lineId, byId.getProductNo());
//        获取已经配置的展示的检测项
        QueryWrapper<DzWorkStationTemplate> wp = new QueryWrapper<>();
        wp.eq("work_station_product_id", workStationProductId);
        List<DzWorkStationTemplate> list = dzWorkStationTemplateService.list(wp);
        if (CollectionUtils.isNotEmpty(tempVos)) {
            for (DzDetectTempVo tempVo : tempVos) {
                tempVo.setIsShow(1);
                if (CollectionUtils.isNotEmpty(list)) {
                    String detectionId = tempVo.getDetectionId();
                    for (DzWorkStationTemplate workDetectionTemplate : list) {
                        String detectionIdX = workDetectionTemplate.getDetectionId().toString();
                        if (detectionId.equals(detectionIdX)) {
                            tempVo.setIsShow(0);
                            break;
                        }

                    }
                } else {
                    break;
                }
            }
        }
        GetLocationArtifactsByIdDo proceduct = new GetLocationArtifactsByIdDo();
        proceduct.setDzDetectTempVos(tempVos);
        Products products = new Products();
        products.setProductId(byId.getProductId() != null ? byId.getProductId().toString() : "");
        products.setOrderId(byId.getOrderId() != null ? byId.getOrderId().toString() : "");
        products.setOrderNo(byId.getOrderNo());
        products.setDepartId(byId.getDepartId() != null ? byId.getDepartId().toString() : "");
        products.setProductName(byId.getProductName());
        products.setProductNo(byId.getProductNo());
        products.setPicture(byId.getPicture());
        proceduct.setProduct(products);
        proceduct.setWorkStationProductId(workStationProductId);
        return Result.ok(proceduct);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result updateProcedureProduct(PutLocationArtifactsVo putLocationArtifactsVo, String sub) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        String productId = putLocationArtifactsVo.getProductId();
        String workStationProductId = putLocationArtifactsVo.getWorkStationProductId();

        QueryWrapper<DzWorkStationTemplate> wp = new QueryWrapper<>();
        wp.eq("work_station_product_id", workStationProductId);
        dzWorkStationTemplateService.remove(wp);

        List<DzDetectTempVo> dzDetectTempVos = putLocationArtifactsVo.getDzDetectTempVos();
        DzWorkingStationProduct workingStationProduct = new DzWorkingStationProduct();
        workingStationProduct.setWorkStationProductId(workStationProductId);
        workingStationProduct.setProductId(Long.valueOf(productId));
        dzWorkingStationProductMapper.updateById(workingStationProduct);
        //      保存检测项 和产品关系
        List<DzWorkStationTemplate> workDetectionTemplates = new ArrayList<>();
        dzDetectTempVos.stream().forEach(dzDetectTempVo -> {
            if (dzDetectTempVo.getIsShow().intValue() == 0) {
                DzWorkStationTemplate workDetectionTemplate = new DzWorkStationTemplate();
                workDetectionTemplate.setWorkStationProductId(workStationProductId);
                workDetectionTemplate.setDetectionId(Long.valueOf(dzDetectTempVo.getDetectionId()));
                workDetectionTemplate.setDetectionGroupId(dzDetectTempVo.getGroupId());
                workDetectionTemplate.setOrgCode(byUserName.getUseOrgCode());
                workDetectionTemplate.setDelFlag(false);
                workDetectionTemplate.setCreateBy(byUserName.getUsername());
                workDetectionTemplates.add(workDetectionTemplate);
            }
        });
        dzWorkStationTemplateService.saveBatch(workDetectionTemplates);
        return Result.ok();
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result delWorkStationProductId(String workStationProductId, String sub) {
        dzWorkingStationProductMapper.deleteById(workStationProductId);
        QueryWrapper<DzWorkStationTemplate> wp = new QueryWrapper<>();
        wp.eq("work_station_product_id", workStationProductId);
        dzWorkStationTemplateService.remove(wp);
        return Result.ok();
    }
}
