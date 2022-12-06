package com.dzics.business.service;

import com.dzics.business.model.vo.productiontask.workingprocedure.DetectionProceduct;
import com.dzics.business.model.vo.productiontask.workingprocedure.ProcedIdproductNo;
import com.dzics.business.model.vo.productiontask.workingprocedure.UpdateDetectionProceduct;
import com.dzics.business.model.vo.productiontask.workingprocedure.WorkingProcedureAdd;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.productiontask.workingProcedure.WorkingProcedureRes;
import com.dzics.common.util.PageLimit;

import java.util.List;

/**
 * 工序管理
 *
 * @author ZhangChengJun
 * Date 2021/5/18.
 * @since
 */
public interface WorkingProcedureService {

    /**
     * 新增工序
     *
     * @param procedureAdd 工序编号 名称 订单 产线 id
     * @param sub
     * @return
     */
    Result addWorkingProcedure(WorkingProcedureAdd procedureAdd, String sub);

    /**
     * 查询工列表
     *
     * @param pageLimit
     * @param procedureAdd
     * @param sub
     * @return
     */
    Result<List<WorkingProcedureRes>> selWorkingProcedure(PageLimit pageLimit, WorkingProcedureAdd procedureAdd, String sub);

    /**
     * 编辑工序
     *
     * @param procedureAdd
     * @param sub
     * @return
     */
    Result editWorkingProcedure(WorkingProcedureAdd procedureAdd, String sub);

    /**
     * 删除工序
     *
     * @param id
     * @param sub
     * @return
     */
    Result delWorkingProcedure(String id, String sub);

    /**
     * 新增工序绑定工件接口
     *
     * @param detectionProceduct
     * @param sub
     * @return
     */
    Result addProcedureProduct(DetectionProceduct detectionProceduct, String sub);

    /**
     * 根据产品Id 获取产品检测项配置
     *
     *
     * @param orderId
     * @param lineId
     * @param productNo
     * @param sub
     * @return
     */
    Result selProductTemplate(String orderId, String lineId, String productNo, String sub);

    /**
     * 根据工序id 工件编号 获取 工序所关联的工件
     *
     * @param productNo 工件编号
     * @param sub
     * @return
     */
    Result selProcedureProduct(ProcedIdproductNo productNo, String sub);

    /**
     * 根据工件 工序 id 获取 参数检测配置项进行编辑
     *
     *
     * @param orderId
     * @param lineId
     * @param workProcedProductId
     * @param sub
     * @return
     */
    Result selEditProcedureProduct(String orderId, String lineId, String workProcedProductId, String sub);

    /**
     * 更新工件 工序 关系
     *
     * @param detectionProceduct
     * @param sub
     * @return
     */
    Result updateProcedureProduct(UpdateDetectionProceduct detectionProceduct, String sub);

    /**
     * 工序工件主键
     * @param workProcedProductId
     * @param sub
     * @return
     */
    Result delWorkProcedProductId(String workProcedProductId, String sub);

    /**
     * 获取所有工序
     * @param sub
     * @return
     */
    Result getWorkingProcedures(String sub);
}
