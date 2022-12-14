package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.DzEquipmentProNumSignalMapper;
import com.dzics.common.dao.DzWorkStationManagementMapper;
import com.dzics.common.dao.DzWorkingFlowMapper;
import com.dzics.common.dao.DzWorkpieceDataMapper;
import com.dzics.common.exception.CustomException;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.constant.QrCode;
import com.dzics.common.model.constant.WorkingProcedureCode;
import com.dzics.common.model.constant.WorkpieceOkNg;
import com.dzics.common.model.custom.OrderIdLineId;
import com.dzics.common.model.entity.*;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.mom.GetWorkDateDo;
import com.dzics.common.model.response.mom.WorkStationParms;
import com.dzics.common.model.response.productiontask.ProDetection;
import com.dzics.common.model.response.productiontask.ProcedureAndStation;
import com.dzics.common.model.response.productiontask.WorkingProcedureStation;
import com.dzics.common.model.response.productiontask.station.ResponseWorkStation;
import com.dzics.common.model.response.productiontask.station.StationModel;
import com.dzics.common.model.response.productiontask.station.WorkingFlowRes;
import com.dzics.common.model.response.productiontask.station.WorkingProcedureModel;
import com.dzics.common.model.response.productiontask.stationbg.*;
import com.dzics.common.service.*;
import com.dzics.common.util.DateUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * ???????????????????????? ???????????????
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-19
 */
@Service
@Slf4j
public class DzWorkingFlowServiceImpl extends ServiceImpl<DzWorkingFlowMapper, DzWorkingFlow> implements DzWorkingFlowService {

    @Autowired
    private MomOrderService momOrderService;
    @Autowired
    private DzLineShiftDayService shiftDayService;
    @Autowired
    private DzWorkpieceDataMapper dzWorkpieceDataMapper;
    @Autowired
    private DzWorkpieceDataService workpieceDataService;
    @Autowired
    DzWorkingFlowMapper dzWorkingFlowMapper;
    @Autowired
    private DateUtil dateUtil;
    @Autowired
    private DzWorkStationManagementMapper stationManagementMapper;
    @Autowired
    private DzProductionLineService dzProductionLineService;
    @Autowired
    private DzEquipmentProNumSignalMapper signalService;

    @Override
    public DzWorkingFlow getQrCodeStationCode(String stationId, String qrCode, Long orderId, Long lineId) {
        QueryWrapper<DzWorkingFlow> wp = new QueryWrapper<>();
        wp.eq("line_id", lineId);
        wp.eq("order_id", orderId);
        wp.eq("station_id", stationId);
        wp.eq("qr_code", qrCode);
        wp.select("process_flow_id", "line_id", "order_id", "working_procedure_id", "station_id", "pro_task_id", "qr_code", "start_time", "complete_time");
        DzWorkingFlow list = getOne(wp);
        return list;
    }

    /**
     * ??????????????????
     * */
    public List<String> getNowWorkErrNum(String orderId,String beginTime,String endTime,String stationId){
        QueryWrapper<DzWorkingFlow>queryWrapper=new QueryWrapper<>();
        List<String> list = new ArrayList<>();
//        queryWrapper.eq("order_id",orderId);
//        queryWrapper.between("start_ropert_time",beginTime,endTime);
//        queryWrapper.eq("start_reporting_status",3);
//        //???????????? ????????????
//        List<DzWorkingFlow> flows1 = dzWorkingFlowMapper.selectList(queryWrapper);
//        List<String> beginList = flows1.stream().map(s -> s.getQrCode()).collect(Collectors.toList());
//        queryWrapper.clear();

        //???????????? ????????????
        queryWrapper.eq("order_id",orderId);
        queryWrapper.eq("station_id",stationId);
        queryWrapper.between("complete_ropert_time",beginTime,endTime);
        queryWrapper.eq("complete_reporting_status",3);
        List<DzWorkingFlow> flows2 = dzWorkingFlowMapper.selectList(queryWrapper);
        List<String> endList = flows2.stream().map(s -> s.getQrCode()).collect(Collectors.toList());
        list.addAll(endList);
        list = list.stream().distinct().collect(Collectors.toList());
        return list;
    }

    /**
     * Ok????????????
     * */
    public List<String> getNowWorkOkNum(String orderId,String beginTime,String endTime,String id){
        List<DzWorkingFlow> flows = dzWorkingFlowMapper.selectList(new QueryWrapper<DzWorkingFlow>().eq("order_id", orderId)
                .eq("station_id",id)
                .eq("complete_reporting_status",1)
                .between("complete_ropert_time", beginTime, endTime));
        List<String> list = flows.stream().map(s -> s.getQrCode()).collect(Collectors.toList());
        list = list.stream().distinct().collect(Collectors.toList());
        return list;
    }

    @Override
    public Result<GetWorkDateDo> getDayWorkDate(DzProductionLine line) throws ParseException {
        GetWorkDateDo getWorkDateDo = new GetWorkDateDo();
        //??????????????????????????????
        List<DzLineShiftDay> list = shiftDayService.list(new QueryWrapper<DzLineShiftDay>().eq("line_id", line.getId()).eq("work_data",dateUtil.getDate()).eq("eq_id", line.getStatisticsEquimentId()));
        if(CollectionUtils.isEmpty(list)){
            log.error("????????????"+line.getOrderNo()+"???????????????????????????????????????????????????????????????");
            return Result.OK(getWorkDateDo);
        }
        Date begin_where = null;//??????????????????????????????
        Date end_where = null;//??????????????????????????????
        String dayId = "";
        SimpleDateFormat df = new SimpleDateFormat("HH:mm"); // ??????????????????
        ZoneId zone = ZoneId.systemDefault();
        try {
            for (DzLineShiftDay day : list) {
                //?????????????????????????????????????????????
                if(df.parse(day.getStartTime().toString()).getTime()<df.parse(day.getEndTime().toString()).getTime()){
                    LocalDateTime ldt1 = LocalDateTime.of(day.getWorkData(), day.getStartTime());
                    begin_where = Date.from(ldt1.atZone(zone).toInstant());

                    LocalDateTime ldt2 = LocalDateTime.of(day.getWorkData(), day.getEndTime());
                    end_where = Date.from(ldt2.atZone(zone).toInstant());
                }else if(df.parse(day.getStartTime().toString()).getTime()>df.parse(day.getEndTime().toString()).getTime()){
                    //?????????????????????????????????????????????
                    LocalDateTime ldt1 = LocalDateTime.of(day.getWorkData(), day.getStartTime());
                    begin_where = Date.from(ldt1.atZone(zone).toInstant());

                    LocalDateTime ldt2 = LocalDateTime.of(day.getWorkData().plusDays(1), day.getEndTime());
                    end_where = Date.from(ldt2.atZone(zone).toInstant());
                }
                //???????????????????????????????????????
                boolean b = belongCalendar(new Date(),begin_where,end_where);
                if(b==true){
                    dayId = String.valueOf(day.getId());
                    break;
                }
            }
        }catch (Exception e){
                   e.printStackTrace();
        }
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String beginFormat = df1.format(begin_where);
        String EndFormat = df1.format(end_where);
        //????????????????????????????????????
        List<DzWorkStationManagement> managements = stationManagementMapper.selectList(new QueryWrapper<DzWorkStationManagement>()
                .eq("order_id", line.getOrderId()).isNotNull("dz_station_code").orderByDesc("dz_station_code"));
        //?????????????????? ???Mom???????????????????????????
        String stationId = managements.stream().map(m -> m.getStationId()).collect(Collectors.toList()).get(0);
        Integer planNum = momOrderService.getNowWorkPlanNum(String.valueOf(line.getOrderId()), beginFormat, EndFormat);
        getWorkDateDo.setPlanNum(String.valueOf(planNum));
        long sum = signalService.selectList(new QueryWrapper<DzEquipmentProNumSignal>().eq("day_id", dayId)).stream().mapToLong(DzEquipmentProNumSignal::getNowNum).sum();
        getWorkDateDo.setFinishNum(String.valueOf(sum));
        List<String> okNum = getNowWorkOkNum(line.getOrderId().toString(),beginFormat, EndFormat,stationId);
        getWorkDateDo.setOkNum(String.valueOf(okNum.size()));
        List<String> errNum = getNowWorkErrNum(line.getOrderId().toString(), beginFormat, EndFormat,stationId);
        getWorkDateDo.setErrNum(String.valueOf(errNum.size()));
        return Result.ok(getWorkDateDo);
    }



    /**
     * ?????????????????????????????????
     *
     * @param nowTime
     * @param beginTime
     * @param endTime
     * @return
     */
    public static boolean belongCalendar(Date nowTime, Date beginTime, Date endTime) {
        if (nowTime.getTime() == beginTime.getTime() || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * ??????????????????????????????????????????
     *
     * @param list ???????????????
     * @return
     */
    @Override
    public List<ResponseWorkStation> getWorkpiecePosition(List<String> list) {
        long stMils = System.currentTimeMillis();
//      ???????????????????????????????????????
        List<WorkingFlowRes> workingFlow = getWorkingFlow(list, 1L, 1L);
//        ???????????????|??????, ????????????
        Map<String, List<WorkingFlowRes>> groupFolw = groupFolw(workingFlow);
//        ??????????????????????????? ?????????????????? ?????? ??????
        Map<String, WorkingFlowRes> toRepeat = toRepeat(workingFlow, list);
        if (CollectionUtils.isNotEmpty(toRepeat)) {
            List<ResponseWorkStation> arrayList = new ArrayList<>();
            Map<String, WorkingProcedureStation> workingProcedure = getWorkingProcedure();
//            ?????????????????????????????????
            for (Map.Entry<String, WorkingFlowRes> gJ : toRepeat.entrySet()) {
                WorkingFlowRes flowRes = gJ.getValue();
                flowRes.setUpdateTimeUse(dateUtil.dateFormatToStingYmdHms(flowRes.getUpdateTime()));
//                ??????id
                String key = gJ.getKey();
                ResponseWorkStation responseWorkStation = new ResponseWorkStation();
                responseWorkStation.setWorkingFlowRes(flowRes);
//              ?????????????????? ??????????????? ??????
                List<WorkingProcedureModel> workingProcedureModels = getWorkingProcedureGw(flowRes, workingProcedure, groupFolw);
//                ???????????????
                List<WorkingProcedureModel> workingProcedureModelsXin = getSortWorKing(workingProcedureModels);
//               ?????????????????????????????????????????????????????????????????????????????????????????????????????????
                List<WorkingProcedureModel> worpProced = workingProcedureRest(workingProcedureModelsXin);
//              ????????????????????????
                responseWorkStation.setWorkingProcedureModels(worpProced);
                arrayList.add(responseWorkStation);
            }
//            ??????????????????????????????????????????
            List<ResponseWorkStation> workStations = verificationWorkingFlowRes(arrayList);
            long timeMillis = System.currentTimeMillis();
            long ch = timeMillis - stMils;
            log.debug("???????????????????????????????????????{}", ch);
            return workStations;
        } else {
            log.warn("??????????????????????????????????????????????????????");
            throw new CustomException(CustomExceptionType.SYSTEM_ERROR, CustomResponseCode.ERR42);
        }
    }

    private List<ResponseWorkStation> verificationWorkingFlowRes(List<ResponseWorkStation> arrayList) {
//        ????????????
        for (ResponseWorkStation responseWorkStation : arrayList) {
//            ??????????????????????????????
            String qrCode = responseWorkStation.getWorkingFlowRes().getQrCode();
//            ????????????????????????
            List<DzWorkpieceData> workpieceData = dzWorkpieceDataMapper.getQrCodeOutOk(qrCode);
            if (workpieceData != null) {
//            ??????????????????????????????????????? ????????????????????????????????????
//              ????????????
                List<WorkingProcedureModel> workingProcedureModels = responseWorkStation.getWorkingProcedureModels();
                if (CollectionUtils.isNotEmpty(workingProcedureModels)) {
//                    ????????????????????????????????????
                    for (WorkingProcedureModel workingProcedureModel : workingProcedureModels) {
                        List<StationModel> stationModels = workingProcedureModel.getStationModels();
                        if (stationModels != null) {
                            boolean b = checkStation(workpieceData, stationModels);
                            if (b) {
                                workingProcedureModel.setState(WorkingProcedureCode.ERR);
                            }
                        }

                    }
                }
            }
//            ??????????????????????????????????????????

        }
        return arrayList;
    }

    private boolean checkStation(List<DzWorkpieceData> workpieceData, List<StationModel> stationModels) {
        for (StationModel stationModel : stationModels) {
            String stationCode = stationModel.getStationCode();
            for (DzWorkpieceData workpieceDatum : workpieceData) {
                String workNumber = workpieceDatum.getWorkNumber();
                if (stationCode.equals(workNumber)) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<WorkingProcedureModel> workingProcedureRest(List<WorkingProcedureModel> workingProcedureModelsXin) {
//       ????????????????????????????????????
        int fal = -1;
        for (int i = workingProcedureModelsXin.size() - 1; i >= 0; i--) {
            Integer state = workingProcedureModelsXin.get(i).getState();
            if (state.intValue() == WorkingProcedureCode.IN || state.intValue() == WorkingProcedureCode.OUT) {
                fal = i;
                break;
            }

        }
//        ?????????????????????????????????????????????????????? ???????????????????????????
        for (int i = 0; i < workingProcedureModelsXin.size(); i++) {
            if (i < fal) {
                WorkingProcedureModel workingProcedureModel = workingProcedureModelsXin.get(i);
                List<StationModel> stationModels = workingProcedureModel.getStationModels();
                boolean falg = false;
                if (stationModels != null) {
                    for (StationModel stationModel : stationModels) {
                        Integer state = stationModel.getState();
//                    ???????????????
                        if (state.intValue() == WorkingProcedureCode.OUT || state.intValue() == WorkingProcedureCode.IN) {
                            falg = true;
                            break;
                        }
                    }
                }

                if (falg) {
                    workingProcedureModelsXin.get(i).setState(WorkingProcedureCode.OUT);
                }
            }
        }
        return workingProcedureModelsXin;
    }

    private List<WorkingProcedureModel> getSortWorKing(List<WorkingProcedureModel> list) {
        Collections.sort(list, new Comparator<WorkingProcedureModel>() {
            @Override
            public int compare(WorkingProcedureModel o1, WorkingProcedureModel o2) {
                return o1.getSortCode().compareTo(o2.getSortCode());
            }
        });
        return list;
    }

    private Map<String, List<WorkingFlowRes>> groupFolw(List<WorkingFlowRes> workingFlow) {
        Map<String, List<WorkingFlowRes>> map = new HashMap<>();
        for (WorkingFlowRes flowRes : workingFlow) {
            String workingProcedureId = flowRes.getQrCode() + "|" + flowRes.getWorkingProcedureId();
            List<WorkingFlowRes> workingFlowRes = map.get(workingProcedureId);
            if (workingFlowRes == null) {
                workingFlowRes = new ArrayList<>();
                workingFlowRes.add(flowRes);
                map.put(workingProcedureId, workingFlowRes);
            } else {
                workingFlowRes.add(flowRes);
            }
        }
        return map;
    }

    /**
     * @param flowRes          ??????????????????
     * @param workingProcedure ?????????????????? ?????????ID value ????????????
     * @param groupFolw        ???????????? ?????????????????????
     * @return
     */
    private List<WorkingProcedureModel> getWorkingProcedureGw(WorkingFlowRes flowRes, Map<String, WorkingProcedureStation> workingProcedure, Map<String, List<WorkingFlowRes>> groupFolw) {
        List<WorkingProcedureModel> workingProcedureModels = new ArrayList<>();
//        ???????????????
        String qrCode = flowRes.getQrCode();
        for (Map.Entry<String, WorkingProcedureStation> prox : workingProcedure.entrySet()) {
            String workingProcedureId = prox.getKey();
//          ?????? ????????????
            WorkingProcedureStation station = prox.getValue();
            WorkingProcedureModel procedureModel = station.getProcedureModel();
//          ????????? ??? ??????????????????
            List<StationModel> stationModels = station.getStationModels();
//            ????????????????????????
            List<StationModel> stationModelsXin = new ArrayList<>();
            stationModelsXin.addAll(stationModels);
//          ???????????????????????????????????????????????????
            List<WorkingFlowRes> workingFlowRes = groupFolw.get(qrCode + "|" + workingProcedureId);
            WorkingProcedureModel workingProcedureModel = new WorkingProcedureModel();
            BeanUtils.copyProperties(procedureModel, workingProcedureModel);
            if (CollectionUtils.isNotEmpty(workingFlowRes)) {
//                ??????????????????
                int falgIn = 0;
                int falgOut = 0;
                for (StationModel stationModel : stationModelsXin) {
                    String stationIdAll = stationModel.getStationId();
                    for (WorkingFlowRes workingFlowRe : workingFlowRes) {
                        String stationIdNow = workingFlowRe.getStationId();
                        if (stationIdAll.equals(stationIdNow)) {
                            if (workingFlowRe.getCompleteTime() != null) {
                                ++falgOut;
                                stationModel.setState(WorkingProcedureCode.OUT);
                                break;
                            } else {
                                ++falgIn;
                                stationModel.setState(WorkingProcedureCode.IN);
                                break;
                            }

                        }
                    }
                }
                workingProcedureModel.setStationModels(stationModelsXin);
                int size = stationModelsXin.size();
                if (falgOut == size) {
                    workingProcedureModel.setState(WorkingProcedureCode.OUT);
                } else {
                    if (falgIn > 0 || falgOut > 0) {
                        workingProcedureModel.setState(WorkingProcedureCode.IN);
                    } else {
                        workingProcedureModel.setState(WorkingProcedureCode.NOT);
                    }
                }
            } else {
//                ????????????????????????
                workingProcedureModel.setState(WorkingProcedureCode.NOT);
            }
            workingProcedureModels.add(workingProcedureModel);
        }
        return workingProcedureModels;
    }

    /**
     * ?????????????????????????????????????????????
     * map ??? ????????? ?????????
     * ????????????
     *
     * @param workingFlowRes
     * @return
     */
    @Override
    public Map<String, WorkingFlowRes> toRepeat(List<WorkingFlowRes> workingFlowRes, List<String> qrCodes) {
        Map<String, WorkingFlowRes> map = new LinkedHashMap<>();
        for (WorkingFlowRes dzWorkingFlow : workingFlowRes) {
            String qrCode = dzWorkingFlow.getQrCode();
            WorkingFlowRes flowRes = map.get(qrCode);
            if (flowRes == null) {
                map.put(qrCode, dzWorkingFlow);
            } else {
//                ????????????map ???????????????
                Date beforeUpdateTime = flowRes.getUpdateTime();
                Date afterUpdateTime = dzWorkingFlow.getUpdateTime();
//                    ?????????????????????????????? map ????????????????????????
                if (afterUpdateTime.after(beforeUpdateTime)) {
//                        ??????
                    map.put(qrCode, dzWorkingFlow);
                }
            }
        }
        Map<String, WorkingFlowRes> mapSort = new LinkedHashMap<>();
        for (String qrCode : qrCodes) {
            WorkingFlowRes flowRes = map.get(qrCode);
            if (flowRes != null) {
                mapSort.put(qrCode, flowRes);
            }
        }
        return mapSort;
    }


    /**
     * ????????????????????????????????????
     * ????????????????????????
     *
     * @return
     */
    @Override
    public Map<String, WorkingProcedureStation> getWorkingProcedure() {
        List<ProcedureAndStation> procedureAndStations = dzWorkingFlowMapper.getWorkingProcedureAndStation();
        if (CollectionUtils.isEmpty(procedureAndStations)) {
            throw new CustomException(CustomExceptionType.SYSTEM_ERROR, CustomResponseCode.ERR41);
        }
        Map<String, List<StationModel>> listMap = new HashMap<>();
//        ???????????? ??????????????????
        for (ProcedureAndStation andStation : procedureAndStations) {
            String stationId = andStation.getStationId();
            String stationName = andStation.getStationName();
            String stationCode = andStation.getStationCode();
            List<StationModel> stationModels = listMap.get(andStation.getWorkingProcedureId());
            if (stationModels == null) {
                stationModels = new ArrayList<>();
                listMap.put(andStation.getWorkingProcedureId(), stationModels);
            }
            StationModel stationModel = new StationModel();
            stationModel.setStationCode(stationCode);
            stationModel.setStationId(stationId);
            stationModel.setStationName(stationName);
            stationModels.add(stationModel);

        }
        Map<String, WorkingProcedureStation> map = new HashMap<>();
        for (ProcedureAndStation procedureAndStation : procedureAndStations) {
//            ????????????
            String workingProcedureId = procedureAndStation.getWorkingProcedureId();
            List<StationModel> stationModels = listMap.get(workingProcedureId);
            WorkingProcedureStation workingProcedureStation = new WorkingProcedureStation();
//            ????????????
            WorkingProcedureModel procedureModel = new WorkingProcedureModel();
            procedureModel.setWorkcode(procedureAndStation.getWorkCode());
            procedureModel.setWorkingProcedureId(workingProcedureId);
            procedureModel.setSortCode(procedureAndStation.getSortCode());
            procedureModel.setWorkName(procedureAndStation.getWorkName());
//            ??????????????????
            workingProcedureStation.setProcedureModel(procedureModel);
//            ??????????????????
            workingProcedureStation.setStationModels(stationModels);
            map.put(workingProcedureId, workingProcedureStation);
        }
        return map;
    }

    @Override
    public List<WorkingFlowRes> getWorkingFlow(List<String> qrCode, Long orderId, Long lineId) {

        if (CollectionUtils.isNotEmpty(qrCode)) {
            List<WorkingFlowRes> workingFlow = dzWorkingFlowMapper.getWorkingFlow(qrCode, orderId, lineId);
            return workingFlow;
        }
        throw new CustomException(CustomExceptionType.SYSTEM_ERROR, CustomResponseCode.ERR42);

    }

    /**
     * ???????????? ?????????????????????
     *
     * @param qrCode
     * @param orderId
     * @param lineId
     * @return
     */
    @Override
    public List<ResponseWorkStationBg> getPosition(List<String> qrCode, Long orderId, Long lineId) {
        long stMils = System.currentTimeMillis();
//      ???????????????????????????????????????
        List<WorkingFlowRes> workingFlow = getWorkingFlow(qrCode, orderId, lineId);
//        ???????????????????????????
        Map<String, List<WorkingFlowRes>> workingFlowResMap = getGroupQrCode(workingFlow);
//        ??????????????????????????? ?????????????????? ?????? ??????
        Map<String, WorkingFlowRes> toRepeat = toRepeat(workingFlow, qrCode);
//      ??????????????????????????????????????????
        List<StationModelAll> stationModels = stationManagementMapper.getSortPosition(orderId, lineId);

        if (CollectionUtils.isNotEmpty(toRepeat)) {
            List<ResponseWorkStationBg> stationBgs = new ArrayList<>();
//            ???????????????????????????
            for (Map.Entry<String, WorkingFlowRes> qrCodeFlow : toRepeat.entrySet()) {
                String key = qrCodeFlow.getKey();
                WorkingFlowRes value = qrCodeFlow.getValue();
                WorkingFlowResBg workingFlowResBg = new WorkingFlowResBg();
                workingFlowResBg.setQrCode(key);
                workingFlowResBg.setWorkpieceCode(value.getWorkpieceCode());
                workingFlowResBg.setUpdateTimeUse(dateUtil.dateFormatToStingYmdHms(value.getUpdateTime()));
                ResponseWorkStationBg responseWorkStationBg = new ResponseWorkStationBg();
                responseWorkStationBg.setWorkingFlowRes(workingFlowResBg);
//                ????????????????????????????????????
                List<WorkingFlowRes> workingFlowRes = workingFlowResMap.get(key);
//               ?????????????????????????????????????????? ??????
                List<StationModelAll> stationModelAllList = getQrCodeStationCheck(workingFlowRes, stationModels);
                responseWorkStationBg.setStationModels(stationModelAllList);
                stationBgs.add(responseWorkStationBg);
            }
//           ??????????????????????????????????????????
//            delOutFalgStation(stationBgs);
//           ????????????????????????  ??????????????????????????????
            getQrCodeCheckDate(stationBgs, orderId, lineId);
//          ???????????????
            taskTime(stationBgs);
//          ??????????????????????????????????????????????????????
//            mergeStation(stationBgs);
            long timeMillis = System.currentTimeMillis();
            long ch = timeMillis - stMils;
            log.debug("???????????????????????????????????????{}", ch);
            return stationBgs;
        } else {
            log.warn("??????????????????????????????????????????????????????");
            throw new CustomException(CustomExceptionType.SYSTEM_ERROR, CustomResponseCode.ERR42);
        }

    }


    @Override
    public ProDetection getPositionTable(List<String> qrCode, Long orderId, Long lineId,String single) {
        List<ResponseWorkStationBg> position = getPosition(qrCode, orderId, lineId);
        if (CollectionUtils.isNotEmpty(position)){
            List<Map<String, Object>> stationModelAlls = stationManagementMapper.getSortPositionOnOff(orderId, lineId, 1);
            Map<String, String> colData = getStationColData(stationModelAlls);
            List<Map<String, Object>> header = responseHead(stationModelAlls);
            List<Map<String, Object>> flowRes = new ArrayList<>();
            for (ResponseWorkStationBg stationBg : position) {
                WorkingFlowResBg workingFlowRes = stationBg.getWorkingFlowRes();
                Map<String,Object>  mapAdd = new LinkedHashMap<>();
                List<StationModelAll> stationModels = stationBg.getStationModels();
                for (StationModelAll stationModel : stationModels) {
                    String stationId = stationModel.getStationId();
                    String colDataVal = colData.get(stationId);
                    if (!StringUtils.isEmpty(colDataVal)) {
                        StationModelTable table = new StationModelTable();
                        table.setStartTime(stationModel.getStartTime());
                        table.setCompleteTime(stationModel.getCompleteTime());
                        table.setTaktTime(stationModel.getTaktTime());
                        table.setState(stationModel.getState());
                        mapAdd.put(colDataVal, table);
                    }
                }
                mapAdd.put("workMsg", workingFlowRes);
                flowRes.add(mapAdd);
            }
            ProDetection proDetection = new ProDetection();
            if ("single".equals(single)){
                proDetection.setTableData(flowRes.get(0));
            }else {
               proDetection.setTableColumn(header);
               proDetection.setTableData(flowRes);
            }
            return proDetection;
        }
        throw new CustomException(CustomExceptionType.SYSTEM_ERROR, CustomResponseCode.ERR42);
    }



    private List<Map<String, Object>> responseHead(List<Map<String, Object>> stationModelAlls) {
        List<Map<String, Object>> maps = new ArrayList<>();
        Map<String, Object> workMsg = new HashMap<>();
        workMsg.put("colData", "workMsg");
        workMsg.put("colName", "????????????");
        maps.add(workMsg);
        for (Map<String, Object> stationModelAll : stationModelAlls) {
            Map<String, Object> map = new HashMap<>();
            map.put("colData", stationModelAll.get("colData"));
            map.put("colName", stationModelAll.get("colName"));
            maps.add(map);
        }
        return maps;
    }

    private void delOutFalgStation(List<ResponseWorkStationBg> stationBgs) {
        for (ResponseWorkStationBg stationBg : stationBgs) {
            List<StationModelAll> stationModelAlls = new ArrayList<>();
            List<StationModelAll> stationModels = stationBg.getStationModels();
            for (StationModelAll stationModel : stationModels) {
                String outFlag = stationModel.getOutFlag();
                String completeTime = stationModel.getCompleteTime();
                String startTime = stationModel.getStartTime();
                stationModelAlls.add(stationModel);
                if ("1".equals(outFlag) && (!StringUtils.isEmpty(completeTime) || !StringUtils.isEmpty(startTime))) {
                    break;
                }
            }
            stationBg.setStationModels(stationModelAlls);
        }
    }

    private void mergeStation(List<ResponseWorkStationBg> stationBgs) {
        for (ResponseWorkStationBg stationBg : stationBgs) {
            LinkedHashMap<String, List<StationModelAll>> map = new LinkedHashMap<>();
            List<StationModelAll> stationModels = stationBg.getStationModels();
            for (StationModelAll stationModel : stationModels) {
                String mergeCode = stationModel.getMergeCode();
                List<StationModelAll> stationModelAlls = map.get(mergeCode);
                if (StringUtils.isEmpty(stationModelAlls)) {
                    stationModelAlls = new ArrayList<>();
                    stationModelAlls.add(stationModel);
                    map.put(mergeCode, stationModelAlls);
                } else {
                    stationModelAlls.add(stationModel);
                }
            }
            List<StationModelAll> stall = new ArrayList<>();
            for (Map.Entry<String, List<StationModelAll>> stringListEntry : map.entrySet()) {
                List<StationModelAll> value = stringListEntry.getValue();
                if (stringListEntry.getKey() == null) {
                    stall.addAll(value);
                    continue;
                }
                boolean falg = false;
                for (StationModelAll stationModelAll : value) {
                    String startTime = stationModelAll.getStartTime();
                    String completeTime = stationModelAll.getCompleteTime();
                    if (!StringUtils.isEmpty(startTime) || !StringUtils.isEmpty(completeTime)) {
                        stall.add(stationModelAll);
                        falg = true;
                        break;
                    }
                }
                if (!falg) {
                    stall.addAll(value);
                }
            }
            stall.sort(Comparator.comparing(StationModelAll::getSortCode));
            stationBg.setStationModels(stall);
        }
    }

    private void taskTime(List<ResponseWorkStationBg> stationBgs) {
        for (ResponseWorkStationBg stationBg : stationBgs) {
            List<StationModelAll> stationModels = stationBg.getStationModels();
            try {
                if (CollectionUtils.isNotEmpty(stationModels)) {
                    String stTime = null;
                    String enTime = null;
                    for (StationModelAll stationModel : stationModels) {
                        String startTime = stationModel.getStartTime();
                        String completeTime = stationModel.getCompleteTime();
                        if (StringUtils.isEmpty(stTime)) {
                            if (!StringUtils.isEmpty(startTime)) {
                                stTime = startTime;
                                if (StringUtils.isEmpty(enTime)) {
                                    enTime = completeTime;
                                }
                                continue;
                            }
                            if (!StringUtils.isEmpty(completeTime)) {
                                stTime = completeTime;
                                continue;
                            }
                        } else {
                            if (!StringUtils.isEmpty(completeTime)) {
                                enTime = completeTime;
                                continue;
                            }
                            if (!StringUtils.isEmpty(startTime)) {
                                enTime = startTime;
                                continue;
                            }

                        }
                    }
                    if (!StringUtils.isEmpty(stTime) && !StringUtils.isEmpty(enTime)) {
                        long stDate = dateUtil.stringDateToformatDateYmdHms(stTime).getTime();
                        long enDate = dateUtil.stringDateToformatDateYmdHms(enTime).getTime();
                        WorkingFlowResBg workingFlowRes = stationBg.getWorkingFlowRes();
                        long task = (enDate - stDate) / 1000;
                        workingFlowRes.setTaktTime(getGapTime(Long.valueOf(task).intValue()));
                    }

                }
            } catch (Throwable throwable) {
                log.error("?????????????????????: {}", throwable.getMessage(), throwable);
            }

        }
    }

    /**
     * @param outInputType  ???????????? 1????????? 2?????????
     * @param processFlowId
     * @param isSendOK
     * @return
     */
    @Override
    public boolean updateQrcodeOutInptType(String outInputType, String processFlowId, String isSendOK) {
        DzWorkingFlow dzWorkingFlow = this.getById(processFlowId);
        if (QrCode.QR_CODE_IN.equals(outInputType)) {
            log.info("????????????MOM???????????????processFlowId???{}", processFlowId);
//         1?????????
            DzWorkingFlow workingFlow = new DzWorkingFlow();
//         ????????????????????????
            workingFlow.setStartRopertTime(new Date());
            workingFlow.setStartReportingStatus(isSendOK);
            workingFlow.setProcessFlowId(processFlowId);
            workingFlow.setStartReportingFrequency(dzWorkingFlow.getStartReportingFrequency() + 1);
            boolean update = this.updateById(workingFlow);
            log.info("????????????MOM?????????????????? update: {},workingFlow: {}", update, workingFlow);
            return update;
        } else if (QrCode.QR_CODE_OUT.equals(outInputType)) {
            log.info("????????????MOM???????????????processFlowId???{}", processFlowId);
//          2?????????
            DzWorkingFlow workingFlow = new DzWorkingFlow();
//         ???????????????????????? completeRopertTime
            workingFlow.setCompleteRopertTime(new Date());
            workingFlow.setCompleteReportingStatus(isSendOK);
            workingFlow.setProcessFlowId(processFlowId);
            workingFlow.setCompleteReportingFrequency(dzWorkingFlow.getStartReportingFrequency() + 1);
            boolean update = this.updateById(workingFlow);
            log.info("????????????MOM?????????????????? update: {},workingFlow: {}", update, workingFlow);
            return update;
        } else {
            log.warn("????????????????????????  ?????????????????????????????????: processFlowId???{},outInputType: {}", processFlowId, outInputType);
            return false;
        }
    }

    @Override
    public Result getLineWorkPostion(WorkStationParms workStationParms) {
        try {
            String lineNo = workStationParms.getLineNo();
            String orderNo = workStationParms.getOrderNo();
            OrderIdLineId orderNoAndLineNo = dzProductionLineService.getOrderNoAndLineNo(orderNo, lineNo);
            if (orderNoAndLineNo == null) {
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR52);
            }
            Long lineId = orderNoAndLineNo.getLineId();
            Long orderId = orderNoAndLineNo.getOrderId();
            PageHelper.startPage(workStationParms.getPage(), workStationParms.getLimit());
            List<String> qrCodeX = dzWorkingFlowMapper.getWorkingFlowBigQrCode(orderId, lineId, workStationParms.getStartTime(), workStationParms.getEndTime());
            PageInfo<String> stringPageInfo = new PageInfo<>(qrCodeX);
            List<String> qrCode = stringPageInfo.getList();
            List<ResponseWorkStationBg> workpiecePosition = getPosition(qrCode, orderId, lineId);
            Result<List<ResponseWorkStationBg>> ok = Result.ok(workpiecePosition);
            ok.setCount(stringPageInfo.getTotal());
            return ok;
        } catch (Throwable throwable) {
            log.error("????????????????????????: {}", throwable.getMessage(), throwable);
            throw new CustomException(CustomExceptionType.SYSTEM_ERROR);
        }

    }


    /**
     * ????????????????????????  ??????????????????????????????
     *
     * @param stationBgs
     */
    private void getQrCodeCheckDate(List<ResponseWorkStationBg> stationBgs, Long orderId, Long lineId) {
        for (ResponseWorkStationBg stationBg : stationBgs) {
            String qrCode = stationBg.getWorkingFlowRes().getQrCode();
//            ???????????????
            DzWorkpieceData workpieceData = workpieceDataService.getQrCodeProduct(qrCode);
//             ?????????????????????????????????????????????
            if (workpieceData != null) {
                String productId = workpieceData.getProductId();
                if (StringUtils.isEmpty(productId)) {
                    return;
                }
//              ???????????????   ?????????????????????  ?????????????????????
                Map<String, List<CheckItems>> map = workpieceDataService.getProductIdCheckItems(productId, orderId, lineId);
                if (map != null) {
//                ????????????????????????
                    List<StationModelAll> stationModels = stationBg.getStationModels();
                    for (StationModelAll stationModel : stationModels) {
                        String stationModelStationId = stationModel.getStationId();
                        List<CheckItems> checkItems = map.get(stationModelStationId);
                        if (CollectionUtils.isNotEmpty(checkItems)) {
                            for (CheckItems checkItem : checkItems) {
                                String tableColVal = checkItem.getTableColVal();
                                if (tableColVal.equals("detect01")) {
                                    Integer outOk01 = workpieceData.getOutOk01();
                                    if (outOk01 != null && outOk01.intValue() == WorkpieceOkNg.WORKPIEC_NG) {
                                        stationModel.setState(WorkingProcedureCode.ERR);
                                        break;
                                    }
                                } else if (tableColVal.equals("detect02")) {
                                    Integer outOk02 = workpieceData.getOutOk02();
                                    if (outOk02 != null && outOk02.intValue() == WorkpieceOkNg.WORKPIEC_NG) {
                                        stationModel.setState(WorkingProcedureCode.ERR);
                                        break;
                                    }
                                } else if (tableColVal.equals("detect03")) {
                                    Integer outOk03 = workpieceData.getOutOk03();
                                    if (outOk03 != null && outOk03.intValue() == WorkpieceOkNg.WORKPIEC_NG) {
                                        stationModel.setState(WorkingProcedureCode.ERR);
                                        break;
                                    }
                                } else if (tableColVal.equals("detect04")) {
                                    Integer outOk04 = workpieceData.getOutOk04();
                                    if (outOk04 != null && outOk04.intValue() == WorkpieceOkNg.WORKPIEC_NG) {
                                        stationModel.setState(WorkingProcedureCode.ERR);
                                        break;
                                    }
                                } else if (tableColVal.equals("detect05")) {
                                    Integer outOk05 = workpieceData.getOutOk05();
                                    if (outOk05 != null && outOk05.intValue() == WorkpieceOkNg.WORKPIEC_NG) {
                                        stationModel.setState(WorkingProcedureCode.ERR);
                                        break;
                                    }
                                } else if (tableColVal.equals("detect06")) {
                                    Integer outOk06 = workpieceData.getOutOk06();
                                    if (outOk06 != null && outOk06.intValue() == WorkpieceOkNg.WORKPIEC_NG) {
                                        stationModel.setState(WorkingProcedureCode.ERR);
                                        break;
                                    }
                                } else if (tableColVal.equals("detect07")) {
                                    Integer outOk07 = workpieceData.getOutOk07();
                                    if (outOk07 != null && outOk07.intValue() == WorkpieceOkNg.WORKPIEC_NG) {
                                        stationModel.setState(WorkingProcedureCode.ERR);
                                        break;
                                    }
                                } else if (tableColVal.equals("detect08")) {
                                    Integer outOk08 = workpieceData.getOutOk08();
                                    if (outOk08 != null && outOk08.intValue() == WorkpieceOkNg.WORKPIEC_NG) {
                                        stationModel.setState(WorkingProcedureCode.ERR);
                                        break;
                                    }
                                } else if (tableColVal.equals("detect09")) {
                                    Integer outOk09 = workpieceData.getOutOk09();
                                    if (outOk09 != null && outOk09.intValue() == WorkpieceOkNg.WORKPIEC_NG) {
                                        stationModel.setState(WorkingProcedureCode.ERR);
                                        break;
                                    }
                                } else if (tableColVal.equals("detect10")) {
                                    Integer outOk10 = workpieceData.getOutOk10();
                                    if (outOk10 != null && outOk10.intValue() == WorkpieceOkNg.WORKPIEC_NG) {
                                        stationModel.setState(WorkingProcedureCode.ERR);
                                        break;
                                    }
                                } else if (tableColVal.equals("detect11")) {
                                    Integer outOk11 = workpieceData.getOutOk11();
                                    if (outOk11 != null && outOk11.intValue() == WorkpieceOkNg.WORKPIEC_NG) {
                                        stationModel.setState(WorkingProcedureCode.ERR);
                                        break;
                                    }
                                } else if (tableColVal.equals("detect12")) {
                                    Integer outOk12 = workpieceData.getOutOk12();
                                    if (outOk12 != null && outOk12.intValue() == WorkpieceOkNg.WORKPIEC_NG) {
                                        stationModel.setState(WorkingProcedureCode.ERR);
                                        break;
                                    }
                                } else if (tableColVal.equals("detect13")) {
                                    Integer outOk13 = workpieceData.getOutOk13();
                                    if (outOk13 != null && outOk13.intValue() == WorkpieceOkNg.WORKPIEC_NG) {
                                        stationModel.setState(WorkingProcedureCode.ERR);
                                        break;
                                    }
                                } else if (tableColVal.equals("detect14")) {
                                    Integer outOk14 = workpieceData.getOutOk14();
                                    if (outOk14 != null && outOk14.intValue() == WorkpieceOkNg.WORKPIEC_NG) {
                                        stationModel.setState(WorkingProcedureCode.ERR);
                                        break;
                                    }
                                } else if (tableColVal.equals("detect15")) {
                                    Integer outOk15 = workpieceData.getOutOk15();
                                    if (outOk15 != null && outOk15.intValue() == WorkpieceOkNg.WORKPIEC_NG) {
                                        stationModel.setState(WorkingProcedureCode.ERR);
                                        break;
                                    }
                                } else if (tableColVal.equals("detect16")) {
                                    Integer outOk16 = workpieceData.getOutOk16();
                                    if (outOk16 != null && outOk16.intValue() == WorkpieceOkNg.WORKPIEC_NG) {
                                        stationModel.setState(WorkingProcedureCode.ERR);
                                        break;
                                    }
                                } else if (tableColVal.equals("detect17")) {
                                    Integer outOk17 = workpieceData.getOutOk17();
                                    if (outOk17 != null && outOk17.intValue() == WorkpieceOkNg.WORKPIEC_NG) {
                                        stationModel.setState(WorkingProcedureCode.ERR);
                                        break;
                                    }
                                } else if (tableColVal.equals("detect18")) {
                                    Integer outOk18 = workpieceData.getOutOk18();
                                    if (outOk18 != null && outOk18.intValue() == WorkpieceOkNg.WORKPIEC_NG) {
                                        stationModel.setState(WorkingProcedureCode.ERR);
                                        break;
                                    }
                                } else if (tableColVal.equals("detect19")) {
                                    Integer outOk19 = workpieceData.getOutOk19();
                                    if (outOk19 != null && outOk19.intValue() == WorkpieceOkNg.WORKPIEC_NG) {
                                        stationModel.setState(WorkingProcedureCode.ERR);
                                        break;
                                    }
                                } else if (tableColVal.equals("detect20")) {
                                    Integer outOk20 = workpieceData.getOutOk20();
                                    if (outOk20 != null && outOk20.intValue() == WorkpieceOkNg.WORKPIEC_NG) {
                                        stationModel.setState(WorkingProcedureCode.ERR);
                                        break;
                                    }
                                } else if (tableColVal.equals("detect21")) {
                                    Integer outOk21 = workpieceData.getOutOk21();
                                    if (outOk21 != null && outOk21.intValue() == WorkpieceOkNg.WORKPIEC_NG) {
                                        stationModel.setState(WorkingProcedureCode.ERR);
                                        break;
                                    }
                                } else if (tableColVal.equals("detect22")) {
                                    Integer outOk22 = workpieceData.getOutOk22();
                                    if (outOk22 != null && outOk22.intValue() == WorkpieceOkNg.WORKPIEC_NG) {
                                        stationModel.setState(WorkingProcedureCode.ERR);
                                        break;
                                    }
                                } else if (tableColVal.equals("detect23")) {
                                    Integer outOk23 = workpieceData.getOutOk23();
                                    if (outOk23 != null && outOk23.intValue() == WorkpieceOkNg.WORKPIEC_NG) {
                                        stationModel.setState(WorkingProcedureCode.ERR);
                                        break;
                                    }
                                } else if (tableColVal.equals("detect24")) {
                                    Integer outOk24 = workpieceData.getOutOk24();
                                    if (outOk24 != null && outOk24.intValue() == WorkpieceOkNg.WORKPIEC_NG) {
                                        stationModel.setState(WorkingProcedureCode.ERR);
                                        break;
                                    }
                                } else if (tableColVal.equals("detect25")) {
                                    Integer outOk25 = workpieceData.getOutOk25();
                                    if (outOk25 != null && outOk25.intValue() == WorkpieceOkNg.WORKPIEC_NG) {
                                        stationModel.setState(WorkingProcedureCode.ERR);
                                        break;
                                    }
                                } else if (tableColVal.equals("detect26")) {
                                    Integer outOk26 = workpieceData.getOutOk26();
                                    if (outOk26 != null && outOk26.intValue() == WorkpieceOkNg.WORKPIEC_NG) {
                                        stationModel.setState(WorkingProcedureCode.ERR);
                                        break;
                                    }
                                } else if (tableColVal.equals("detect27")) {
                                    Integer outOk27 = workpieceData.getOutOk27();
                                    if (outOk27 != null && outOk27.intValue() == WorkpieceOkNg.WORKPIEC_NG) {
                                        stationModel.setState(WorkingProcedureCode.ERR);
                                        break;
                                    }
                                } else if (tableColVal.equals("detect28")) {
                                    Integer outOk28 = workpieceData.getOutOk28();
                                    if (outOk28 != null && outOk28.intValue() == WorkpieceOkNg.WORKPIEC_NG) {
                                        stationModel.setState(WorkingProcedureCode.ERR);
                                        break;
                                    }
                                } else {
                                    continue;
                                }
                            }
                        }
                    }
                }
            } else {
                continue;
            }

        }

    }


    /**
     * @param workingFlowRes ????????????
     * @param stationModels  ????????????
     * @return
     */
    private List<StationModelAll> getQrCodeStationCheck(List<WorkingFlowRes> workingFlowRes, List<StationModelAll> stationModels) {
        List<StationModelAll> modelAlls = new ArrayList<>();
        for (StationModelAll stationModel : stationModels) {
            StationModelAll modelAll = new StationModelAll();
            modelAll.setStationId(stationModel.getStationId());
            modelAll.setStationCode(stationModel.getStationCode());
            modelAll.setStationName(stationModel.getStationName());
            modelAll.setSortCode(stationModel.getSortCode());
            modelAll.setState(stationModel.getState());
            modelAll.setNgCode(stationModel.getNgCode());
            modelAll.setMergeCode(stationModel.getMergeCode());
            modelAll.setOutFlag(stationModel.getOutFlag());
            modelAlls.add(modelAll);
        }
        for (WorkingFlowRes workingFlowRe : workingFlowRes) {
            String stationIdJc = workingFlowRe.getStationId();
            String startTime = workingFlowRe.getStartTime();
            String completeTime = workingFlowRe.getCompleteTime();
            for (StationModelAll modelAll : modelAlls) {
                String stationIdGw = modelAll.getStationId();
                if (stationIdJc.equals(stationIdGw)) {
                    modelAll.setStartTime(startTime == null ? "" : startTime);
                    modelAll.setCompleteTime(completeTime == null ? "" : completeTime);
                    if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(completeTime)) {
                        long timeSt = dateUtil.stringDateToformatDateYmdHms(startTime).getTime();
                        long timeCm = dateUtil.stringDateToformatDateYmdHms(completeTime).getTime();
                        long devx = (timeCm - timeSt) / 1000;
                        modelAll.setTaktTime(getGapTime(Long.valueOf(devx).intValue()));
                    }
                    if (startTime != null && completeTime != null) {
                        if ("1".equals(modelAll.getNgCode())) {
                            modelAll.setState(WorkingProcedureCode.ERR);
                        } else {
                            modelAll.setState(WorkingProcedureCode.OUT);
                        }
                        break;
                    } else {
                        if ("1".equals(modelAll.getNgCode())) {
                            modelAll.setState(WorkingProcedureCode.ERR);
                        } else {
                            if (workingFlowRe.getCompleteTime() != null) {
                                modelAll.setState(WorkingProcedureCode.OUT);
                                break;
                            } else {
                                modelAll.setState(WorkingProcedureCode.IN);
                                break;
                            }
                        }

                    }
                }

            }
        }
        return modelAlls;
    }

    public static String getGapTime(int time) {
        int minutes = time / 60;
        int seconds = time % 60;
        String timeString = minutes + " ??? " + seconds + " ???";
        if (time < 0) {
            timeString = timeString + " ??????";
        }
        return timeString;
    }

    private Map<String, List<WorkingFlowRes>> getGroupQrCode(List<WorkingFlowRes> workingFlow) {
        Map<String, List<WorkingFlowRes>> map = new HashMap<>();
        for (WorkingFlowRes flowRes : workingFlow) {
            String qrCode = flowRes.getQrCode();
            List<WorkingFlowRes> workingFlowRes = map.get(qrCode);
            if (workingFlowRes == null) {
                workingFlowRes = new ArrayList<>();
                workingFlowRes.add(flowRes);
                map.put(qrCode, workingFlowRes);
            } else {
                workingFlowRes.add(flowRes);
            }
        }
        return map;
    }

    private Map<String, String> getStationColData(List<Map<String, Object>> modelTables) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < modelTables.size(); i++) {
            Map<String, Object> modelTable = modelTables.get(i);
            String column = "column" +modelTable.get("stationCode");
            modelTable.put("colData", column);
            map.put(modelTable.get("stationId").toString(), column);
        }
        return map;
    }

}
