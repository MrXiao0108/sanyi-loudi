package com.dzics.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.business.service.BusDzRepairHistoryService;
import com.dzics.common.exception.CustomException;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.entity.DzRepairHistory;
import com.dzics.common.model.entity.DzRepairHistoryDetails;
import com.dzics.common.model.entity.SysUser;
import com.dzics.common.model.request.device.AddFaultRecordParms;
import com.dzics.common.model.request.device.AddFaultRecordParmsInner;
import com.dzics.common.model.request.device.FaultRecordParmsDateils;
import com.dzics.common.model.request.device.FaultRecordParmsReq;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.device.FaultRecord;
import com.dzics.common.model.response.device.FaultRecordDetails;
import com.dzics.common.model.response.device.FaultRecordDetailsInner;
import com.dzics.common.service.DzRepairHistoryDetailsService;
import com.dzics.common.service.DzRepairHistoryService;
import com.dzics.common.service.SysUserServiceDao;
import com.dzics.common.util.DateUtil;
import com.dzics.common.util.PageLimitBase;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/9/28.
 * @since
 */
@Service
public class BusDzRepairHistoryServiceImpl implements BusDzRepairHistoryService {
    @Autowired
    private DzRepairHistoryService dzRepairHistoryService;
    @Autowired
    private DzRepairHistoryDetailsService historyDetailsService;
    @Autowired
    private DateUtil dateUtil;
    @Autowired
    private SysUserServiceDao sysUserServiceDao;

    @Override
    public Result getFaultRecordList(String sub, PageLimitBase pageLimit, FaultRecordParmsReq parmsReq) {
        if (pageLimit.getPage()!=-1){
            PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        }
        Long checkNumber = StringUtils.isEmpty(parmsReq.getCheckNumber()) ? null : Long.valueOf(parmsReq.getCheckNumber());
        String equipmentNo = parmsReq.getEquipmentNo();
        Integer faultType = StringUtils.isEmpty(parmsReq.getFaultType()) ? null : Integer.valueOf(parmsReq.getFaultType());
        Long lineId = StringUtils.isEmpty(parmsReq.getLineId()) ? null : Long.valueOf(parmsReq.getLineId());
        List<FaultRecord> faultRecords = dzRepairHistoryService.getFaultRecordList(checkNumber, lineId, faultType, equipmentNo, pageLimit.getField(), pageLimit.getType(),parmsReq.getStartTime(),parmsReq.getEndTime());
        PageInfo<FaultRecord> faultRecordPageInfo = new PageInfo<>(faultRecords);
        List<FaultRecord> list = faultRecordPageInfo.getList();
        long total = faultRecordPageInfo.getTotal();
        return new Result(CustomExceptionType.OK, list, total);
    }



    @Override
    public Result<FaultRecordDetails> getFaultRecordDetails(String sub, FaultRecordParmsDateils parmsReq) {
        DzRepairHistory byId = dzRepairHistoryService.getById(parmsReq.getRepairId());
        List<FaultRecordDetailsInner> detailsInners = dzRepairHistoryService.getFaultRecordDetails(parmsReq.getRepairId());
        FaultRecordDetails details = new FaultRecordDetails();
        details.setDetailsInner(detailsInners);
        details.setRemarks(byId.getRemarks());
        details.setStartHandleDate(dateUtil.dateFormatToStingYmdHms(byId.getStartHandleDate()));
        details.setCompleteHandleDate(dateUtil.dateFormatToStingYmdHms(byId.getCompleteHandleDate()));
        return Result.OK(details);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result addFaultRecord(String sub, AddFaultRecordParms parmsReq) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        String realname = byUserName.getRealname();
        String useOrgCode = byUserName.getUseOrgCode();
        DzRepairHistory dzRepairHistory = new DzRepairHistory();
        dzRepairHistory.setLineId(Long.valueOf(parmsReq.getLineId()));
        dzRepairHistory.setDieviceId(Long.valueOf(parmsReq.getDeviceId()));
        dzRepairHistory.setFaultType(Integer.valueOf(parmsReq.getFaultType()));
        dzRepairHistory.setStartHandleDate(parmsReq.getStartHandleDate());
        dzRepairHistory.setCompleteHandleDate(parmsReq.getCompleteHandleDate());
        dzRepairHistory.setRemarks(parmsReq.getRemarks());
        dzRepairHistory.setUsername(sub);
        dzRepairHistory.setOrgCode(useOrgCode);
        dzRepairHistory.setDelFlag(false);
        dzRepairHistory.setCreateBy(realname);
        dzRepairHistoryService.save(dzRepairHistory);
        String repairId = dzRepairHistory.getRepairId();
        List<AddFaultRecordParmsInner> parmsInners = parmsReq.getParmsInners();
        if (CollectionUtils.isNotEmpty(parmsInners)) {
            List<DzRepairHistoryDetails> historyDetails = new ArrayList<>();
            for (AddFaultRecordParmsInner parmsInner : parmsInners) {
                DzRepairHistoryDetails dzRepairHistoryDetails = new DzRepairHistoryDetails();
                dzRepairHistoryDetails.setRepairId(repairId);
                dzRepairHistoryDetails.setFaultLocation(parmsInner.getFaultLocation());
                dzRepairHistoryDetails.setFaultDescription(parmsInner.getFaultDescription());
                dzRepairHistoryDetails.setOrgCode(useOrgCode);
                dzRepairHistoryDetails.setDelFlag(false);
                dzRepairHistoryDetails.setCreateBy(realname);
                historyDetails.add(dzRepairHistoryDetails);
            }
            historyDetailsService.saveBatch(historyDetails);
        }
        return Result.ok();
    }

    @Override
    public Result updateFaultRecord(String sub, AddFaultRecordParms parmsReq) {
        String repairId = parmsReq.getRepairId();
        DzRepairHistory repairHistory = dzRepairHistoryService.getById(repairId);
        if (!repairHistory.getUsername().equals(sub)) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR57);
        }
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        String realname = byUserName.getRealname();
        DzRepairHistory dzRepairHistory = new DzRepairHistory();
        dzRepairHistory.setRepairId(repairId);
        dzRepairHistory.setLineId(Long.valueOf(parmsReq.getLineId()));
        dzRepairHistory.setDieviceId(Long.valueOf(parmsReq.getDeviceId()));
        dzRepairHistory.setFaultType(Integer.valueOf(parmsReq.getFaultType()));
        dzRepairHistory.setStartHandleDate(parmsReq.getStartHandleDate());
        dzRepairHistory.setCompleteHandleDate(parmsReq.getCompleteHandleDate());
        dzRepairHistory.setRemarks(parmsReq.getRemarks());
        dzRepairHistory.setUpdateBy(realname);
        dzRepairHistoryService.updateById(dzRepairHistory);
        List<AddFaultRecordParmsInner> parmsInners = parmsReq.getParmsInners();
        if (CollectionUtils.isNotEmpty(parmsInners)) {
            List<DzRepairHistoryDetails> historyDetails = new ArrayList<>();
            for (AddFaultRecordParmsInner parmsInner : parmsInners) {
                DzRepairHistoryDetails dzRepairHistoryDetails = new DzRepairHistoryDetails();
                dzRepairHistoryDetails.setRepairDetailsId(parmsInner.getRepairDetailsId());
                dzRepairHistoryDetails.setFaultLocation(parmsInner.getFaultLocation());
                dzRepairHistoryDetails.setFaultDescription(parmsInner.getFaultDescription());
                historyDetails.add(dzRepairHistoryDetails);
            }
            historyDetailsService.updateBatchById(historyDetails);
        }
        return Result.ok();
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Result delFaultRecord(String sub, String repairId) {
        dzRepairHistoryService.removeById(repairId);
        QueryWrapper<DzRepairHistoryDetails> wp = new QueryWrapper<>();
        wp.eq("repair_id", repairId);
        historyDetailsService.remove(wp);
        return Result.ok();
    }
}
