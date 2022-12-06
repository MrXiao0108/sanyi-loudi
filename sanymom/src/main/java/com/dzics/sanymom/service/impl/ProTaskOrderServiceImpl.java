package com.dzics.sanymom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.dao.DzWorkStationManagementMapper;
import com.dzics.common.dao.MomMaterialPointMapper;
import com.dzics.common.dao.MomMaterialWarehouseMapper;
import com.dzics.common.exception.CustomException;
import com.dzics.common.exception.RobRequestException;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.agv.search.MomResultSearch;
import com.dzics.common.model.agv.search.SearchDzdcMomSeqenceNo;
import com.dzics.common.model.agv.search.SearchNo;
import com.dzics.common.model.agv.search.SearchNoRes;
import com.dzics.common.model.constant.MomProgressStatus;
import com.dzics.common.model.constant.mom.MomReqContent;
import com.dzics.common.model.constant.mom.MomTaskType;
import com.dzics.common.model.constant.mom.MomVersion;
import com.dzics.common.model.entity.*;
import com.dzics.common.model.mom.response.RequestHeaderVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.DzProductService;
import com.dzics.common.service.MomOrderService;
import com.dzics.common.service.MomWaitCallMaterialReqService;
import com.dzics.common.service.MomWaitCallMaterialService;
import com.dzics.common.util.DateUtil;
import com.dzics.sanymom.config.MomRequestPath;
import com.dzics.sanymom.exception.CustomMomException;
import com.dzics.sanymom.framework.OperLogReportWork;
import com.dzics.sanymom.model.request.sany.ComponentList;
import com.dzics.sanymom.model.request.sany.OprSequenceList;
import com.dzics.sanymom.model.request.sany.Task;
import com.dzics.sanymom.service.ProTaskOrderService;
import com.dzics.sanymom.service.TaskMomOrderPathService;
import com.dzics.sanymom.service.TaskPathMaterialService;
import com.dzics.sanymom.service.impl.http.NextOperationMomHttpImpl;
import com.dzics.sanymom.util.RedisUniqueID;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @author ZhangChengJun
 * Date 2021/5/27.
 */
@Slf4j
@Service
public class ProTaskOrderServiceImpl implements ProTaskOrderService {
    @Autowired
    private MomOrderService orderService;
    @Autowired
    private TaskMomOrderPathService orderPathService;
    @Autowired
    private DateUtil dateUtil;
    @Autowired
    private RedisUniqueID redisUniqueID;
    @Autowired
    private TaskPathMaterialService pathMaterialService;
    @Autowired
    private DzProductService dzProductService;
    @Autowired
    private MomRequestPath momRequestPath;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MomWaitCallMaterialService momWaitCallMaterialService;
    @Autowired
    private MomWaitCallMaterialReqService waitCallMaterialReqService;
    @Autowired
    private MomMaterialPointMapper pointMapper;
    @Value("${business.robot.ip}")
    private String busIpPort;
    @Value("${business.robot.material.click.path}")
    private String materialClick;
    @Autowired
    private MomMaterialWarehouseMapper warehouseMapper;
    @Autowired
    private NextOperationMomHttpImpl nextOperationMomHttp;
    @Autowired
    private DzWorkStationManagementMapper workstationService;

    /**
     * 保存中控下发订单中的工序
     *
     * @param proTaskOrderId  订单ID
     * @param oprSequenceList 工序集合
     * @return
     */
    @Override
    public void saveOrderPath(String proTaskOrderId, List<OprSequenceList> oprSequenceList, String productNo) {
        List<MomOrderPath> momOrderPaths = new ArrayList<>();
        for (OprSequenceList sequenceList : oprSequenceList) {
            List<ComponentList> componentLists = sequenceList.getComponentList();
            List<MomOrderPathMaterial> materialList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(componentLists)) {
                for (ComponentList componentList : componentLists) {
                    String materialNo = componentList.getMaterialNo();
                    if (StringUtils.isEmpty(materialNo)) {
                        materialNo = productNo;
                    }
                    MomOrderPathMaterial pathMaterial = new MomOrderPathMaterial();
                    pathMaterial.setMaterialno(materialNo);
                    pathMaterial.setMaterialname(componentList.getMaterialName());
                    pathMaterial.setMaterialalias(componentList.getMaterialAlias());
                    pathMaterial.setQuantity(componentList.getQuantity());
                    pathMaterial.setReserveno(componentList.getReserveNo());
                    pathMaterial.setReservelineno(componentList.getReserveNo());
                    pathMaterial.setUnit(componentList.getUnit());
                    pathMaterial.setMomOrderId(proTaskOrderId);
                    materialList.add(pathMaterial);
                }
            } else {
                log.warn("工序:{},物料信息不存在：{}", sequenceList.getOprSequenceNo(), componentLists);
            }
            MomOrderPath momOrderPath = new MomOrderPath();
            momOrderPath.setMomOrderPathMaterials(materialList);
            momOrderPath.setMomOrderId(proTaskOrderId);
            momOrderPath.setOprsequenceno(sequenceList.getOprSequenceNo());
            momOrderPath.setSequenceno(sequenceList.getSequenceNo());
            momOrderPath.setOprsequencename(sequenceList.getOprSequenceName());
            momOrderPath.setOprsequencetype(sequenceList.getOprSequenceType());
            momOrderPath.setScheduledstartdate(sequenceList.getScheduledStartDate());
            momOrderPath.setScheduledcompletedate(sequenceList.getScheduledCompleteDate());
            momOrderPath.setWorkcenter(sequenceList.getWorkCenter());
            momOrderPath.setWorkcentername(sequenceList.getWorkCenterName());
            momOrderPath.setWorkstation(sequenceList.getWorkStation());
            momOrderPath.setWorkstationname(sequenceList.getWorkStationName());
            momOrderPath.setProgressstatus(sequenceList.getProgressStatus());
            momOrderPath.setQuantity(sequenceList.getQuantity());
            momOrderPaths.add(momOrderPath);
        }
        orderPathService.saveMomOrderPath(momOrderPaths);
        List<MomOrderPathMaterial> materialList = new ArrayList<>();
        for (MomOrderPath momOrderPath : momOrderPaths) {
            List<MomOrderPathMaterial> momOrderPathMaterials = momOrderPath.getMomOrderPathMaterials();
            momOrderPathMaterials.stream().forEach(material -> {
                material.setWorkingProcedureId(momOrderPath.getWorkingProcedureId());
            });
            materialList.addAll(momOrderPathMaterials);
        }
        pathMaterialService.savePathMaterials(materialList);
    }

    /**
     * 保存中控下发的订单
     *
     * @param task
     * @param wipOrderNo
     * @param taskId
     * @param version
     * @param taskType
     * @param line
     * @return
     */
    @Override
    public MonOrder saveTaskOrder(Task task, String wipOrderNo, String taskId, int version, String taskType, DzProductionLine line,String momParms) {
        List<OprSequenceList> list = task.getOprSequenceList();
        List<DzWorkStationManagement> dzWorkStationManagements = workstationService.selectList(new QueryWrapper<DzWorkStationManagement>()
                .eq("order_id", line.getOrderId()).isNotNull("dz_station_code").orderByAsc("sort_code"));
        DzWorkStationManagement station = dzWorkStationManagements.get(0);
        String OprSequenceName = null;
        Date stationStartTime =null;
        for (OprSequenceList oprSequenceList : list) {
            if(oprSequenceList.getWorkStation().equals(station.getDzStationCode())){
                if(oprSequenceList.getScheduledStartDate()==null){
                    stationStartTime = dateUtil.stringDateToformatDateYmdHms(task.getScheduledStartDate());
                }else {
                    stationStartTime = oprSequenceList.getScheduledStartDate();
                }
                OprSequenceName = oprSequenceList.getOprSequenceName();
                break;
            }
        }
        MonOrder monOrder = new MonOrder();
        monOrder.setTasktype(taskType);
        monOrder.setTaskid(taskId);
        monOrder.setVersion(version);
        monOrder.setProductAliasProductionLine(task.getProductAlias() + task.getProductionLine());
        monOrder.setWiporderno(wipOrderNo);
        monOrder.setWipOrderType(task.getWipOrderType());
        monOrder.setProductionLine(task.getProductionLine());
        monOrder.setWorkCenter(task.getWorkCenter());
        monOrder.setWipOrderGroup(task.getWipOrderGroup());
        monOrder.setGroupCount(task.getGroupCount());
        monOrder.setProductNo(task.getProductNo());
        monOrder.setProductName(task.getProductName());
        monOrder.setProductAlias(task.getProductAlias());
        monOrder.setFacility(task.getFacility());
        monOrder.setQuantity(task.getQuantity());
        monOrder.setOprSequenceName(OprSequenceName);
        monOrder.setScheduledStartDate(stationStartTime);
        monOrder.setScheduledCompleteDate(dateUtil.stringDateToformatDateYmdHms(task.getScheduledCompleteDate()));
        monOrder.setOrgCode("MOM");
        monOrder.setDelFlag(false);
        monOrder.setCreateBy("MOM");
        //monOrder.setProgressStatus(task.getProgressStatus());
        monOrder.setProgressStatus(MomProgressStatus.DOWN);
        monOrder.setOrderOldState(MomProgressStatus.DOWN);
        monOrder.setOrderOperationResult(2);
        monOrder.setOrderId(line.getId());
        monOrder.setLineId(line.getOrderId());
        monOrder.setJsonOriginalData(momParms);
        //查询产品
        QueryWrapper<DzProduct> wrapper = new QueryWrapper<DzProduct>()
                .eq("sy_product_alias", task.getProductAlias())
                .eq("line_type", line.getLineType());
        List<DzProduct> data = dzProductService.list(wrapper);
        if (data.size() == 0) {
            log.error("还未配置相应产品，接收mom订单失败.订单详情:{}", task);
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR82);
        }
        DzProduct dzProduct = data.get(0);
        monOrder.setProductId(dzProduct.getProductId().toString());
        boolean save = orderService.save(monOrder);
        return monOrder;
    }

    /**
     * 保存订单待叫料信息
     *
     * @param line
     * @param monOrder
     * @param version
     * @param taskId
     */
    @Override
    public void saveTaskOrderMaterial(DzProductionLine line, Task task, MonOrder monOrder, int version, String taskId) {
        List<OprSequenceList> oprSequenceList = task.getOprSequenceList();//该订单 所有工序
        if (CollectionUtils.isEmpty(oprSequenceList)) {
            log.error("保存订单待叫料信息，订单下工序集合为空，订单详情:{}", task);
            throw new CustomMomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR41.getChinese(), version, taskId);
        }
//        获取当前岛的所有工位
        List<String> stationCode = pointMapper.getOrderNoLineNo(line.getOrderNo(), line.getLineNo());
        if (CollectionUtils.isEmpty(stationCode)) {
            throw new CustomMomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, "中控没有设置工位信息", version, taskId);
        }
        List<MomWaitCallMaterial> listWaitCallMa = new ArrayList<>();
        Map<String, List<ComponentList>> mp = new HashMap<>();
        for (OprSequenceList data : oprSequenceList) {
            List<ComponentList> componentList = data.getComponentList();
            if (CollectionUtils.isEmpty(componentList)) {
                log.warn("保存订单解析物料数量，物料为空,跳过该工序,解析下一个工序，订单详情:{}", task);
                throw new CustomMomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR85.getChinese(), version, taskId);
            }

            for (ComponentList component : componentList) {
                String materialNo = component.getMaterialNo();
                if (StringUtils.isEmpty(materialNo)) {
                    materialNo = task.getProductNo();
                }
                String productName = component.getMaterialName();
                if(StringUtils.isEmpty(productName)){
                    productName = task.getProductName();
                }
                int quantity = component.getQuantity();
                if (quantity <= 0) {
                    quantity = task.getQuantity();
                    log.warn("保存订单解析物料:{} 数量，组件物料数量为 0,使用订单数量作为组件物料数量:{}", materialNo, quantity);
                }
                MomWaitCallMaterial momWaitCallMaterial = new MomWaitCallMaterial();
                momWaitCallMaterial.setOrderNo(line.getOrderNo());
                momWaitCallMaterial.setLineNo(line.getLineNo());
                momWaitCallMaterial.setReqSys(MomReqContent.REQ_SYS);
                momWaitCallMaterial.setSequenceNo(data.getSequenceNo());
                momWaitCallMaterial.setFacility(task.getFacility());
                momWaitCallMaterial.setWipOrderNo(task.getWipOrderNo());
                momWaitCallMaterial.setProductNo(task.getProductNo());
                momWaitCallMaterial.setMaterialName(productName);
                momWaitCallMaterial.setMomOrderId(monOrder.getProTaskOrderId());
                momWaitCallMaterial.setMaterialType("2");//1产品物料 2 组件物料
                momWaitCallMaterial.setOprSequenceNo(data.getOprSequenceNo());//工序号
                momWaitCallMaterial.setMaterialNo(materialNo);//物料编码
                momWaitCallMaterial.setQuantity(quantity);//叫料总数量
                momWaitCallMaterial.setSucessQuantity(0);//已叫料总数量
                momWaitCallMaterial.setSurplusQuantity(quantity);//剩余叫料总数
                momWaitCallMaterial.setFalgStatus(false);
                momWaitCallMaterial.setFalgOrderStatus(0);
                momWaitCallMaterial.setOrgCode(monOrder.getOrgCode());
                momWaitCallMaterial.setDelFlag(false);
                momWaitCallMaterial.setWorkStation(data.getWorkStation());
                listWaitCallMa.add(momWaitCallMaterial);
            }

            //遍历组件物料
            for (String stCode : stationCode) {
                for (ComponentList component : componentList) {
                    String materialNo = component.getMaterialNo();
                    if (StringUtils.isEmpty(materialNo)) {
                        materialNo = task.getProductNo();
                    }
                    String workStation = data.getWorkStation();
                    if (stCode.contains(",")) {
                        String[] split = stCode.split(",");
                        for (String code : split) {
                            if (code.equals(workStation)) {
                                List<ComponentList> list = mp.get(materialNo);
                                if (CollectionUtils.isEmpty(list)) {
                                    list = new ArrayList<>();
                                    list.add(component);
                                    mp.put(materialNo, list);
                                } else {
                                    list.add(component);
                                }
                            }
                        }
                    } else {
                        if (stCode.equals(workStation)) {
                            List<ComponentList> oprSequenceLists = mp.get(materialNo);
                            if (CollectionUtils.isEmpty(oprSequenceLists)) {
                                oprSequenceLists = new ArrayList<>();
                                oprSequenceLists.add(component);
                                mp.put(materialNo, oprSequenceLists);
                            } else {
                                oprSequenceLists.add(component);
                            }
                        }
                    }
                }
            }

        }
        momWaitCallMaterialService.saveBatch(listWaitCallMa);
        for (Map.Entry<String, List<ComponentList>> stringListEntry : mp.entrySet()) {
            String materialNo = stringListEntry.getKey();
            List<ComponentList> oprSequenceLists = stringListEntry.getValue();
//           合计组件数量
            int sum = 0;
            for (ComponentList componentList : oprSequenceLists) {
                int quantity = componentList.getQuantity();
                if (quantity <= 0) {
                    quantity = task.getQuantity();
                }
                sum += quantity;
            }
            MomMaterialWarehouse warehouse = warehouseMapper.getOrderAndMaterial(line.getOrderNo(), line.getLineNo(), materialNo);
            if (warehouse == null) {
                warehouse = new MomMaterialWarehouse();
                warehouse.setOrderNo(line.getOrderNo());
                warehouse.setLineNo(line.getLineNo());
                warehouse.setMaterialNo(materialNo);
                warehouse.setQuantity((long) sum);
                warehouse.setTotalQuantity((long) sum);
                warehouseMapper.insert(warehouse);
            } else {
                warehouse.setQuantity(warehouse.getQuantity() + sum);
                warehouse.setTotalQuantity(warehouse.getTotalQuantity() + sum);
                warehouseMapper.updateById(warehouse);
            }
        }
    }


    /**
     * 查询工序号
     *
     * @return
     */
    @OperLogReportWork(operModul = "查询下个工序",operDesc = "查询下个工序")
    @Override
    public Result searechOprSequenceNo(SearchDzdcMomSeqenceNo momSeqenceNo) {
        SearchNo searchNo = new SearchNo();
        searchNo.setReqSys(MomReqContent.REQ_SYS);
        searchNo.setFacility(MomReqContent.FACILITY);
        searchNo.setWipOrderNo(momSeqenceNo.getWipOrderNo());
        searchNo.setSequenceNo(MomReqContent.SEQUENCENO);
        searchNo.setOprSequenceNo(momSeqenceNo.getOprSequenceNo());
        RequestHeaderVo<SearchNo> headerVo = new RequestHeaderVo<>();
        headerVo.setTaskType(MomTaskType.nextOprSeqNo);
        headerVo.setReported(searchNo);
        headerVo.setVersion(MomVersion.VERSION);
        headerVo.setTaskId(redisUniqueID.getUUID());
        Gson gson = new Gson();
        String reqJson = gson.toJson(headerVo);
        MomResultSearch body = nextOperationMomHttp.sendPost(redisUniqueID.getGroupId(), momSeqenceNo.getOrderCode(), momSeqenceNo.getLineNo(), momSeqenceNo.getGroupId(), momRequestPath.ipPortPath, headerVo, MomResultSearch.class);
        log.info("请求MOM 下个工序工序号 地址：{}  ,请求参数:{}, 返回信息：{}", momRequestPath.ipPortPath, reqJson, gson.toJson(body));
        if (body == null) {
            throw new RobRequestException(CustomResponseCode.ERR60);
        }
        if (!MomReqContent.MOM_CODE_OK.equals(body.getCode())) {
            log.error("请求MOM查询料框接口错误 body: {}", body);
            throw new RobRequestException(body.getMsg());
        }
        SearchNoRes noRes = body.getReturnData();
        if (noRes == null) {
            log.error("返回下个工序号 returnData 为空 body :{}", body);
            throw new RobRequestException(CustomResponseCode.ERR61);
        }
        String nextOprSeqNo = noRes.getNextOprSeqNo();
        if (StringUtils.isEmpty(nextOprSeqNo)) {
            log.error("查询下个工序号：MOM返回下个工序号为空：body ：{}", body);
            throw new RobRequestException(CustomResponseCode.ERR62);
        }
        return Result.ok(body);
    }

}
