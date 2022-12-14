package com.dzics.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.business.service.BusinessDzProductionLineService;
import com.dzics.business.service.cache.DzDetectionTemplCache;
import com.dzics.common.dao.*;
import com.dzics.common.enums.Message;
import com.dzics.common.enums.UserIdentityEnum;
import com.dzics.common.exception.CustomWarnException;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.entity.*;
import com.dzics.common.model.constant.FinalCode;
import com.dzics.common.model.request.*;
import com.dzics.common.model.request.line.LineParmsList;
import com.dzics.common.model.response.DzEquipmentWorkShiftDo;
import com.dzics.common.model.response.GeneralLineDo;
import com.dzics.common.model.response.LineDo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.commons.Lines;
import com.dzics.common.model.response.feishi.LineListDo;
import com.dzics.common.service.SysUserServiceDao;
import com.dzics.common.util.LineTypeUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BusinessDzProductionLineServiceImpl implements BusinessDzProductionLineService {
    @Autowired
    DzProductionLineMapper dzProductionLineMapper;
    @Autowired
    DzOrderMapper dzOrderMapper;
    @Autowired
    SysUserServiceDao sysUserServiceDao;
    @Autowired
    DzEquipmentWorkShiftMapper workShiftMapper;
    @Autowired
    DzEquipmentMapper dzEquipmentMapper;
    @Autowired
    DzEquipmentWorkShiftMapper dzEquipmentWorkShiftMapper;
    @Autowired
    DzLineShiftDayMapper dzLineShiftDayMapper;
    @Autowired
    DzProductionPlanMapper dzProductionPlanMapper;
    @Autowired
    DzProductionPlanDayMapper dzProductionPlanDayMapper;
    @Autowired
    DzDetectionTemplCache dzDetectionTemplCache;

    @Override
    @Transactional
    public Result add(String sub, AddLineVo data) throws Exception {
        String lineType = data.getLineType();
        LineTypeUtil.typtIsOk(lineType);
        if (!lineWorkTime(data.getWorkShiftVos())) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_41);
        }
        //???????????????????????????
        List<Integer> collect = data.getWorkShiftVos().stream().map(p -> p.getSortNo()).collect(Collectors.toList());
        List<Integer> collect1 = collect.stream().distinct().collect(Collectors.toList());
        if (collect.size() != collect1.size()) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_46);
        }
        //????????????????????????
        List<String> workName = data.getWorkShiftVos().stream().map(p -> p.getWorkName()).collect(Collectors.toList());
        List<String> workName1 = workName.stream().distinct().collect(Collectors.toList());
        if (workName.size() != workName1.size()) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_51);
        }
        List<DzProductionLine> dzProductionLines = dzProductionLineMapper.selectList(new QueryWrapper<DzProductionLine>().eq("line_code", data.getLineCode()));
        if (dzProductionLines.size() > 0) {
            log.error("??????????????????:{}", data.getLineCode());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_31);
        }
        List<DzProductionLine> lineNo = dzProductionLineMapper.selectList(new QueryWrapper<DzProductionLine>().eq("line_no", data.getLineNo()).eq("order_id", data.getOrderId()));
        if (lineNo.size() > 0) {
            log.error("??????????????????:{}", data.getLineNo());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_40);
        }
        List<DzProductionLine> lineName = dzProductionLineMapper.selectList(new QueryWrapper<DzProductionLine>().eq("line_name", data.getLineName()));
        if (lineName.size() > 0) {
            log.error("??????????????????:{}", data.getLineName());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_32);
        }
        DzOrder dzOrder = dzOrderMapper.selectById(data.getOrderId());
        if (dzOrder == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_33);
        }
        if (data.getLineNo().length() > 10) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_74);
        }
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        DzProductionLine dzProductionLine = new DzProductionLine();
        dzProductionLine.setOrderId(data.getOrderId());
        dzProductionLine.setLineCode(data.getLineCode());
        dzProductionLine.setLineName(data.getLineName());
        dzProductionLine.setOrderNo(dzOrder.getOrderNo());
        dzProductionLine.setOrgCode(dzOrder.getOrgCode());
        dzProductionLine.setCreateBy(byUserName.getRealname());
        dzProductionLine.setRemarks(data.getRemarks());
        dzProductionLine.setLineNo(data.getLineNo());
        dzProductionLine.setLineType(data.getLineType());
        int insert = dzProductionLineMapper.insert(dzProductionLine);
        if (insert > 0) {
            //????????????????????????
            List<AddWorkShiftVo> workShiftVos = data.getWorkShiftVos();
            for (AddWorkShiftVo addWorkShiftVo : workShiftVos) {
                lineWork(addWorkShiftVo);
                DzEquipmentWorkShift dzEquipmentWorkShift = new DzEquipmentWorkShift();
                BeanUtils.copyProperties(addWorkShiftVo, dzEquipmentWorkShift);
                dzEquipmentWorkShift.setProductionLineId(dzProductionLine.getId());
                dzEquipmentWorkShift.setOrgCode(byUserName.getUseOrgCode());
                dzEquipmentWorkShift.setCreateBy(byUserName.getRealname());
                workShiftMapper.insert(dzEquipmentWorkShift);
            }
            //????????????????????????
            DzProductionPlan dzProductionPlan = new DzProductionPlan();
            dzProductionPlan.setLineId(dzProductionLine.getId());
            dzProductionPlan.setPlanType(0);
            dzProductionPlan.setPlannedQuantity(0L);
            dzProductionPlan.setStatus(1);
            dzProductionPlan.setOrgCode(dzProductionLine.getOrgCode());
            dzProductionPlanMapper.insert(dzProductionPlan);
            return new Result(CustomExceptionType.OK, dzProductionLine);
        }
        return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_1);
    }

    /**
     * ????????????????????????
     *
     * @param addWorkShiftVo ????????????
     * @return
     */
    public void lineWork(AddWorkShiftVo addWorkShiftVo) throws Exception {

        if (addWorkShiftVo.getSortNo() == null) {
            throw new CustomWarnException(Message.ERR_47);
        }
        if (addWorkShiftVo.getEndTime() == null) {
            throw new CustomWarnException(Message.ERR_48);
        }
        if (addWorkShiftVo.getStartTime() == null) {
            throw new CustomWarnException(Message.ERR_49);
        }
        if (addWorkShiftVo.getWorkName() == null || addWorkShiftVo.getWorkName().equals("")) {
            throw new CustomWarnException(Message.ERR_50);
        }
    }


    @Override
    public Result list(String sub, LineParmsList lineParmsList) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue() && byUserName.getUseOrgCode().equals(FinalCode.DZ_USE_ORG_CODE)) {
            byUserName.setUseOrgCode(null);
        }
        PageHelper.startPage(lineParmsList.getPage(), lineParmsList.getLimit());
        SelectLineVo selectLineVo = new SelectLineVo();
        selectLineVo.setUseOrgCode(byUserName.getUseOrgCode());
        selectLineVo.setDepartName(lineParmsList.getDepartName());
        selectLineVo.setLineName(lineParmsList.getLineName());
        selectLineVo.setOrderNo(lineParmsList.getOrderNo());
        selectLineVo.setType(lineParmsList.getType());
        selectLineVo.setField(lineParmsList.getField());
        selectLineVo.setLineType(lineParmsList.getLineType());
        selectLineVo.setId(lineParmsList.getLineId());
        List<LineDo> list = dzProductionLineMapper.list(selectLineVo);
        PageInfo<LineDo> info = new PageInfo<>(list);
        List<LineDo> data = info.getList();
        //???????????? ????????????????????????????????????
        for (int i = 0; i < data.size(); i++) {
            Long id = data.get(i).getId();
            String now = LocalTime.now().toString();
            String dataNow = now.substring(0, 8);
            List<DzEquipmentWorkShift> listWorkShift = dzEquipmentWorkShiftMapper.getPresentWorkShift(byUserName.getUseOrgCode(), id, dataNow);
            if (listWorkShift.size() > 0) {
                DzEquipmentWorkShift dzEquipmentWorkShift = listWorkShift.get(0);
                data.get(i).setWork_name(dzEquipmentWorkShift.getWorkName());
                data.get(i).setStart_time(dzEquipmentWorkShift.getStartTime().toString());
                data.get(i).setEnd_time(dzEquipmentWorkShift.getEndTime().toString());
            } else {
                List<DzEquipmentWorkShift> listWorkShift2 = dzEquipmentWorkShiftMapper.getPresentWorkShift2(byUserName.getUseOrgCode(), id, dataNow);
                if (listWorkShift2.size() > 0) {
                    DzEquipmentWorkShift dzEquipmentWorkShift2 = listWorkShift2.get(0);
                    data.get(i).setWork_name(dzEquipmentWorkShift2.getWorkName());
                    data.get(i).setStart_time(dzEquipmentWorkShift2.getStartTime().toString());
                    data.get(i).setEnd_time(dzEquipmentWorkShift2.getEndTime().toString());
                }
            }

        }

        return new Result(CustomExceptionType.OK, data, info.getTotal());
    }

    @Override
    @Transactional
    public Result del(String sub, Long id) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        //????????????????????????????????????
        List<DzEquipment> lineId = dzEquipmentMapper.selectList(new QueryWrapper<DzEquipment>().eq("line_id", id));
        if (lineId.size() > 0) {
            log.error("???????????????????????????????????????,??????id:{}", id);
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_34);
        }
        //???????????????????????????????????????
        dzEquipmentWorkShiftMapper.del(id, byUserName.getUseOrgCode());
        //??????????????????????????????
        dzLineShiftDayMapper.delete(new QueryWrapper<DzLineShiftDay>().eq("line_id", id));
        //?????????????????????????????????
        List<DzProductionPlan> lineList = dzProductionPlanMapper.selectList(new QueryWrapper<DzProductionPlan>().eq("line_id", id));
        if (lineList.size() > 0) {
            List<Long> collect = lineList.stream().map(p -> p.getId()).collect(Collectors.toList());
            dzProductionPlanDayMapper.delete(new QueryWrapper<DzProductionPlanDay>().in("plan_id", collect));
        }
        //????????????????????????
        dzProductionPlanMapper.delete(new QueryWrapper<DzProductionPlan>().eq("line_id", id));
        //????????????
        int i = dzProductionLineMapper.deleteById(id);
        dzDetectionTemplCache.deleteLineIdByOrderNoLineNo();
        return new Result(CustomExceptionType.OK, i);
    }


    @Transactional
    @Override
    public Result pud(String sub, PudLineVo data) throws Exception {
        String lineType = data.getLineType();
        LineTypeUtil.typtIsOk(lineType);
        if (!lineWorkTime(data.getWorkShiftVos())) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_41);
        }
        //???????????????????????????
        List<Integer> collect = data.getWorkShiftVos().stream().map(p -> p.getSortNo()).collect(Collectors.toList());
        List<Integer> collect1 = collect.stream().distinct().collect(Collectors.toList());
        if (collect.size() != collect1.size()) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_46);
        }

        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        DzProductionLine dzProductionLine = dzProductionLineMapper.selectById(data.getId());
        if (dzProductionLine == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_35);
        }
        if (!dzProductionLine.getLineName().equals(data.getLineName())) {
            List<DzProductionLine> lineName = dzProductionLineMapper.selectList(new QueryWrapper<DzProductionLine>().eq("line_name", data.getLineName()));
            if (lineName.size() > 0) {
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_36);
            }
        }
        if (!dzProductionLine.getLineNo().equals(data.getLineNo())) {
            List<DzProductionLine> lineNo = dzProductionLineMapper.selectList(new QueryWrapper<DzProductionLine>().eq("line_no", data.getLineNo()).eq("order_id", dzProductionLine.getOrderId()));
            if (lineNo.size() > 0) {
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_40);
            }

        }
        //??????????????????
        if (!dzProductionLine.getLineCode().equals(data.getLineCode())) {
            List<DzProductionLine> lineCode = dzProductionLineMapper.selectList(new QueryWrapper<DzProductionLine>().eq("line_code", data.getLineCode()).eq("order_id", dzProductionLine.getOrderId()));
            if (lineCode.size() > 0) {
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_31);
            }
        }
        //???????????????????????????
        if (!dzProductionLine.getLineNo().equals(data.getLineNo())) {
            //???????????????????????????
            DzEquipment dzEquipment = new DzEquipment();
            dzEquipment.setLineNo(data.getLineNo());
            dzEquipmentMapper.update(dzEquipment, new QueryWrapper<DzEquipment>().eq("line_id", data.getId()));
            //???????????????????????????
            DzLineShiftDay dzLineShiftDay = new DzLineShiftDay();
            dzLineShiftDay.setLineNo(data.getLineNo());
            dzLineShiftDayMapper.update(dzLineShiftDay, new QueryWrapper<DzLineShiftDay>().eq("line_id", dzProductionLine.getId()));
        }

        //????????????
        dzProductionLine.setOrderId(data.getOrderId());
        dzProductionLine.setLineCode(data.getLineCode());
        dzProductionLine.setLineName(data.getLineName());
        dzProductionLine.setRemarks(data.getRemarks());
        dzProductionLine.setLineNo(data.getLineNo());
        dzProductionLine.setLineType(data.getLineType());
        int i = dzProductionLineMapper.updateById(dzProductionLine);
        if (i > 0) {
            //????????????????????????
            dzEquipmentWorkShiftMapper.del(data.getId(), byUserName.getUseOrgCode());
            //????????????????????????
            //????????????????????????
            List<AddWorkShiftVo> workShiftVos = data.getWorkShiftVos();
            for (AddWorkShiftVo addWorkShiftVo : workShiftVos) {
                DzEquipmentWorkShift dzEquipmentWorkShift = new DzEquipmentWorkShift();
                BeanUtils.copyProperties(addWorkShiftVo, dzEquipmentWorkShift);
                dzEquipmentWorkShift.setProductionLineId(dzProductionLine.getId());
                dzEquipmentWorkShift.setOrgCode(byUserName.getUseOrgCode());
                dzEquipmentWorkShift.setCreateBy(byUserName.getRealname());
                workShiftMapper.insert(dzEquipmentWorkShift);
            }
        }
        dzDetectionTemplCache.deleteLineIdByOrderNoLineNo();
        return new Result(CustomExceptionType.OK, i);
    }

    @Override
    public Result<LineDo> getById(String sub, Long lineId) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode() && byUserName.getUseOrgCode().equals(FinalCode.DZ_USE_ORG_CODE)) {
            byUserName.setUseOrgCode(null);
        }
        SelectLineVo selectLineVo = new SelectLineVo();
        selectLineVo.setUseOrgCode(byUserName.getUseOrgCode());
        selectLineVo.setId(lineId);
        List<LineDo> list = dzProductionLineMapper.list(selectLineVo);
        if (list.size() == 1) {
            LineDo lineDo = list.get(0);
            List<DzEquipmentWorkShiftDo> workShifts = dzEquipmentWorkShiftMapper.getListByLineId(lineId, byUserName.getUseOrgCode());
            lineDo.setWorkShiftVos(workShifts);
            return new Result(CustomExceptionType.OK, lineDo);
        }
        return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_35);

    }

    @Override
    public Result putStatus(String sub, Long id) {
        DzProductionLine dzProductionLine = dzProductionLineMapper.selectById(id);
        if (dzProductionLine == null) {//
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_28);
        }
        if (dzProductionLine.getStatus() == 0) {
            //???????????????  ????????????
            dzProductionLine.setStatus(1);
        } else if (dzProductionLine.getStatus() == 1) {
            //??????????????????????????????
            dzProductionLine.setStatus(0);
        }
        dzProductionLineMapper.updateById(dzProductionLine);
        return new Result(CustomExceptionType.OK, dzProductionLine);
    }

    @Override
    public boolean lineWorkTime(List<AddWorkShiftVo> workShiftVos) throws Exception {
        if (workShiftVos == null || workShiftVos.size() == 0) {
            throw new Exception("?????????????????????");
        }
        for (int i = 0; i < workShiftVos.size(); i++) {
            for (int j = 0; j < workShiftVos.size(); j++) {
                if (i != j) {//???????????????????????????
                    LocalTime endTime1 = workShiftVos.get(i).getEndTime();//??????1????????????
                    LocalTime startTime1 = workShiftVos.get(i).getStartTime();//??????1????????????

                    LocalTime endTime2 = workShiftVos.get(j).getEndTime();//??????2????????????
                    LocalTime startTime2 = workShiftVos.get(j).getStartTime();//??????2????????????
                    //??????????????????????????????????????????????????????08:00:00-14:00:00???
                    if (endTime1.isAfter(startTime1)) {
                        if (endTime2.isAfter(startTime2)) {
                            if (!(startTime1.compareTo(endTime2) == 1 || startTime1.compareTo(endTime2) == 0 || endTime1.compareTo(startTime2) == 0 || endTime1.compareTo(startTime2) == -1)) {
                                log.error("???????????????:{}---{}", workShiftVos.get(i).toString(), workShiftVos.get(j).toString());
                                return false;
                            }
                        } else {
                            if (!((startTime1.compareTo(endTime2) == 1 || startTime1.compareTo(endTime2) == 0) && (endTime1.compareTo(startTime2) == -1 || endTime1.compareTo(startTime2) == 0))) {
                                log.error("???????????????:{}---{}", workShiftVos.get(i).toString(), workShiftVos.get(j).toString());
                                return false;
                            }
                        }
                    } else {//?????????????????????????????????????????????22:00:00-06:00:00???
                        if (startTime2.isAfter(endTime2)) {
                            log.error("???????????????:{}---{}", workShiftVos.get(i).toString(), workShiftVos.get(j).toString());
                            return false;
                        }
                        if (!((startTime1.compareTo(endTime2) == 1 || startTime1.compareTo(endTime2) == 0) && (endTime1.compareTo(startTime2) == -1 || endTime1.compareTo(startTime2) == 0))) {
                            log.error("???????????????:{}---{}", workShiftVos.get(i).toString(), workShiftVos.get(j).toString());
                            return false;
                        }

                    }


                }

            }
        }
        return true;
    }

    @Override
    public Result<DzProductionLine> getByOrderId(String sub, Long id) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        QueryWrapper<DzProductionLine> eq = new QueryWrapper<DzProductionLine>().eq("order_id", id);
        if (byUserName.getUserIdentity().intValue() != UserIdentityEnum.DZ.getCode().intValue()) {
            eq.eq("org_code", byUserName.getUseOrgCode());
        }
        List<DzProductionLine> dzProductionLines = dzProductionLineMapper.selectList(eq);
        return new Result(CustomExceptionType.OK, dzProductionLines);
    }

    @Override
    public Result bingEquipment(String sub, BingEquipmentVo bingEquipmentVo) {
        DzProductionLine dzProductionLine = dzProductionLineMapper.selectById(bingEquipmentVo.getLineId());
        dzProductionLine.setStatisticsEquimentId(bingEquipmentVo.getEquipmentId());
        dzProductionLineMapper.updateById(dzProductionLine);
        return new Result(CustomExceptionType.OK, dzProductionLine);
    }

    @Override
    public Result allLineList(String sub) {
        List<LineListDo> list = dzProductionLineMapper.allLineList();
        return new Result(CustomExceptionType.OK, list, list.size());
    }

    @Override
    public Result allList(String sub) {
        String orgCode = sysUserServiceDao.getUserOrgCode(sub);
        QueryWrapper<DzProductionLine> wrapper = new QueryWrapper<>();
        if (orgCode != null) {
            wrapper.eq("org_code", orgCode);
        }
        wrapper.select("id", "line_name");
        List<DzProductionLine> dzProductionLines = dzProductionLineMapper.selectList(wrapper);
        List<GeneralLineDo> list = new ArrayList<>();
        for (DzProductionLine data : dzProductionLines) {
            GeneralLineDo generalLineDo = new GeneralLineDo();
            generalLineDo.setLineId(data.getId());
            generalLineDo.setLineName(data.getLineName());
            list.add(generalLineDo);
        }
        return Result.ok(list);
    }

    @Override
    public Result getByOrderIdV2(String sub, Long ordeId) {
        List<Lines> proLines = dzProductionLineMapper.getByOerderId(ordeId);
        return Result.ok(proLines);
    }

    @Override
    public DzProductionLine getLineId(Long lineId) {
        return dzProductionLineMapper.selectById(lineId);

    }
}
