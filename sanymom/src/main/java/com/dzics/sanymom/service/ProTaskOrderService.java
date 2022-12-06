package com.dzics.sanymom.service;

import com.dzics.common.model.agv.search.SearchDzdcMomSeqenceNo;
import com.dzics.common.model.entity.DzProductionLine;
import com.dzics.common.model.entity.MonOrder;
import com.dzics.common.model.response.Result;
import com.dzics.sanymom.model.request.sany.OprSequenceList;
import com.dzics.sanymom.model.request.sany.Task;

import java.util.List;

/**
 * mom下发订单接口
 *
 * @author ZhangChengJun
 * Date 2021/5/27.
 */
public interface ProTaskOrderService {

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
    MonOrder saveTaskOrder(Task task, String wipOrderNo, String taskId, int version, String taskType, DzProductionLine line,String momParms);

    /**
     * 保存中控下发订单中的工序
     *
     * @param proTaskOrderId  订单ID
     * @param oprSequenceList 工序集合
     * @return
     */
    void saveOrderPath(String proTaskOrderId, List<OprSequenceList> oprSequenceList, String productNo);

    /**
     * 跟新保存物料信息
     *
     * @param line
     * @param monOrder
     * @param version
     * @param taskId
     */
    void saveTaskOrderMaterial(DzProductionLine line, Task task, MonOrder monOrder, int version, String taskId);





    Result searechOprSequenceNo(SearchDzdcMomSeqenceNo momSeqenceNo);

  }
