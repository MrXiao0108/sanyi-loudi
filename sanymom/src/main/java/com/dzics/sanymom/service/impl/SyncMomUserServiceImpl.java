package com.dzics.sanymom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.entity.MomUser;
import com.dzics.common.service.MomUserService;
import com.dzics.sanymom.exception.CustomMomException;
import com.dzics.sanymom.framework.OperLogReportWork;
import com.dzics.sanymom.model.ResultDto;
import com.dzics.sanymom.model.common.MomUserSyncType;
import com.dzics.sanymom.model.request.syncuser.SyncEmployeeListUser;
import com.dzics.sanymom.model.request.syncuser.SyncMomUser;
import com.dzics.sanymom.model.request.syncuser.SyncMomUserTask;
import com.dzics.sanymom.service.SyncMomUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2022/1/10.
 * @since
 */
@Slf4j
@Service
public class SyncMomUserServiceImpl implements SyncMomUserService {
    @Autowired
    private MomUserService momUserService;
    @OperLogReportWork(operModul = "人员信息下发",operDesc = "人员信息下发")
    @Transactional(rollbackFor = Throwable.class, isolation = Isolation.SERIALIZABLE)
    @Override
    public ResultDto syncUser(SyncMomUser syncMomUser) {
        try {
            String taskId = syncMomUser.getTaskId();
            int version = syncMomUser.getVersion();
            SyncMomUserTask task = syncMomUser.getTask();
            if (task != null) {
                List<SyncEmployeeListUser> employeeList = task.getEmployeeList();
                if (CollectionUtils.isNotEmpty(employeeList)) {
                    List<MomUser> ins = new ArrayList<>();
                    List<MomUser> update = new ArrayList<>();
                    List<SyncEmployeeListUser> del = new ArrayList<>();
                    for (SyncEmployeeListUser us : employeeList) {
                        String empStatus = us.getEmpStatus();
                        String employeeNo = us.getEmployeeNo();
                        String employeeName = us.getEmployeeName();
                        String paramRsrv1 = us.getParamRsrv1();
                        String paramRsrv2 = us.getParamRsrv2();
                        String paramRsrv3 = us.getParamRsrv3();
                        if (MomUserSyncType.DELETE.equals(empStatus)) {
                            del.add(us);
                        } else if (MomUserSyncType.UPDATE.equals(empStatus)) {
                            MomUser mUs = updateMomUser(empStatus, employeeNo, employeeName, paramRsrv1, paramRsrv2, paramRsrv3);
                            update.add(mUs);
                        } else if (MomUserSyncType.INSERT.equals(empStatus)) {
                            boolean cz = momUserService.isSaveUser(employeeNo);
                            if (cz) {
                                MomUser mUs = getMomUser(empStatus, employeeNo, employeeName, paramRsrv1, paramRsrv2, paramRsrv3);
                                ins.add(mUs);
                            }
                        }
                    }

                    if (CollectionUtils.isNotEmpty(del)) {
                        for (SyncEmployeeListUser user : del) {
                            QueryWrapper<MomUser> wp = new QueryWrapper<>();
                            wp.eq("employee_no", user.getEmployeeNo());
                            momUserService.remove(wp);
                        }
                    }
                    if (CollectionUtils.isNotEmpty(ins)) {
                        momUserService.saveBatch(ins);
                    }
                    if (CollectionUtils.isNotEmpty(update)) {
                        for (MomUser user : update) {
                            QueryWrapper<MomUser> wp = new QueryWrapper<>();
                            wp.eq("employee_no", user.getEmployeeNo());
                            momUserService.update(user, wp);
                        }
                    }
                    ResultDto resultDto = getResultDto(taskId, version);
                    return resultDto;
                }
                throw new CustomMomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, "未解析到人员信息", syncMomUser.getVersion(), syncMomUser.getTaskId());
            }
            throw new CustomMomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, "下发参数解析错误", syncMomUser.getVersion(), syncMomUser.getTaskId());
        } catch (Throwable throwable) {
            log.error("同步人员信息错误：{}",throwable.getMessage(),throwable);
            throw new CustomMomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR0.getChinese(), syncMomUser.getVersion(), syncMomUser.getTaskId());
        }

    }

    private MomUser updateMomUser(String empStatus, String employeeNo, String employeeName, String paramRsrv1, String paramRsrv2, String paramRsrv3) {
        MomUser mUs = new MomUser();
        mUs.setEmployeeNo(employeeNo);
        mUs.setEmployeeName(employeeName);
        mUs.setEmpStatus(empStatus);
        mUs.setParamrsrv1(paramRsrv1);
        mUs.setParamrsrv2(paramRsrv2);
        mUs.setParamrsrv3(paramRsrv3);
        return mUs;
    }

    private MomUser getMomUser(String empStatus, String employeeNo, String employeeName, String paramRsrv1, String paramRsrv2, String paramRsrv3) {
        MomUser mUs = new MomUser();
        mUs.setEmployeeNo(employeeNo);
        mUs.setEmployeeName(employeeName);
        mUs.setEmpStatus(empStatus);
        mUs.setParamrsrv1(paramRsrv1);
        mUs.setParamrsrv2(paramRsrv2);
        mUs.setParamrsrv3(paramRsrv3);
        mUs.setDelFlag(false);
        mUs.setCreateBy("MOM");
        return mUs;
    }

    private ResultDto getResultDto(String taskId, int version) {
        ResultDto resultDto = new ResultDto();
        resultDto.setVersion(version);
        resultDto.setTaskId(taskId);
        resultDto.setCode("0");
        resultDto.setMsg("接收成功");
        return resultDto;
    }
}
