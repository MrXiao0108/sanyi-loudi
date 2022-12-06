package com.dzics.business.service;

import com.dzics.common.model.request.device.AddFaultRecordParms;
import com.dzics.common.model.request.device.FaultRecordParmsDateils;
import com.dzics.common.model.request.device.FaultRecordParmsReq;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.device.FaultRecord;
import com.dzics.common.model.response.device.FaultRecordDetails;
import com.dzics.common.util.PageLimitBase;

/**
 * @author ZhangChengJun
 * Date 2021/9/28.
 * @since
 * 设备维修记录操作接口
 */
public interface BusDzRepairHistoryService {
    /**
     * 故障列表
     * @param sub
     * @param pageLimit
     * @param parmsReq
     * @return
     */
    Result getFaultRecordList(String sub, PageLimitBase pageLimit, FaultRecordParmsReq parmsReq);

    Result<FaultRecordDetails> getFaultRecordDetails(String sub,  FaultRecordParmsDateils parmsReq);

    Result addFaultRecord(String sub, AddFaultRecordParms parmsReq);

    Result updateFaultRecord(String sub, AddFaultRecordParms parmsReq);

    Result delFaultRecord(String sub, String repairId);
}
