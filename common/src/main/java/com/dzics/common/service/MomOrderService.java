package com.dzics.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.common.model.custom.StartWokeOrderMooM;
import com.dzics.common.model.entity.MonOrder;
import com.dzics.common.model.request.dzcheck.DzOrderCheck;
import com.dzics.common.model.request.mom.AddMomOrder;
import com.dzics.common.model.request.mom.GetMomOrderVo;
import com.dzics.common.model.request.mom.PutMomOrder;
import com.dzics.common.model.response.Result;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * mom下发订单表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-27
 */
public interface MomOrderService extends IService<MonOrder> {

    /**
     * 更新订单号获取订单
     *
     * @param wipOrderNo
     * @param orderNo
     * @param lineNo
     */
    List<MonOrder> getWipOrderNo(String wipOrderNo, String orderNo, String lineNo);


    /**
     * 根据当前在做订单 校验物料信息是否正常
     *
     * @param dzOrderCheck
     * @return
     */
    Result checkOrder(DzOrderCheck dzOrderCheck);

    /**
     * 手动新增三一mom 订单
     *
     * @param momOrder
     * @param sub
     * @return
     */
    Result addOrder(AddMomOrder momOrder, String sub);

    /**
     * 获取mom订单列表
     *
     * @param getMomOrderVO
     * @param sub
     * @return
     */
    Result getMomOderList(GetMomOrderVo getMomOrderVO, String sub);

    /**
     * 根据工序id查询组件物料详情
     *
     * @param workingProcedureId
     * @param sub
     * @return
     */
    Result getMaterialDetails(String workingProcedureId, String sub);

    /**
     * 订单修改为进行中
     *
     * @param sub
     * @param putMomOrder
     * @return
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    Result put(String sub, PutMomOrder putMomOrder);


    /**
     * 跟新订单状态
     *
     * @param momOrder
     * @param value
     * @return MonOrder 跟新订单后的信息返回
     */

    MonOrder updateOrderStates(MonOrder momOrder, String value);

    /**
     * 强制关闭
     *
     * @param sub
     * @param putMomOrder
     * @return
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    Result forceClose(String sub, PutMomOrder putMomOrder);

    /**
     * 订单暂停
     *
     * @param sub
     * @param putMomOrder
     * @return
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    Result orderStop(String sub, PutMomOrder putMomOrder);

    /**
     * 订单恢复
     *
     * @param sub
     * @param putMomOrder
     * @return
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    Result orderRecover(String sub, PutMomOrder putMomOrder);




    /**
     * 跟新订单生产数量
     *
     * @param wiporderno
     * @param quantity
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    MonOrder updaateQuantity(MonOrder monOrderSel, String wiporderno, String quantity);

    /**
     * 根据订单状态获取订单 ID 和订单号
     *
     * @param orderNo
     * @param lineNo
     * @param loading
     * @return
     */
    StartWokeOrderMooM getOrderLine(String orderNo, String lineNo, String loading);


    /**
     * 根据订单状态获取订单 ID 和订单号
     *
     * @param orderNo
     * @param lineNo
     * @param loading
     * @return
     */
    MonOrder getMomOrder(String orderNo, String lineNo, String loading);


    /**
     * 正在执行中的订单状态
     *
     * @param orderNo
     * @param lineNo
     * @param operationResultLoading
     * @return
     */
    MonOrder getOrderOperationResult(Long orderNo, Long lineNo, String operationResultLoading);

    /**
     * @param lineId                     产线ID
     * @param orderId                    订单ID
     * @param productAliasProductionLine 临时订单号
     * @param productNo                  产品物料号
     * @param proTaskOrderId             订单主键
     * @return
     */
    List<MonOrder> getEqNoOrderMom(Long lineId, Long orderId, String productAliasProductionLine, String productNo, String proTaskOrderId);


    MonOrder getWipOrderNoOne(String wipOrderNo, String orderNo, String lineNo);

    boolean removeByIdProTask(String proTaskOrderId);


    MonOrder getOrderCallMaterialStatus(Long orderId, Long lineId, String productNo, String down);

    /**
    * 查询当班计划生产数量
    * */
    Integer getNowWorkPlanNum(String orderId,String begin,String end);




}
