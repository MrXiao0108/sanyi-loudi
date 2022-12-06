package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.DzWorkReportSortMapper;
import com.dzics.common.dao.MomOrderMapper;
import com.dzics.common.dao.MomOrderPathMaterialMapper;
import com.dzics.common.enums.Message;
import com.dzics.common.exception.CustomException;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.constant.DzUdpType;
import com.dzics.common.model.constant.MomProgressStatus;
import com.dzics.common.model.custom.OrderIdLineId;
import com.dzics.common.model.custom.StartWokeOrderMooM;
import com.dzics.common.model.entity.*;
import com.dzics.common.model.request.dzcheck.DzOrderCheck;
import com.dzics.common.model.request.mom.AddMomOrder;
import com.dzics.common.model.request.mom.GetMomOrderVo;
import com.dzics.common.model.request.mom.MaterialAddParms;
import com.dzics.common.model.request.mom.PutMomOrder;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.mom.GetMomOrderDo;
import com.dzics.common.service.*;
import com.dzics.common.util.DateUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * mom下发订单表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-27
 */
@Service
@Slf4j
public class MomOrderServiceImpl extends ServiceImpl<MomOrderMapper, MonOrder> implements MomOrderService {

    @Autowired
    private DzProductionLineService dzProductionLineService;
    @Autowired
    private DzProductionLineService lineService;
    @Autowired
    private DzProductService productService;

    @Autowired
    private SysUserServiceDao sysUserServiceDao;
    @Autowired
    private MomOrderPathService orderPathService;
    @Autowired
    private DzWorkReportSortMapper sortMapper;
    @Autowired
    private MomOrderPathMaterialService orderPathMaterialService;
    @Autowired
    private MomOrderPathMaterialMapper momOrderPathMaterialMapper;
    @Autowired
    DateUtil dateUtil;
    @Autowired
    private MomWaitCallMaterialService momWaitCallMaterialService;
    @Autowired
    private MomOrderMapper momOrderMapper;

    @Override
    public List<MonOrder> getWipOrderNo(String wipOrderNo, String orderNo, String lineNo) {
        OrderIdLineId lid = dzProductionLineService.getOrderNoAndLineNo(orderNo, lineNo);
        if (lid == null) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR59);
        }
        Long lineId = lid.getLineId();
        Long orderId = lid.getOrderId();
        QueryWrapper<MonOrder> wp = new QueryWrapper<>();
        wp.eq("WipOrderNo", wipOrderNo);
        wp.eq("order_id", orderId);
        wp.eq("line_id", lineId);
        List<MonOrder> list = list(wp);
        return list;
    }

    @Override
    public MonOrder getMomOrder(String orderNo, String lineNo, String loading) {
        OrderIdLineId orderNoAndLineNo = dzProductionLineService.getOrderNoAndLineNo(orderNo, lineNo);
        if (orderNoAndLineNo == null) {
            return null;
        }
        Long orderId = orderNoAndLineNo.getOrderId();
        Long lineId = orderNoAndLineNo.getLineId();
        return getMomOrder(orderId, lineId, loading);
    }

    private MonOrder getMomOrder(Long order, Long line, String loading) {
        QueryWrapper<MonOrder> wp = new QueryWrapper<>();
        wp.eq("order_id", order);
        wp.eq("line_id", line);
        wp.eq("ProgressStatus", loading);
        wp.eq("order_operation_result", 2);
        List<MonOrder> list = list(wp);
        if (CollectionUtils.isNotEmpty(list)) {
            if (list.size() > 1) {
                log.error("订单:{},产线:{},状态: {} ,存在多条订单记录", order, line, loading);
            }
            return list.get(0);
        }
        log.error("查询MOM订单参数: DZICS订单ID: {},产线ID: {},订单状态: {} 订单不存在", order, line, loading);
        return null;
    }


    /**
     * 根据订单和产线校验，最好有订单ID
     *
     * @param orderNo
     * @param lineNo
     * @param loading
     * @return
     */
    @Override
    public StartWokeOrderMooM getOrderLine(String orderNo, String lineNo, String loading) {
        OrderIdLineId orderNoAndLineNo = dzProductionLineService.getOrderNoAndLineNo(orderNo, lineNo);
        if (orderNoAndLineNo == null) {
            return null;
        }
        Long orderId = orderNoAndLineNo.getOrderId();
        Long lineId = orderNoAndLineNo.getLineId();
        MonOrder monOrder = getMomOrder(orderId, lineId, loading);
        if (monOrder != null) {
            StartWokeOrderMooM orderMooM = new StartWokeOrderMooM();
            orderMooM.setProTaskId(monOrder.getProTaskOrderId());
            orderMooM.setWipOrderNo(monOrder.getWiporderno());
            orderMooM.setProductNo(monOrder.getProductNo());
            orderMooM.setProductAliasProductionLine(monOrder.getProductAliasProductionLine());
            return orderMooM;
        } else {
            return null;
        }
    }


    /**
     * 托盘号  必有
     * 数量
     * 工序
     * 物料号
     * 订单号
     *
     * @param dzOrderCheck
     * @return
     */
    @Override
    public Result checkOrder(DzOrderCheck dzOrderCheck) {
        try {
            Result ok = Result.ok();
            ok.setData("ERRERR");
            if (dzOrderCheck == null) {
                return ok;
            }
            StartWokeOrderMooM progressStatus = getOrderLine(dzOrderCheck.getOrderCode(), dzOrderCheck.getLineNo(), MomProgressStatus.LOADING);
            if (progressStatus == null) {
                return ok;
            }
            //            工序信息
            MomOrderPath momOrderPath = orderPathService.getproTaskOrderId(progressStatus.getProTaskId());
//            组件物料信息
            List<MomOrderPathMaterial> momOrderPathMaterial = orderPathMaterialService.getMaterialNo(momOrderPath.getWorkingProcedureId());

            String wipOrderNo = progressStatus.getWipOrderNo();
            if (CollectionUtils.isNotEmpty(momOrderPathMaterial) && wipOrderNo == null) {
                return ok;
            }
            String material = dzOrderCheck.getMaterial();
            String orderNo = dzOrderCheck.getMomOrderNo();
            if (material == null || orderNo == null) {
                return ok;
            }
            log.info("校验订单参数 ------------8：materialno:{},material:{},wipOrderNo:{},orderNo:{} ", momOrderPathMaterial, material, wipOrderNo, orderNo);
            boolean falg = false;
            for (MomOrderPathMaterial orderPathMaterial : momOrderPathMaterial) {
                String materialno = orderPathMaterial.getMaterialno();
                if (material.equals(materialno)) {
                    falg = true;
                    break;
                }
            }
            if (falg && wipOrderNo.equals(orderNo)) {
                ok.setData("OKOK");
                return ok;
            }
            return ok;
        } catch (Throwable throwable) {
            log.error("校验物料异常:{}", throwable.getMessage(), throwable);
            Result ok = Result.ok();
            ok.setData("ERRERR");
            return ok;
        }

    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result addOrder(AddMomOrder momOrder, String sub) {
        //判断订单号是否重复
        String lineId = momOrder.getLineId();
        String productId = momOrder.getProductId();
        DzProduct dzProduct = productService.getById(productId);
        String syProductAlias = dzProduct.getSyProductAlias();
        String syPproductNo = dzProduct.getSyProductNo();
        String syCategory = dzProduct.getSyCategory();
        if (dzProduct == null) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
        }
        DzProductionLine dzProductionLine = lineService.getById(lineId);

//          保存订单
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        MonOrder monOrder = new MonOrder();
        monOrder.setTasktype("");
        monOrder.setTaskid("");
        monOrder.setVersion(0);
        monOrder.setOrderId(dzProductionLine.getOrderId());
//        三一 简码 + 类别 = 订单号
        monOrder.setWiporderno(momOrder.getWipOrderNo());
        monOrder.setProductAliasProductionLine(syProductAlias + syCategory);
        monOrder.setWipOrderType(momOrder.getWipOrderType());
        monOrder.setProductionLine(syCategory);
        monOrder.setWorkCenter("");
        monOrder.setWipOrderGroup("");
        monOrder.setGroupCount("");
        monOrder.setProductNo(syPproductNo);
        monOrder.setProductId(productId);
        monOrder.setProductName("");
        monOrder.setProductAlias(syProductAlias);
        monOrder.setFacility("");
        monOrder.setOrderOperationResult(2);
        monOrder.setQuantity(momOrder.getQuantity());
        monOrder.setOrderOldState(MomProgressStatus.DOWN);
        monOrder.setScheduledStartDate(momOrder.getScheduledStartDate());
        monOrder.setScheduledCompleteDate(momOrder.getScheduledCompleteDate());
        monOrder.setProgressStatus(MomProgressStatus.DOWN);
        monOrder.setOrgCode(byUserName.getUseOrgCode());
        monOrder.setDelFlag(false);
        monOrder.setCreateBy(byUserName.getUsername());
        monOrder.setLineId(Long.valueOf(lineId));
//        monOrder.setDockingCode(momOrder.getDockingCode());
        boolean save = save(monOrder);
        if (!save) {
            throw new CustomException(CustomExceptionType.SYSTEM_ERROR);
        }
//        保存工序
//        String workingProcedureId = momOrder.getWorkingProcedureId();
//        DzWorkingProcedure byId = dzWorkingProcedureService.getById(workingProcedureId);

        MomOrderPath momOrderPath = new MomOrderPath();
        momOrderPath.setMomOrderId(monOrder.getProTaskOrderId());
        momOrderPath.setOprsequenceno(momOrder.getWorkName());
        momOrderPath.setSequenceno(momOrder.getWorkName());
        momOrderPath.setOprsequencetype("");
        momOrderPath.setOprsequencename(momOrder.getWorkName());
        momOrderPath.setScheduledstartdate(momOrder.getScheduledStartDate());
        momOrderPath.setScheduledcompletedate(momOrder.getScheduledCompleteDate());
        momOrderPath.setProgressstatus(Integer.valueOf(MomProgressStatus.DOWN));
        momOrderPath.setQuantity(0);
        boolean save1 = orderPathService.save(momOrderPath);
        if (!save1) {
            throw new CustomException(CustomExceptionType.SYSTEM_ERROR);
        }
//       保存组件物料信息
        List<MaterialAddParms> materialAddParms = momOrder.getMaterialAddParms();
        if (materialAddParms.isEmpty()) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
        }
//        产品物料数量
        Integer productNumber = 0;
//        组件物料 编码
        String materialNo = "";
//        组件物料数量
        Integer materialQuantity = 0;
        List<MomOrderPathMaterial> materials = new ArrayList<>();

        for (MaterialAddParms materialAddParm : materialAddParms) {
            if (productNumber.intValue() == 0) {
                productNumber = materialAddParm.getQuantity();
            }
            if (StringUtils.isEmpty(materialAddParm)) {
                materialNo = materialAddParm.getMaterialNo();
            }
            if (materialQuantity.intValue() == 0) {
                materialQuantity = materialAddParm.getQuantity();
            }
            MomOrderPathMaterial momOrderPathMaterial = new MomOrderPathMaterial();
            momOrderPathMaterial.setWorkingProcedureId(momOrderPath.getWorkingProcedureId());
            momOrderPathMaterial.setMaterialno(materialAddParm.getMaterialNo());
            momOrderPathMaterial.setMaterialname("");
            momOrderPathMaterial.setMaterialalias(materialAddParm.getMaterialAlias());
            momOrderPathMaterial.setQuantity(materialAddParm.getQuantity());
            momOrderPathMaterial.setReserveno("");
            momOrderPathMaterial.setReservelineno("");
            momOrderPathMaterial.setUnit("");
            materials.add(momOrderPathMaterial);
        }
        DzProductionLine line = lineService.getById(lineId);
        boolean save2 = orderPathMaterialService.saveBatch(materials);
        if (!save2) {
            throw new CustomException(CustomExceptionType.SYSTEM_ERROR);
        }
        //保存产品物料 叫料信息
        MomWaitCallMaterial orderMomWaitCallMaterial = new MomWaitCallMaterial();
        orderMomWaitCallMaterial.setMomOrderId(monOrder.getProTaskOrderId());
        //1产品物料 2 组件物料
        orderMomWaitCallMaterial.setMaterialType("1");
        //工序号
        orderMomWaitCallMaterial.setOprSequenceNo(momOrderPath.getOprsequenceno());
        //物料编码
        orderMomWaitCallMaterial.setMaterialNo(materialNo);
        orderMomWaitCallMaterial.setQuantity(productNumber);//叫料总数量
        orderMomWaitCallMaterial.setSucessQuantity(0);//已叫料总数量
        orderMomWaitCallMaterial.setSurplusQuantity(productNumber);//剩余叫料总数
        orderMomWaitCallMaterial.setFalgStatus(false);
        orderMomWaitCallMaterial.setFalgOrderStatus(0);
        orderMomWaitCallMaterial.setOrgCode(monOrder.getOrgCode());
        orderMomWaitCallMaterial.setDelFlag(false);
        orderMomWaitCallMaterial.setOrderNo(line.getOrderNo());
        orderMomWaitCallMaterial.setLineNo(line.getLineNo());
        momWaitCallMaterialService.save(orderMomWaitCallMaterial);

        //保存组件物料 叫料信息
        MomWaitCallMaterial momWaitCallMaterial = new MomWaitCallMaterial();
        momWaitCallMaterial.setMomOrderId(monOrder.getProTaskOrderId());
        momWaitCallMaterial.setMaterialType("2");//1产品物料 2 组件物料
        momWaitCallMaterial.setOprSequenceNo(momOrderPath.getOprsequenceno());//工序号
        momWaitCallMaterial.setMaterialNo(materialNo);//物料编码
        momWaitCallMaterial.setQuantity(materialQuantity);//叫料总数量
        momWaitCallMaterial.setSucessQuantity(0);//已叫料总数量
        momWaitCallMaterial.setSurplusQuantity(materialQuantity);//剩余叫料总数
        momWaitCallMaterial.setFalgStatus(false);
        momWaitCallMaterial.setFalgOrderStatus(0);
        momWaitCallMaterial.setOrgCode(monOrder.getOrgCode());
        momWaitCallMaterial.setDelFlag(false);
        momWaitCallMaterial.setOrderNo(line.getOrderNo());
        momWaitCallMaterial.setLineNo(line.getLineNo());
        momWaitCallMaterialService.save(momWaitCallMaterial);
        return Result.ok();
    }

    @Override
    public Result getMomOderList(GetMomOrderVo momOrderVo, String sub) {
        if(momOrderVo.getPage() != -1){
            PageHelper.startPage(momOrderVo.getPage(), momOrderVo.getLimit());
        }
        List<GetMomOrderDo> list = momOrderMapper.getMomOrder(momOrderVo);
        PageInfo<GetMomOrderDo> info = new PageInfo<>(list);
        return new Result(CustomExceptionType.OK, list, info.getTotal());
    }

    @Override
    public Result getMaterialDetails(String mom_order_id, String sub) {
        List<MaterialAddParms> listData = new ArrayList<>();
        List<MomOrderPathMaterial> list = momOrderPathMaterialMapper.selectList(new QueryWrapper<MomOrderPathMaterial>().eq("mom_order_id", mom_order_id));
        for (MomOrderPathMaterial momOrderPathMaterial : list) {
            MaterialAddParms materialAddParms = new MaterialAddParms();
            materialAddParms.setMaterialAlias(momOrderPathMaterial.getMaterialalias());
            materialAddParms.setMaterialNo(momOrderPathMaterial.getMaterialno());
            materialAddParms.setQuantity(momOrderPathMaterial.getQuantity());
            listData.add(materialAddParms);
        }
        return Result.OK(listData);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result put(String sub, PutMomOrder putMomOrder) {
        String progressStatus = putMomOrder.getProgressStatus();
        if (StringUtils.isEmpty(progressStatus)) {
            log.error("订单状态不能为空");
        }
        //判断有没有在进行中（120） 或者暂停（160）的订单
        QueryWrapper<MonOrder> in = new QueryWrapper<MonOrder>()
                .in("ProgressStatus", MomProgressStatus.LOADING, MomProgressStatus.STOP);
        List<MonOrder> monOrders = momOrderMapper.selectList(in);
        if (monOrders.size() > 0) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_125);
        }
        //更改指定订单状态为进行中
        MonOrder monOrder = new MonOrder();
        monOrder.setProTaskOrderId(putMomOrder.getProTaskOrderId());
        monOrder.setProgressStatus(putMomOrder.getProgressStatus());
        monOrder.setOrderOperationResult(1);//操作进行中
        boolean b = this.updateById(monOrder);
        if (b) {
            log.info("开始订单:{}", putMomOrder);
//            下发指令到机器人
        }
        return Result.OK(b);
    }

    @Override
    public Result forceClose(String sub, PutMomOrder putMomOrder) {
        //判断当前订单的操作是否完成
        MonOrder monOrder1 = momOrderMapper.selectById(putMomOrder.getProTaskOrderId());
        if (monOrder1 == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_33);
        }
        if (monOrder1.getOrderOperationResult().intValue() == 1) {
            log.warn("订单上个操作未完成，不允许强制关闭:{}", putMomOrder);
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_126);
        }
        //强制关闭指定订单
        MonOrder monOrder = new MonOrder();
        monOrder.setProTaskOrderId(putMomOrder.getProTaskOrderId());
        monOrder.setProgressStatus(putMomOrder.getProgressStatus());
        monOrder.setOrderOperationResult(1);//操作进行中
        boolean b = this.updateById(monOrder);
        return Result.OK(b);
    }

    /**
     * @param momOrder mom 订单号，
     * @param value    控制是否成功 值 "1" 执行成功 ，0 执行失败
     */
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Throwable.class)
    @Override
    public MonOrder updateOrderStates(MonOrder momOrder, String value) {
        if (log.isInfoEnabled()) {
            log.info("跟新订单状态 momOrder : {}, value: {}", momOrder, value);
        }
        if (StringUtils.isEmpty(value)) {
            log.warn("MOM订单 或 控制是否成功值 为空  value : {}", value);
            return null;
        }
        String progressStatus = momOrder.getProgressStatus();
        if (DzUdpType.OK.equals(value)) {
//             机器人回复 订单控制状态 执行成功 把中间状态变成 执行完成的状态 ，例如 点击开始订单，
//             点击后 变为 开始中，下发指令到机器人，机器人收到指令，运行成功后，回复执行成功。
//            更新之前订单状态为当前状态, 也就是需要吧中间状态变成，执行成功的状态
            momOrder.setOrderOperationResult(2);
            momOrder.setOrderOldState(progressStatus);//操作成功，把旧状态和当前状态同步
            if (MomProgressStatus.LOADING.equals(progressStatus)) {
                if (MomProgressStatus.LOADING.equals(progressStatus)) {
                    momOrder.setRealityStartDate(new Date());
                }
                if(!momOrder.getWiporderno().equals("DZICS-Manual")){
                    //插入报工订单
                    DzWorkReportSort reportSort = new DzWorkReportSort();
                    reportSort.setProTaskOrderId(momOrder.getProTaskOrderId());
                    reportSort.setWipOrderNo(momOrder.getWiporderno());
                    reportSort.setOrderId(momOrder.getOrderId());
                    reportSort.setLineId(momOrder.getLineId());
                    sortMapper.insert(reportSort);
                    log.info("订单开始成功 插入报工订单：{}", reportSort);
                }
            }
//            if (MomProgressStatus.SUCCESS.equals(progressStatus) || MomProgressStatus.DELETE.equals(progressStatus)) {
            if (MomProgressStatus.CLOSE.equals(progressStatus) || MomProgressStatus.DELETE.equals(progressStatus)) {
                momOrder.setRealityCompleteDate(new Date());
                sortMapper.deleteByProTaskId(momOrder.getProTaskOrderId());
                log.info("订单强制关闭：删除报工订单：{}", momOrder.getProTaskOrderId());
            }
//            }
        }

        if (DzUdpType.ERR.equals(value)) {
            if (MomProgressStatus.LOADING.equals(progressStatus)) {
                momOrder.setRealityStartDate(null);
            }
            if (MomProgressStatus.CLOSE.equals(progressStatus)) {
                momOrder.setRealityCompleteDate(null);
            }
//         机器人回复 订单控制状态 执行失败，由 中间状态，恢复回去。 例如 ： 开始中，变为 为开始，也就是需要变为上次的订单状态
            momOrder.setOrderOperationResult(2);
            momOrder.setProgressStatus(momOrder.getOrderOldState());//把订单旧状态赋值给当前状态
        }
        if (MomProgressStatus.SUCCESS.equals(progressStatus)) {
            momOrder.setRealityCompleteDate(new Date());
        }
        boolean b = updateById(momOrder);
        log.warn("跟新订单结果：{},momOrder: {}", b, momOrder);
        return momOrder;
    }

    @Override
    public Result orderStop(String sub, PutMomOrder putMomOrder) {
        MonOrder monOrder = momOrderMapper.selectById(putMomOrder.getProTaskOrderId());
        if (monOrder == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_33);
        }
        if (monOrder.getOrderOperationResult().intValue() == 1) {
            log.warn("订单上个操作未完成，不允许强制关闭:{}", putMomOrder);
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_126);
        }
        //暂停订单
        MonOrder monOrder1 = new MonOrder();
        monOrder1.setProTaskOrderId(putMomOrder.getProTaskOrderId());
        monOrder1.setProgressStatus(putMomOrder.getProgressStatus());
        monOrder1.setOrderOperationResult(1);//操作进行中
        boolean b = this.updateById(monOrder1);
        if (b) {
            log.info("暂停订单:{}", putMomOrder);
        }
        return Result.OK(b);
    }

    @Override
    public Result orderRecover(String sub, PutMomOrder putMomOrder) {
        MonOrder monOrder = momOrderMapper.selectById(putMomOrder.getProTaskOrderId());
        if (monOrder == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_33);
        }
        if (monOrder.getOrderOperationResult().intValue() == 1) {
            log.warn("订单上个操作未完成，不允许强制关闭:{}", putMomOrder);
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_126);
        }
        //判断订单是否在暂停状态
        if (!monOrder.getProgressStatus().equals(MomProgressStatus.STOP)) {
            log.warn("订单不是暂停状态:{}", putMomOrder);
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_7);
        }
        //暂停订单
        MonOrder monOrder1 = new MonOrder();
        monOrder1.setProTaskOrderId(putMomOrder.getProTaskOrderId());
        monOrder1.setProgressStatus(putMomOrder.getProgressStatus());
        monOrder1.setOrderOperationResult(1);//操作进行中
        boolean b = this.updateById(monOrder1);
        if (b) {
            log.info("恢复订单:{}", putMomOrder);
        }
        return Result.OK(b);
    }


    /**
     * 跟新订单数量，如果生产数量大于 等于 计划生产数量 则修改订单状态已完成
     *
     * @param wiporderno 订单号
     * @param output     生产数量
     */
    @Override
    public MonOrder updaateQuantity(MonOrder start, String wiporderno, String output) {
        boolean update = false;
        int orderOutput = 0;
        MonOrder monUpdate = new MonOrder();
        if (start != null) {
            int quantity = start.getQuantity().intValue();
            orderOutput = Integer.valueOf(output).intValue();
            monUpdate.setWiporderno(wiporderno);
            monUpdate.setOrderOutput(orderOutput);
            if (orderOutput >= quantity) {
                if("DZICS-Manual".equals(wiporderno)){
                    monUpdate.setOrderOldState(start.getOrderOldState());
                    monUpdate.setProgressStatus(start.getProgressStatus());
                }else{
                    monUpdate.setOrderOldState(MomProgressStatus.SUCCESS);
                    monUpdate.setProgressStatus(MomProgressStatus.SUCCESS);
                    monUpdate.setRealityCompleteDate(new Date());
                }
            }
            QueryWrapper<MonOrder> wp = new QueryWrapper<>();
            wp.eq("WipOrderNo", wiporderno);
            wp.eq("order_id", start.getOrderId());
            wp.eq("line_id", start.getLineId());
            update = update(monUpdate, wp);
        } else {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, "更新订单生产数量时订单不存在");
        }
        if (monUpdate.getProgressStatus() != null) {
            start.setProgressStatus(monUpdate.getProgressStatus());
            start.setRealityCompleteDate(monUpdate.getRealityCompleteDate());
        }
        if (!update) {
            log.warn("跟新订单: {} 数量: {} 失败", wiporderno, output);
            return start;
        } else {
            start.setOrderOutput(orderOutput);
            return start;
        }
    }


    @Override
    public MonOrder getOrderOperationResult(Long order, Long line, String loading) {
        QueryWrapper<MonOrder> wp = new QueryWrapper<>();
        wp.eq("order_id", order);
        wp.eq("line_id", line);
        wp.eq("order_operation_result", loading);
        List<MonOrder> list = list(wp);
        if (CollectionUtils.isNotEmpty(list)) {
            if (list.size() > 1) {
                log.error("订单:{},产线:{},状态: {} ,存在多条订单执行记录", order, line, loading);
            }
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<MonOrder> getEqNoOrderMom(Long lineId, Long orderId, String productAliasProductionLine, String productNo, String proTaskOrderId) {
        QueryWrapper<MonOrder> wp = new QueryWrapper<>();
        wp.eq("line_id", lineId);
        wp.eq("order_id", orderId);
        wp.eq("productAlias_productionLine", productAliasProductionLine);
        wp.eq("ProductNo", productNo);
        wp.eq("order_operation_result", 2);
        wp.eq("ProgressStatus", MomProgressStatus.DOWN);
        wp.ne("pro_task_order_id", proTaskOrderId);
        List<MonOrder> list = list(wp);
        return list;
    }

    @Override
    public MonOrder getWipOrderNoOne(String wipOrderNo, String orderNo, String lineNo) {
        OrderIdLineId lid = dzProductionLineService.getOrderNoAndLineNo(orderNo, lineNo);
        if (lid == null) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR59);
        }
        Long lineId = lid.getLineId();
        Long orderId = lid.getOrderId();
        QueryWrapper<MonOrder> wp = new QueryWrapper<>();
        wp.eq("WipOrderNo", wipOrderNo);
        wp.eq("order_id", orderId);
        wp.eq("line_id", lineId);
        return getOne(wp);
    }

    @Override
    public boolean  removeByIdProTask(String proTaskOrderId) {
        return removeById(proTaskOrderId);
    }


    @Override
    public MonOrder getOrderCallMaterialStatus(Long orderId, Long lineId, String productNo, String down) {
        return momOrderMapper.getOrderCallMaterialStatus(orderId, lineId, productNo, down);
    }

    @Override
    public Integer getNowWorkPlanNum(String orderId, String begin, String end) {
        return momOrderMapper.getNowWorkPlanNum(orderId,begin,end);
    }



}
