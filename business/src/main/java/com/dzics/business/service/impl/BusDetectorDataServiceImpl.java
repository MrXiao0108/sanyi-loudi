package com.dzics.business.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.business.framework.excel.CustomCellWriteHandler;
import com.dzics.business.model.response.proddetection.HeaderClom;
import com.dzics.business.model.response.proddetection.ProDetection;
import com.dzics.business.model.vo.detectordata.AddDetectorPro;
import com.dzics.business.model.vo.detectordata.GroupId;
import com.dzics.business.model.vo.detectordata.ProDuctCheck;
import com.dzics.business.model.vo.detectordata.check.DetectionTemplateParm;
import com.dzics.business.model.vo.detectordata.edit.EditProDuctTemp;
import com.dzics.business.service.*;
import com.dzics.business.service.cache.DzDetectionTemplCache;
import com.dzics.business.util.SnowflakeUtil;
import com.dzics.common.dao.*;
import com.dzics.common.enums.UserIdentityEnum;
import com.dzics.common.exception.CustomException;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.entity.*;
import com.dzics.common.model.request.DBDetectTempVo;
import com.dzics.common.model.request.DetectorDataQuery;
import com.dzics.common.model.request.DzDetectTempVo;
import com.dzics.common.model.request.product.DzProductDetectionTemplateParms;
import com.dzics.common.model.response.ProductParm;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.SwitchSiteDo;
import com.dzics.common.service.DzDetectorDataService;
import com.dzics.common.service.DzProductDetectionTemplateService;
import com.dzics.common.service.SysUserServiceDao;
import com.dzics.common.util.DateUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author ZhangChengJun
 * Date 2021/2/5.
 * @since
 */
@Slf4j
@Service
public class BusDetectorDataServiceImpl implements BusDetectorDataService {
    @Autowired
    private SnowflakeUtil snowflakeUtil;
    @Autowired
    private DzDetectionTemplCache dzDetectionTemplCache;
    @Autowired
    private SysUserServiceDao sysUserServiceDao;
    @Autowired
    private BusProDeteTempl busProDeteTempl;
    @Autowired
    private DzProductDetectionTemplateService proTemplateService;
    @Autowired
    private DzDetectorDataService dzDetectorDataService;
    @Autowired
    private BusinessOrderService businessOrderService;
    @Autowired
    private BusinessUserService businessUserService;
    @Autowired
    private BusinessProductService businessProductService;
    @Autowired
    private BusinessDepartService departService;
    @Autowired
    DzProductDetectionTemplateMapper dzProductDetectionTemplateMapper;
    @Autowired
    private DzWorkpieceDataMapper dzWorkpieceDataMapper;
    @Autowired
    private DzProductMapper dzProductMapper;
    @Autowired
    private DzOrderMapper dzOrderMapper;
    @Autowired
    private DzProductionLineMapper dzProductionLineMapper;
    @Autowired
    DateUtil dateUtil;

    @Override
    public Result selDetectorData(DetectorDataQuery detectorDataQuery, String sub) {
//        SysDepart byId = departService.getById(Long.valueOf(detectorDataQuery.getDepartId()));
//        if (byId == null) {
//            throw new CustomException(CustomExceptionType.USER_IS_NULL, CustomResponseCode.ERR13);
//    }
        //        根据key获取检测值
        ProDetection proDetection = new ProDetection();
        //
        String lineId = detectorDataQuery.getLineId();
        DzProductionLine dzProductionLine = dzProductionLineMapper.selectById(lineId);
        String orderId = dzProductionLine.getOrderId().toString();
        String orderNo = dzProductionLine.getOrderNo();
        String lineNo = dzProductionLine.getLineNo();
        if(dzProductionLine==null){
            log.error("检测记录查询，搜索条件产线id不存在:{}",lineId);
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
        }
        detectorDataQuery.setOrderNo(orderNo);
        detectorDataQuery.setLineNo(lineNo);
        //查询表头
        QueryWrapper<DzProductDetectionTemplate> wrapper = new QueryWrapper<>();
//        wrapper.eq("depart_id", detectorDataQuery.getDepartId());
        wrapper.eq("product_no", detectorDataQuery.getProductNo());
        wrapper.eq("is_show", 0);
        wrapper.select("table_col_val", "table_col_con");
        wrapper.eq("line_id", lineId);
        wrapper.eq("order_id", orderId);
        List<DzProductDetectionTemplate> dzProductDetectionTemplates = dzProductDetectionTemplateMapper.selectList(wrapper);
        List<HeaderClom> tableColumn = proDetection.getTableColumn();
        for (DzProductDetectionTemplate dzProductDetectionTemplate : dzProductDetectionTemplates) {
            HeaderClom headerClom = new HeaderClom();
            headerClom.setColData(dzProductDetectionTemplate.getTableColVal());
            headerClom.setColName(dzProductDetectionTemplate.getTableColCon());
            tableColumn.add(headerClom);
        }
        proDetection.setTableColumn(tableColumn);
        //查询数据
        //1.查询产品相关信息
        DzProduct dzProduct = dzProductMapper.selectOne(new QueryWrapper<DzProduct>().eq("product_no", detectorDataQuery.getProductNo()));
        PageHelper.startPage(detectorDataQuery.getPage(), detectorDataQuery.getLimit());
        if (detectorDataQuery.getType() == null || detectorDataQuery.getType().equals("")) {
            detectorDataQuery.setField("detector_time");
            detectorDataQuery.setType("DESC");
        }
        List<Map<String, Object>> resp = dzWorkpieceDataMapper.selDetectorData(detectorDataQuery);
        PageInfo<Map<String, Object>> info = new PageInfo(resp);
        List<Map<String, Object>> list = info.getList();
        for (Map<String, Object> objectMap : list) {
            objectMap.put("productName", dzProduct != null ? dzProduct.getProductName() : "默认产品");
//            objectMap.put("departName", byId.getDepartName());
            String res = objectMap.get("out_ok").toString().equals("1") ? "正常" : "异常";
            objectMap.put("detectionResult", res);
            if (objectMap.get("out_ok01") != null && objectMap.get("out_ok01").toString().equals("0")) {
                objectMap.put("detect01", objectMap.get("detect01") + "::");
            }
            if (objectMap.get("out_ok02") != null && objectMap.get("out_ok02").toString().equals("0")) {
                objectMap.put("detect02", objectMap.get("detect02") + "::");
            }
            if (objectMap.get("out_ok03") != null && objectMap.get("out_ok03").toString().equals("0")) {
                objectMap.put("detect03", objectMap.get("detect03") + "::");
            }
            if (objectMap.get("out_ok04") != null && objectMap.get("out_ok04").toString().equals("0")) {
                objectMap.put("detect04", objectMap.get("detect04") + "::");
            }
            if (objectMap.get("out_ok05") != null && objectMap.get("out_ok05").toString().equals("0")) {
                objectMap.put("detect05", objectMap.get("detect05") + "::");
            }
            if (objectMap.get("out_ok06") != null && objectMap.get("out_ok06").toString().equals("0")) {
                objectMap.put("detect06", objectMap.get("detect06") + "::");
            }
            if (objectMap.get("out_ok07") != null && objectMap.get("out_ok07").toString().equals("0")) {
                objectMap.put("detect07", objectMap.get("detect07") + "::");
            }
            if (objectMap.get("out_ok08") != null && objectMap.get("out_ok08").toString().equals("0")) {
                objectMap.put("detect08", objectMap.get("detect08") + "::");
            }
            if (objectMap.get("out_ok09") != null && objectMap.get("out_ok09").toString().equals("0")) {
                objectMap.put("detect09", objectMap.get("detect09") + "::");
            }
            if (objectMap.get("out_ok10") != null && objectMap.get("out_ok10").toString().equals("0")) {
                objectMap.put("detect10", objectMap.get("detect10") + "::");
            }
            if (objectMap.get("out_ok11") != null && objectMap.get("out_ok11").toString().equals("0")) {
                objectMap.put("detect11", objectMap.get("detect11") + "::");
            }
            if (objectMap.get("out_ok12") != null && objectMap.get("out_ok12").toString().equals("0")) {
                objectMap.put("detect12", objectMap.get("detect12") + "::");
            }
            if (objectMap.get("out_ok13") != null && objectMap.get("out_ok13").toString().equals("0")) {
                objectMap.put("detect13", objectMap.get("detect13") + "::");
            }
            if (objectMap.get("out_ok14") != null && objectMap.get("out_ok14").toString().equals("0")) {
                objectMap.put("detect14", objectMap.get("detect14") + "::");
            }
            if (objectMap.get("out_ok15") != null && objectMap.get("out_ok15").toString().equals("0")) {
                objectMap.put("detect15", objectMap.get("detect15") + "::");
            }
            if (objectMap.get("out_ok16") != null && objectMap.get("out_ok16").toString().equals("0")) {
                objectMap.put("detect16", objectMap.get("detect16") + "::");
            }
            if (objectMap.get("out_ok17") != null && objectMap.get("out_ok17").toString().equals("0")) {
                objectMap.put("detect17", objectMap.get("detect17") + "::");
            }
            if (objectMap.get("out_ok18") != null && objectMap.get("out_ok18").toString().equals("0")) {
                objectMap.put("detect18", objectMap.get("detect18") + "::");
            }
            if (objectMap.get("out_ok19") != null && objectMap.get("out_ok19").toString().equals("0")) {
                objectMap.put("detect19", objectMap.get("detect19") + "::");
            }
            if (objectMap.get("out_ok20") != null && objectMap.get("out_ok20").toString().equals("0")) {
                objectMap.put("detect20", objectMap.get("detect20") + "::");
            }
            if (objectMap.get("out_ok21") != null && objectMap.get("out_ok21").toString().equals("0")) {
                objectMap.put("detect21", objectMap.get("detect21") + "::");
            }
            if (objectMap.get("out_ok22") != null && objectMap.get("out_ok22").toString().equals("0")) {
                objectMap.put("detect22", objectMap.get("detect22") + "::");
            }
            if (objectMap.get("out_ok23") != null && objectMap.get("out_ok23").toString().equals("0")) {
                objectMap.put("detect23", objectMap.get("detect23") + "::");
            }
            if (objectMap.get("out_ok24") != null && objectMap.get("out_ok24").toString().equals("0")) {
                objectMap.put("detect24", objectMap.get("detect24") + "::");
            }
            if (objectMap.get("out_ok25") != null && objectMap.get("out_ok25").toString().equals("0")) {
                objectMap.put("detect25", objectMap.get("detect25") + "::");
            }
            if (objectMap.get("out_ok26") != null && objectMap.get("out_ok26").toString().equals("0")) {
                objectMap.put("detect26", objectMap.get("detect26") + "::");
            }
            if (objectMap.get("out_ok27") != null && objectMap.get("out_ok27").toString().equals("0")) {
                objectMap.put("detect27", objectMap.get("detect27") + "::");
            }
            if (objectMap.get("out_ok28") != null && objectMap.get("out_ok28").toString().equals("0")) {
                objectMap.put("detect28", objectMap.get("detect28") + "::");
            }
        }
        proDetection.setTableData(list);
        proDetection.setTableColumn(tableColumn);
        return new Result(CustomExceptionType.OK, proDetection, info.getTotal());
    }

    @Override
    public Result selDetectorItem(GroupId groupId, String sub) {
        DetectionTemplateParm parm = new DetectionTemplateParm();
        if (!groupId.getCheckModel()) {
            if (groupId != null && groupId.getDepartId() != null) {
                List<ProductParm> productParms = dzDetectionTemplCache.getByDepartId(groupId.getDepartId());
                if (CollectionUtils.isNotEmpty(productParms)) {
                    for (ProductParm productParm : productParms) {
                        productParm.setProductName(productParm.getProductName() + "-" + productParm.getLineType());
                    }
                }
                parm.setProducts(productParms);
                return new Result(CustomExceptionType.OK, parm);
            } else {
                return new Result(CustomExceptionType.OK, parm);
            }
        }
//        获取站点信息
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        Result result = businessUserService.querySwitchSite(byUserName, true);
        parm.setDeparts(result.getData());
        if (groupId != null && groupId.getDepartId() != null) {
            List<ProductParm> productParms = dzDetectionTemplCache.getByDepartId(groupId.getDepartId());
            if (CollectionUtils.isNotEmpty(productParms)) {
                for (ProductParm productParm : productParms) {
                    productParm.setProductName(productParm.getProductName() + "-" + productParm.getLineType());
                }
            }
            parm.setProducts(productParms);
        }
        if (groupId != null && !StringUtils.isEmpty(groupId.getProductNo())) {
            parm.setProductNo(groupId.getProductNo());
            if (groupId.getEditingMode()) {
                List<DBDetectTempVo> tempVos = dzDetectionTemplCache.getEditDBDetectorItem(groupId.getGroupId());
                if (CollectionUtils.isNotEmpty(tempVos)) {
                    DBDetectTempVo dbDetectTempVo = tempVos.get(0);
                    String lineType = dbDetectTempVo.getLineType();
                    String orderId = dbDetectTempVo.getOrderId();
                    parm.setLineType(lineType);
                    parm.setOrderId(orderId);
                }
                parm.setDbDetectTempVos(tempVos);
            } else {
                List<DzDetectTempVo> tempVos = dzDetectionTemplCache.getEditDetectorItem(groupId.getGroupId());
                if (CollectionUtils.isNotEmpty(tempVos)) {
                    DzDetectTempVo dbDetectTempVo = tempVos.get(0);
                    String lineType = dbDetectTempVo.getLineType();
                    String orderId = dbDetectTempVo.getOrderId();
                    parm.setLineType(lineType);
                    parm.setOrderId(orderId);
                }
                parm.setDzDetectTempVos(tempVos);
            }

        }
        if (groupId != null && groupId.getGroupId() != null) {
            if (!StringUtils.isEmpty(groupId.getProductNo())) {
                Long departId = dzDetectionTemplCache.getByProeuctNoDepartId(groupId.getProductNo());
                parm.setDepartId(departId);
            }
            return new Result(CustomExceptionType.OK, parm);
        } else {
            if (!groupId.getEditingMode()) {
                List<DzDetectTempVo> tempVos = dzDetectionTemplCache.list();
                parm.setDzDetectTempVos(tempVos);
            }
            return new Result(CustomExceptionType.OK, parm);
        }
    }

    @Transactional(rollbackFor = Throwable.class, isolation = Isolation.SERIALIZABLE)
    @Override
    public Result addDetectorItem(AddDetectorPro detectorPro, String sub) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        long groupId = snowflakeUtil.nextId();
        Integer count = busProDeteTempl.getProductNo(detectorPro.getProductNo(),
                Long.valueOf(detectorPro.getDepartId()), detectorPro.getOrderId(), detectorPro.getLineId());
        if (count == null || count > 0) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR34);
        }
        String productNo = detectorPro.getProductNo();
        List<DzDetectTempVo> detectionTemplates = detectorPro.getDetectionTemplates();
        List<DzProductDetectionTemplate> addtempLs = new ArrayList<>();
        for (DzDetectTempVo detectionTemplate : detectionTemplates) {
            DzProductDetectionTemplate addtemp = new DzProductDetectionTemplate();
            BeanUtils.copyProperties(detectionTemplate, addtemp);
            addtemp.setOrderId(Long.valueOf(detectorPro.getOrderId()));
            addtemp.setLineId(Long.valueOf(detectorPro.getLineId()));
            addtemp.setLineNo(detectorPro.getLineNo());
            addtemp.setOrderNo(detectorPro.getOrderNo());
            addtemp.setGroupId(groupId);
            addtemp.setProductNo(productNo);
            addtemp.setDepartId(Long.valueOf(detectorPro.getDepartId()));
//            addtemp.setOrderNo(orderNo);
            addtemp.setOrgCode(byUserName.getUseOrgCode());
            addtemp.setCreateBy(byUserName.getUsername());
            addtemp.setDetectionId(null);
            addtempLs.add(addtemp);

        }
        boolean save = busProDeteTempl.save(addtempLs);
        if (save) {
            return new Result(CustomExceptionType.OK);
        }
        throw new CustomException(CustomExceptionType.SYSTEM_ERROR);
    }

    @Override
    public Result<List<LinkedHashMap<String, Object>>> queryProDetectorItem(ProDuctCheck proDuctCheck, String sub) {
        String orderId = proDuctCheck.getOrderId();
        String lineId = proDuctCheck.getLineId();
        Result result = new Result(CustomExceptionType.OK);
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
//        归属站点id
        Long affiliationDepartId = byUserName.getAffiliationDepartId();
        List<DzProductDetectionTemplateParms> templates = new ArrayList<>();
        SwitchSiteDo byOrgCode = departService.getByOrgCode(byUserName.getUseOrgCode());
        PageHelper.startPage(proDuctCheck.getPage(), proDuctCheck.getLimit());
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode()) {
            if (byUserName.getUseOrgCode().equals(byUserName.getOrgCode())) {
                List<DzProductDetectionTemplateParms> listx = proTemplateService.listGroupBy(proDuctCheck.getField(), proDuctCheck.getType(), proDuctCheck.getProductName(), null, orderId, lineId);
                PageInfo<DzProductDetectionTemplateParms> dzProductDetectionTemplatePageInfo = new PageInfo<>(listx);
                templates = dzProductDetectionTemplatePageInfo.getList();
                result.setCount(dzProductDetectionTemplatePageInfo.getTotal());
            } else {
                List<DzProductDetectionTemplateParms> listx = proTemplateService.listGroupBy(proDuctCheck.getField(), proDuctCheck.getType(), proDuctCheck.getProductName(), byOrgCode.getId(), orderId, lineId);
                PageInfo<DzProductDetectionTemplateParms> dzProductDetectionTemplatePageInfo = new PageInfo<>(listx);
                templates = dzProductDetectionTemplatePageInfo.getList();
                result.setCount(dzProductDetectionTemplatePageInfo.getTotal());
            }
        } else {
            List<DzProductDetectionTemplateParms> listx = proTemplateService.listGroupBy(proDuctCheck.getField(), proDuctCheck.getType(), proDuctCheck.getProductName(), affiliationDepartId, orderId, lineId);
            PageInfo<DzProductDetectionTemplateParms> dzProductDetectionTemplatePageInfo = new PageInfo<>(listx);
            templates = dzProductDetectionTemplatePageInfo.getList();
            result.setCount(dzProductDetectionTemplatePageInfo.getTotal());
        }
        List<LinkedHashMap<String, Object>> resp = new ArrayList<>();
        for (int ix = 0; ix < templates.size(); ix++) {
            DzProductDetectionTemplateParms detectionTemplateGr = templates.get(ix);
            String orderId1 = detectionTemplateGr.getOrderId();
            String lineId1 = detectionTemplateGr.getLineId();
            List<DzProductDetectionTemplate> onedzproTem = dzDetectionTemplCache.getByOrderNoProNo(Long.valueOf(detectionTemplateGr.getDepartId()),
                    detectionTemplateGr.getProductNo(),orderId1,lineId1);
            LinkedHashMap<String, Object> taberHeader = new LinkedHashMap<>();
            taberHeader.put("productName", detectionTemplateGr.getProductName());
            taberHeader.put("departId", detectionTemplateGr.getDepartId().toString());
            taberHeader.put("productNo", detectionTemplateGr.getProductNo());
            taberHeader.put("orderId", detectionTemplateGr.getOrderId());
            taberHeader.put("orderNo", detectionTemplateGr.getOrderNo());
            taberHeader.put("lineId", detectionTemplateGr.getLineId());
            taberHeader.put("lineNo", detectionTemplateGr.getLineNo());
            taberHeader.put("lineName", detectionTemplateGr.getLineName());
            for (int i1 = 0; i1 < onedzproTem.size(); i1++) {
                DzProductDetectionTemplate detectionTemplate = onedzproTem.get(i1);
                if (i1 == 0) {
                    taberHeader.put("groupId", detectionTemplate.getGroupId().toString());
                }
                taberHeader.put(detectionTemplate.getTableColVal(), detectionTemplate.getTableColCon());
                if ((i1 + 1) < 10) {
                    taberHeader.put("compensationValue0" + (i1 + 1), detectionTemplate.getCompensationValue());
                } else {
                    taberHeader.put("compensationValue" + (i1 + 1), detectionTemplate.getCompensationValue());
                }
            }
            resp.add(taberHeader);
        }
        result.setData(resp);
        return result;
    }


    @Override
    public Result delProDetectorItem(Long groupId, String sub) {
        boolean bb = dzDetectionTemplCache.delGroupupId(groupId);
        if (bb) {
            return new Result(CustomExceptionType.OK);
        }
        throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR0);
    }


    @Override
    public Result editProDetectorItem(EditProDuctTemp templateParm, String sub) {
        return dzDetectionTemplCache.editProDetectorItem(templateParm);
    }

    @Override
    public Result dbProDetectorItem(EditProDuctTemp editProDuctTemp, String sub) {
        return dzDetectionTemplCache.dbProDetectorItem(editProDuctTemp);
    }

    @Override
    public void getDetectorExcel(HttpServletResponse response, DetectorDataQuery detectorDataQuery, String sub) {
        try {
            String userIdentity = detectorDataQuery.getUserIdentity();
            if (userIdentity == null) {
                log.error("导出检测数据excel异常，userIdentity为空:{}", userIdentity);
                return;
            }
            detectorDataQuery.setPage(1);
            detectorDataQuery.setLimit(10000);
            Result result = selDetectorData(detectorDataQuery, sub);
            ProDetection proDetection = (ProDetection) result.getData();
            List<HeaderClom> tableColumnData = proDetection.getTableColumn();//表头对象
            List<HeaderClom> tableColumn = new ArrayList<>();
            for (HeaderClom headerClom : tableColumnData) {
                if (userIdentity.equals("1")) {
                    tableColumn.add(headerClom);
                } else if (!headerClom.getColData().equals("departName")) {
                    tableColumn.add(headerClom);
                }
            }
            List<Map<String, Object>> tableData = proDetection.getTableData();//数据
            if (tableData.size() == 0) {
                log.warn("导出检测数据excel，没有查询到检测数据，搜索条件：{}", detectorDataQuery);
                return;
            }
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 设置要导出的文件的名字
            String fileName = new String("检测数据".getBytes("utf-8"), "ISO-8859-1") + format.format(new Date()) + ".xls";
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("检测数据");
            HSSFRow row = sheet.createRow(0);
            for (int i = 0; i < tableColumn.size(); i++) {
                HSSFCell cell = row.createCell(i);
                HSSFRichTextString text = new HSSFRichTextString(tableColumn.get(i).getColName());
                cell.setCellValue(text);
            }
            //在表中存放查询到的数据放入对应的列
            for (int i = 0; i < tableData.size(); i++) {
                Map<String, Object> map = tableData.get(i);
                HSSFRow row1 = sheet.createRow(i + 1);
                for (int j = 0; j < tableColumn.size(); j++) {
                    String colData = tableColumn.get(j).getColData();
                    Object o1 = map.get(colData);
                    String o = o1 != null ? o1.toString() : "";
                    if (o != null && o.indexOf(":") > 0) {
                        o = o.substring(0, o.length() - 2);
                    }
                    row1.createCell(j).setCellValue(o);
                }
            }
            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName);
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("导出检测数据Excel表格失败");
        }

    }

    @Override
    public void getDetectorExcel1(HttpServletResponse response, DetectorDataQuery detectorDataQuery, String sub) {
        try {
            String userIdentity = detectorDataQuery.getUserIdentity();
            if (userIdentity == null) {
                log.error("导出检测数据excel异常，userIdentity为空:{}", userIdentity);
                return;
            }
            detectorDataQuery.setPage(1);
            detectorDataQuery.setLimit(10000);
            Result result = selDetectorData(detectorDataQuery, sub);
            ProDetection proDetection = (ProDetection) result.getData();
            List<HeaderClom> tableColumn = new ArrayList<>();
            List<List<String>> header = new ArrayList<>();//头
            for (HeaderClom headerClom : proDetection.getTableColumn()) {
                List<String> head = new ArrayList<>();
                head.add(headerClom.getColName());
                if (userIdentity.equals("1")) {
                    tableColumn.add(headerClom);
                    header.add(head);
                } else if (!headerClom.getColData().equals("departName")) {
                    tableColumn.add(headerClom);
                    header.add(head);
                }
            }
            List<Map<String, Object>> tableData = proDetection.getTableData();//数据
            if (tableData.size() == 0) {
                log.warn("导出检测数据excel，没有查询到检测数据，搜索条件：{}", detectorDataQuery);
                return;
            }
            //在表中存放查询到的数据放入对应的列
            List<List<String>> data = new ArrayList<>();//数据
            for (int i = 0; i < tableData.size(); i++) {
                List<String> dataList = new ArrayList<>();
                Map<String, Object> map = tableData.get(i);
                for (int j = 0; j < tableColumn.size(); j++) {
                    String colData = tableColumn.get(j).getColData();
                    Object o1 = map.get(colData);
                    String o = o1 != null ? o1.toString() : "";

                    if (o != null && o.indexOf(":") > 0) {
                        o = o.substring(0, o.length() - 2);
                    }
                    dataList.add(o);
                }
                data.add(dataList);
            }
            //导出excel
            downloadExcel(response, "检测数据", "检测记录", header, data);
        } catch (Exception e) {
            log.error("导出检测数据Excel表格失败:{}", e.getMessage(), e);
        }

    }

    public void downloadExcel(HttpServletResponse response, String fileName, String sheet, List<List<String>> header, List<List<String>> data) throws IOException {
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
        fileName = URLEncoder.encode(fileName + dateUtil.dateFormatToStingYmdHms(new Date()) + ".xlsx", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        EasyExcel.write(response.getOutputStream()).registerWriteHandler(new CustomCellWriteHandler()).head(header).sheet(sheet).doWrite(data);
    }


}
