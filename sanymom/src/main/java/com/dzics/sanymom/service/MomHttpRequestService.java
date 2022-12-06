package com.dzics.sanymom.service;

import com.dzics.common.model.custom.ReqWorkQrCodeOrder;
import com.dzics.common.model.entity.MomDistributionWaitRequest;
import com.dzics.common.model.entity.MomDistributionWaitRequestLog;
import com.dzics.common.model.entity.MomProgressFeedback;
import com.dzics.sanymom.model.request.EmptyFrameMoves;
import com.dzics.sanymom.model.request.MoveFrameParms;
import com.dzics.sanymom.model.request.agv.AgvParmsDto;
import com.dzics.sanymom.model.response.searchframe.MaterialFrameRes;

import java.util.List;

/**
 * 与mom 交互请求接口类
 *
 * @author ZhangChengJun
 * Date 2021/6/11.
 * @since
 */
public interface MomHttpRequestService {

    /**
     * 根据待报工记录重新报工
     *
     * @param infoList
     * @return
     */
    List<String> reportWorkMom(List<MomProgressFeedback> infoList);


    /**
     * 请求MOM更新 料点状态
     *
     *
     * @param innerGroupId
     * @param groupId
     * @param lineNo
     * @param orderCode
     * @param externalCode
     * @param palletNo
     * @return
     */
    boolean updatePointPallet(String innerGroupId, String groupId, String lineNo, String orderCode, String externalCode, String palletNo);


    /**
     * 工序间配送等待请求 重新发送
     *
     * @param list
     * @return 返回以发送成功的数据ID  用于清除记录
     */
    List<String> interProcessDistribution(List<MomDistributionWaitRequest> list);

    /**
     * 根据请求ID获取 发送请求时的请求类型
     *
     * @param reqId
     * @return
     */
    String getMyReqTypeId(String reqId);

    /**
     * 查询料框参数接口
     *
     *
     *
     * @param innerGroupId
     * @param groupId
     * @param orderNo
     * @param lineNo
     * @param sourceNo
     * @param paramRsrv1
     * @return
     */
    MaterialFrameRes getStringPalletType(String innerGroupId, String groupId, String orderNo, String lineNo, String sourceNo, String paramRsrv1);


    MomDistributionWaitRequestLog getMomDistributionWaitRequestLog(AgvParmsDto parmsDto);

    MomDistributionWaitRequest getMomDistributionWaitRequest(AgvParmsDto parmsDto, String code, String orderCode, String lineNo, String basketType, String pointModel, String taskType);
}
