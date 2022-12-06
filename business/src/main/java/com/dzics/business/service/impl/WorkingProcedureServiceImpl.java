package com.dzics.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.business.model.vo.productiontask.workingprocedure.*;
import com.dzics.business.service.WorkingProcedureService;
import com.dzics.common.enums.Message;
import com.dzics.common.exception.CustomException;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.entity.*;
import com.dzics.common.model.request.DzDetectTempVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.commons.Products;
import com.dzics.common.model.response.commons.WorkingProcedures;
import com.dzics.common.model.response.productiontask.workingProcedure.SelProcedureProduct;
import com.dzics.common.model.response.productiontask.workingProcedure.WorkingProcedureRes;
import com.dzics.common.service.*;
import com.dzics.common.util.PageLimit;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ZhangChengJun
 * Date 2021/5/18.
 * @since
 */
@Slf4j
@Service
public class WorkingProcedureServiceImpl implements WorkingProcedureService {
    @Autowired
    private DzWorkingProcedureService workingProcedureService;
    @Autowired
    private DzWorkDetectionTemplateService dzWorkDetectionTemplateService;
    @Autowired
    private DzWorkingProcedureProductService dzWorkingProcedureProductService;
    @Autowired
    private SysUserServiceDao sysUserServiceDao;
    @Autowired
    private DzProductDetectionTemplateService detectionTemplateService;
    @Autowired
    private DzProductService dzProductService;
    @Autowired
    DzWorkStationManagementService dzWorkStationManagementService;

    @Override
    public Result addWorkingProcedure(WorkingProcedureAdd procedureAdd, String sub) {
        QueryWrapper<DzWorkingProcedure> wp = new QueryWrapper<>();
        wp.eq("work_code", procedureAdd.getWorkCode());
        wp.eq("order_id",procedureAdd.getOrderId());
        wp.eq("line_id",procedureAdd.getLineId());
        DzWorkingProcedure one = workingProcedureService.getOne(wp);
        if (one != null) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR39);
        }
        wp.clear();
        wp.eq("sort_code", procedureAdd.getSortCode());
        DzWorkingProcedure one1 = workingProcedureService.getOne(wp);
        if (one1 != null) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR40);
        }
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        DzWorkingProcedure workingProcedure = new DzWorkingProcedure();
        workingProcedure.setWorkCode(procedureAdd.getWorkCode());
        workingProcedure.setWorkName(procedureAdd.getWorkName());
        workingProcedure.setDepartId(Long.valueOf(procedureAdd.getDepartId()));
        workingProcedure.setLineId(Long.valueOf(procedureAdd.getLineId()));
        workingProcedure.setOrderId(Long.valueOf(procedureAdd.getOrderId()));
        workingProcedure.setSortCode(procedureAdd.getSortCode());
        workingProcedure.setDelFlag(false);
        workingProcedure.setCreateBy(byUserName.getUsername());
        workingProcedure.setOrgCode(byUserName.getUseOrgCode());
        workingProcedureService.save(workingProcedure);
        return Result.ok();
    }

    @Override
    public Result<List<WorkingProcedureRes>> selWorkingProcedure(PageLimit pageLimit, WorkingProcedureAdd procedureAdd, String sub) {
        if (pageLimit.getPage() != -1 && pageLimit.getLimit() != -1) {
            PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        }
        List<WorkingProcedureRes> workingProcedureRes = workingProcedureService.selWorkingProcedure(pageLimit.getField(), pageLimit.getType(), procedureAdd.getOrderId(), procedureAdd.getLineId(), procedureAdd.getWorkCode(), procedureAdd.getWorkName());
        PageInfo<WorkingProcedureRes> pageInfo = new PageInfo<>(workingProcedureRes);
        return Result.ok(pageInfo.getList(), pageInfo.getTotal());
    }

    @Override
    public Result editWorkingProcedure(WorkingProcedureAdd procedureAdd, String sub) {
        if (StringUtils.isEmpty(procedureAdd.getWorkingProcedureId())) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR38);
        }
        QueryWrapper<DzWorkingProcedure> wp = new QueryWrapper<>();
        wp.eq("order_id",procedureAdd.getOrderId());
        wp.eq("line_id",procedureAdd.getLineId());
        wp.eq("work_code", procedureAdd.getWorkCode());
        DzWorkingProcedure one = workingProcedureService.getOne(wp);
        if (one != null) {
            if (!one.getWorkingProcedureId().equals(procedureAdd.getWorkingProcedureId())) {
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR39);
            }
        }

        QueryWrapper<DzWorkingProcedure> wpSort = new QueryWrapper<>();
        wpSort.eq("order_id",procedureAdd.getOrderId());
        wpSort.eq("line_id",procedureAdd.getLineId());
        wpSort.eq("sort_code", procedureAdd.getSortCode());
        DzWorkingProcedure wpSortPero = workingProcedureService.getOne(wpSort);
        if (wpSortPero != null) {
            if (!wpSortPero.getWorkingProcedureId().equals(procedureAdd.getWorkingProcedureId())) {
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR40);
            }
        }
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        DzWorkingProcedure workingProcedure = new DzWorkingProcedure();
        workingProcedure.setWorkingProcedureId(procedureAdd.getWorkingProcedureId());
        workingProcedure.setWorkCode(procedureAdd.getWorkCode());
        workingProcedure.setSortCode(procedureAdd.getSortCode());
        workingProcedure.setWorkName(procedureAdd.getWorkName());
        workingProcedure.setDepartId(Long.valueOf(procedureAdd.getDepartId()));
        workingProcedure.setLineId(Long.valueOf(procedureAdd.getLineId()));
        workingProcedure.setOrderId(Long.valueOf(procedureAdd.getOrderId()));
        workingProcedure.setUpdateBy(byUserName.getUsername());
        workingProcedure.setOrgCode(byUserName.getUseOrgCode());
        workingProcedureService.updateById(workingProcedure);
        return Result.ok();
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result delWorkingProcedure(String id, String sub) {
        List<DzWorkStationManagement> working_procedure_id = dzWorkStationManagementService.list(new QueryWrapper<DzWorkStationManagement>().eq("working_procedure_id", id));
        if (working_procedure_id.size() > 0) {
            log.warn("删除工序接口，工序下绑定了工位，不允许删除，工序id:{}", id);
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_119);
        }
        workingProcedureService.removeById(id);
        QueryWrapper<DzWorkingProcedureProduct> wp = new QueryWrapper<>();
        wp.eq("working_procedure_id", id);
        List<DzWorkingProcedureProduct> list = dzWorkingProcedureProductService.list(wp);
        if (CollectionUtils.isNotEmpty(list)) {
            List<String> procdeures = list.stream().map(dz -> dz.getWorkProcedProductId()).collect(Collectors.toList());
            dzWorkingProcedureProductService.removeByIds(procdeures);
            QueryWrapper<DzWorkDetectionTemplate> wpT = new QueryWrapper<>();
            wpT.in("work_proced_product_id", procdeures);
            dzWorkDetectionTemplateService.remove(wpT);
        }
        return Result.ok();
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result addProcedureProduct(DetectionProceduct detectionProceduct, String sub) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
//        工件id
        String productId = detectionProceduct.getProductId();
//        工序id
        String workingProcedureId = detectionProceduct.getWorkingProcedureId();
//        工序检测配置项
        List<DzDetectTempVo> dzDetectTempVos = detectionProceduct.getDzDetectTempVos();
        DzWorkingProcedureProduct workingProcedureProduct = new DzWorkingProcedureProduct();
        workingProcedureProduct.setProductId(Long.valueOf(productId));
        workingProcedureProduct.setWorkingProcedureId(workingProcedureId);
//      保存工序产品关系
        dzWorkingProcedureProductService.save(workingProcedureProduct);
//      保存检测项 和产品关系
        List<DzWorkDetectionTemplate> workDetectionTemplates = new ArrayList<>();
        dzDetectTempVos.stream().forEach(dzDetectTempVo -> {
            if (dzDetectTempVo.getIsShow() != null && dzDetectTempVo.getIsShow().intValue() == 0) {
                DzWorkDetectionTemplate workDetectionTemplate = new DzWorkDetectionTemplate();
                workDetectionTemplate.setWorkProcedProductId(workingProcedureProduct.getWorkProcedProductId());
                workDetectionTemplate.setDetectionId(Long.valueOf(dzDetectTempVo.getDetectionId()));
                workDetectionTemplate.setDetectionGroupId(dzDetectTempVo.getGroupId());
                workDetectionTemplate.setOrgCode(byUserName.getUseOrgCode());
                workDetectionTemplate.setDelFlag(false);
                workDetectionTemplate.setCreateBy(byUserName.getUsername());
                workDetectionTemplates.add(workDetectionTemplate);
            }
        });
        dzWorkDetectionTemplateService.saveBatch(workDetectionTemplates);
        return Result.ok();
    }

    @Override
    public Result selProductTemplate(String orderId, String lineId, String productNo, String sub) {
        List<DzDetectTempVo> tempVos = detectionTemplateService.selProductTemplateProductId(orderId,lineId,productNo);
        return Result.ok(tempVos, tempVos != null ? Long.valueOf(tempVos.size()) : 0L);
    }

    @Override
    public Result selProcedureProduct(ProcedIdproductNo productNo, String sub) {
        PageHelper.startPage(productNo.getPage(), productNo.getLimit());
        List<SelProcedureProduct> product = workingProcedureService.selProcedureProduct(productNo.getProductNo(), productNo.getWorkingProcedureId());
        PageInfo<SelProcedureProduct> info = new PageInfo<>(product);
        return Result.ok(info.getList(), Long.valueOf(info.getTotal()));
    }

    @Override
    public Result selEditProcedureProduct(String orderId, String lineId, String workProcedProductId, String sub) {
        DzWorkingProcedureProduct procedureProduct = dzWorkingProcedureProductService.getById(workProcedProductId);
        DzProduct byId = dzProductService.getById(procedureProduct.getProductId());
//        获取该产品所有检测配置项
        List<DzDetectTempVo> tempVos = detectionTemplateService.selProductTemplateProductId(orderId, lineId, byId.getProductNo());
//        获取已经配置的展示的检测项
        QueryWrapper<DzWorkDetectionTemplate> wp = new QueryWrapper<>();
        wp.eq("work_proced_product_id", workProcedProductId);
        List<DzWorkDetectionTemplate> list = dzWorkDetectionTemplateService.list(wp);
        if (CollectionUtils.isNotEmpty(tempVos)) {
            for (DzDetectTempVo tempVo : tempVos) {
                tempVo.setIsShow(1);
                if (CollectionUtils.isNotEmpty(list)) {
                    String detectionId = tempVo.getDetectionId();
                    for (DzWorkDetectionTemplate workDetectionTemplate : list) {
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
        SelEditDetectionProceduct proceduct = new SelEditDetectionProceduct();
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
        proceduct.setWorkProcedProductId(workProcedProductId);
        return Result.ok(proceduct);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result updateProcedureProduct(UpdateDetectionProceduct detectionProceduct, String sub) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        String productId = detectionProceduct.getProductId();
        String workProcedProductId = detectionProceduct.getWorkProcedProductId();

        QueryWrapper<DzWorkDetectionTemplate> wp = new QueryWrapper<>();
        wp.eq("work_proced_product_id", workProcedProductId);
        dzWorkDetectionTemplateService.remove(wp);

        List<DzDetectTempVo> dzDetectTempVos = detectionProceduct.getDzDetectTempVos();
        DzWorkingProcedureProduct procedureProduct = new DzWorkingProcedureProduct();
        procedureProduct.setWorkProcedProductId(workProcedProductId);
        procedureProduct.setProductId(Long.valueOf(productId));
        dzWorkingProcedureProductService.updateById(procedureProduct);
        //      保存检测项 和产品关系
        List<DzWorkDetectionTemplate> workDetectionTemplates = new ArrayList<>();
        dzDetectTempVos.stream().forEach(dzDetectTempVo -> {
            if (dzDetectTempVo.getIsShow().intValue() == 0) {
                DzWorkDetectionTemplate workDetectionTemplate = new DzWorkDetectionTemplate();
                workDetectionTemplate.setWorkProcedProductId(workProcedProductId);
                workDetectionTemplate.setDetectionId(Long.valueOf(dzDetectTempVo.getDetectionId()));
                workDetectionTemplate.setDetectionGroupId(dzDetectTempVo.getGroupId());
                workDetectionTemplate.setOrgCode(byUserName.getUseOrgCode());
                workDetectionTemplate.setDelFlag(false);
                workDetectionTemplate.setCreateBy(byUserName.getUsername());
                workDetectionTemplates.add(workDetectionTemplate);
            }
        });
        dzWorkDetectionTemplateService.saveBatch(workDetectionTemplates);
        return Result.ok();
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result delWorkProcedProductId(String workProcedProductId, String sub) {
        dzWorkingProcedureProductService.removeById(workProcedProductId);
        QueryWrapper<DzWorkDetectionTemplate> wp = new QueryWrapper<>();
        wp.eq("work_proced_product_id", workProcedProductId);
        dzWorkDetectionTemplateService.remove(wp);
        return Result.ok();
    }

    @Override
    public Result getWorkingProcedures(String sub) {
        List<WorkingProcedures> workingProcedures = dzWorkingProcedureProductService.getWorkingProcedures();
        return Result.ok(workingProcedures);
    }
}
