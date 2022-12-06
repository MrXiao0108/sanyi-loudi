package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.DzEquipmentAlarmRecordMapper;
import com.dzics.common.dao.DzEquipmentMapper;
import com.dzics.common.model.custom.SocketUtilization;
import com.dzics.common.model.entity.DzEquipment;
import com.dzics.common.model.entity.DzEquipmentTimeAnalysis;
import com.dzics.common.model.response.JCEquiment;
import com.dzics.common.model.response.equipmentstate.DzDataCollectionDo;
import com.dzics.common.service.DzEquipmentRunTimeService;
import com.dzics.common.service.DzEquipmentService;
import com.dzics.common.service.DzEquipmentTimeAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 设备表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
@Service
@Slf4j
public class DzEquipmentServiceImpl extends ServiceImpl<DzEquipmentMapper, DzEquipment> implements DzEquipmentService {
    @Autowired
    private DzEquipmentRunTimeService runTimeService;
    @Autowired
    private DzEquipmentMapper dzEquipmentMapper;
    @Autowired
    private DzEquipmentAlarmRecordMapper dzEquipmentAlarmRecordMapper;
    @Autowired
    private DzEquipmentTimeAnalysisService dzEquipmentTimeAnalysisService;

    @Override
    public int updateByLineNoAndEqNo(DzEquipment dzEquipment) {
        if (StringUtils.isEmpty(dzEquipment.getLineNo()) ||
                StringUtils.isEmpty(dzEquipment.getEquipmentNo()) ||
                dzEquipment.getEquipmentType() == null ||
                StringUtils.isEmpty(dzEquipment.getOrderNo())) {
            log.warn("更新设备状态参数错误：dzeq:{}", dzEquipment);
        }
        QueryWrapper<DzEquipment> wp = new QueryWrapper<>();
        wp.eq("line_no", dzEquipment.getLineNo());
        wp.eq("equipment_no", dzEquipment.getEquipmentNo());
        wp.eq("equipment_type", dzEquipment.getEquipmentType());
        wp.eq("order_no", dzEquipment.getOrderNo());
        dzEquipment.setIsShow(null);
        dzEquipment.setEquipmentName(null);
        dzEquipment.setNickName(null);
        int update = dzEquipmentMapper.update(dzEquipment, wp);
        return update;
    }

    @Override
    public List<JCEquiment> listjcjqr() {
        LocalDate localDate = LocalDate.now();
        return dzEquipmentMapper.listjcjqr(localDate);
    }

    @Override
    public List<DzEquipment> getRunStaTimeIsNotNull() {
        QueryWrapper<DzEquipment> wp = new QueryWrapper<>();
        wp.select("equipment_no", "equipment_type", "order_no", "line_no");
        wp.isNotNull("start_run_time");
        return dzEquipmentMapper.selectList(wp);
    }

    @Override
    public DzEquipment listjcjqrdeviceid(Long deviceId, LocalDate localDate) {
        return dzEquipmentMapper.listjcjqrdeviceid(deviceId, localDate);
    }

    @Override
    public List<DzDataCollectionDo> getMachiningMessageStatus(String lineNo, String orderNum, LocalDate now) {
        List<DzDataCollectionDo> dataList = dzEquipmentMapper.getMachiningMessageStatus(lineNo, orderNum, now);
        return dataList;
    }

    @Override
    public List<DzEquipment> getDeviceOrderNoLineNo(String orderNo, String lineNo) {
        QueryWrapper<DzEquipment> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", orderNo);
        wrapper.eq("line_no", lineNo);
        return list(wrapper);
    }

    @Override
    public SocketUtilization getSocketUtilization(DzEquipment dzEquipment) {
//        String equipmentNo = dzEquipment.getEquipmentNo();
//        Integer equipmentType = dzEquipment.getEquipmentType();
//        String orderNo = dzEquipment.getOrderNo();
//        String lineNo = dzEquipment.getLineNo();
//        Date startRunTime = dzEquipment.getStartRunTime();
        SocketUtilization socketUtilization = new SocketUtilization();
//        socketUtilization.setEquimentId(dzEquipment.getId().toString());
//        try {
//            LocalDate nowDay = LocalDate.now();
//            ZonedDateTime zonedDateTime = nowDay.atStartOfDay(ZoneId.systemDefault());
//            long fromDate = Date.from(zonedDateTime.toInstant()).getTime();
//            long dateNow = System.currentTimeMillis();
////       当日已过时间 秒
//            BigDecimal until = new BigDecimal((dateNow - fromDate)).divide(new BigDecimal(1000), 0, BigDecimal.ROUND_HALF_UP);
//            if (until == null || until.compareTo(new BigDecimal(0)) == 0) {
//                return null;
//            }
//            if (startRunTime == null) {
//                socketUtilization.setHistoryOk(new BigDecimal("0"));
//                socketUtilization.setHistoryNg(new BigDecimal("0"));
//                socketUtilization.setDayOk(new BigDecimal("0"));
//                socketUtilization.setDayNg(new BigDecimal("0"));
//                socketUtilization.setOk(0);
//                socketUtilization.setNg(0);
//            } else {
//                //            当日告警时长
//                Long timeStop = dzEquipmentAlarmRecordMapper.getTimeDurationNowDay(equipmentNo, equipmentType, nowDay);
////            未恢复的告警计算截止到当前时间
//                if (timeStop == null) {
//                    timeStop = 0L;
//                }
////            增加可能存在为恢复的停机记录
//                Long timeStopResTime = dzEquipmentAlarmRecordMapper.getTimeDurationNowDayResetTimeIsNull(equipmentNo, equipmentType, nowDay);
//                if (timeStopResTime != null) {
//                    timeStop = timeStop + timeStopResTime;
//                }
////            当日告警时长毫秒 转化为秒
//                BigDecimal hourTime = new BigDecimal(timeStop).divide(new BigDecimal(1000), 0, BigDecimal.ROUND_HALF_UP);
////          当日运行时长秒
//                BigDecimal runTime = runTimeService.getDayRunTime(orderNo, lineNo, equipmentNo, equipmentType, nowDay);
//
////            当日稼动率   当日运行时长/当日总时长
////            当日故障率   当日告警时长/总时长
//                BigDecimal dayOk = runTime.divide(until, 4, BigDecimal.ROUND_HALF_UP);
//                BigDecimal dayNg = hourTime.divide(until, 4, BigDecimal.ROUND_HALF_UP);
////            历史总告警时长
//                Long historyTimeStop = dzEquipmentAlarmRecordMapper.getTimeDurationHistory(equipmentNo, equipmentType);
//                Long historyTimeStopAll = dzEquipmentAlarmRecordMapper.getTimeDurationHistoryResetTimeIsNull(equipmentNo, equipmentType);
//                if (historyTimeStop == null) {
//                    historyTimeStop = 0L;
//                }
//                if (historyTimeStopAll != null) {
//                    historyTimeStop = historyTimeStop + historyTimeStopAll;
//                }
////            开始运行时间戳
//                long time = startRunTime.getTime();
////            历史总停机时长秒
//                BigDecimal hisSiop = new BigDecimal(historyTimeStop).divide(new BigDecimal(1000), 0, BigDecimal.ROUND_HALF_UP);
////            历史总时长时间戳 秒
//                BigDecimal histaryAll = new BigDecimal(dateNow - time).divide(new BigDecimal(1000), 0, BigDecimal.ROUND_HALF_UP);
////            历史运行时长时间 秒
//                Long runTimeAll = runTimeService.getRunTimeAll(equipmentNo, equipmentType);
//                if (runTimeAll == null) {
//                    runTimeAll = 0L;
//                }
//                Long runTimeAllIsRestNull = runTimeService.getRunTimeIsRestNnull(equipmentNo, equipmentType);
//                if (runTimeAllIsRestNull != null) {
//                    runTimeAll = runTimeAll + runTimeAllIsRestNull;
//                }
//                BigDecimal histRun = new BigDecimal(runTimeAll).divide(new BigDecimal(1000), 0, BigDecimal.ROUND_HALF_UP);
////            历史稼动时间   历史运行时长/总历史时长
////            历史故障率   历史告警时长/历史总时长
//                BigDecimal historyOk = histRun.divide(histaryAll, 4, BigDecimal.ROUND_HALF_UP);
//                BigDecimal historyNg = hisSiop.divide(histaryAll, 4, BigDecimal.ROUND_HALF_UP);
//
//
////            历史稼动率
//                socketUtilization.setHistoryOk(historyOk.multiply(new BigDecimal(100)));
////            历史故障率
//                socketUtilization.setHistoryNg(historyNg.multiply(new BigDecimal(100)));
////            当日稼动率
//                socketUtilization.setDayOk(dayOk.multiply(new BigDecimal(100)));
////            当日故障率
//                socketUtilization.setDayNg(dayNg.multiply(new BigDecimal(100)));
//                socketUtilization.setOk(runTime.intValue());
//                socketUtilization.setNg(hourTime.intValue());
//            }
//        } catch (Throwable e) {
//            log.error("获取设备稼动率运行时间相关结果错误:", e);
//            return null;
//        }
        return socketUtilization;
    }

    @Override
    public List<SocketUtilization> getSocketUtilizationList(String orderNo, String lineNo) {
        List<DzEquipment> dzEquipments = getDeviceOrderNoLineNo(orderNo, lineNo);
        List<SocketUtilization> socketUtilizations = new ArrayList<>();
        LocalDate localDate=LocalDate.now();
        LocalDate startDate=localDate.plusDays(-7);
        for (DzEquipment equipment:dzEquipments) {
            QueryWrapper<DzEquipmentTimeAnalysis>wrapper=new QueryWrapper<>();
            wrapper.eq("device_id",equipment.getId());
            wrapper.gt("stop_data",startDate);
            List<DzEquipmentTimeAnalysis> list = dzEquipmentTimeAnalysisService.list(wrapper);
            SocketUtilization socketUtilization=new SocketUtilization();
            //当日稼动时间
            Long ok=0L;
            //当日故障时间
             Long ng=0L;
            //历史稼动时间
            Double okTime=0D;
            //历史故障时间
            Double ngTime=0D;
            //历史总时间
            Double sumTime=0D;
            //当日总时间
            Double nowTime=0D;
            for (DzEquipmentTimeAnalysis dzEquipmentTimeAnalysis:list) {
                Long duration = dzEquipmentTimeAnalysis.getDuration();
                if(dzEquipmentTimeAnalysis.getResetTime()==null&&duration.longValue()==0L){
                    long time=new Date().getTime()- dzEquipmentTimeAnalysis.getStopTime().getTime();
                    if(time>0){
                        duration=time;
                    }
                }
                //1：作业
                if(dzEquipmentTimeAnalysis.getWorkState().intValue()==1){
                    okTime+=duration;
                    //如果运行日期是当日 记录到当日运行时间
                    if(localDate.equals(dzEquipmentTimeAnalysis.getStopData())){
                        ok+=duration;
                    }
                }
                //3：故障
                if(dzEquipmentTimeAnalysis.getWorkState().intValue()==3){
                    ngTime+=duration;
                    if(localDate.equals(dzEquipmentTimeAnalysis.getStopData())){
                        ng+=duration;
                    }
                }
                if(localDate.equals(dzEquipmentTimeAnalysis.getStopData())){
                    nowTime+=duration;
                }
                sumTime+=duration;
            }
            socketUtilization.setEquimentId(equipment.getId().toString());
            socketUtilization.setEquimentNo(equipment.getEquipmentNo());
            if(sumTime>0){
                socketUtilization.setHistoryOk(new BigDecimal(okTime / sumTime * 100).setScale(2, BigDecimal.ROUND_HALF_UP));//历史稼动率
                socketUtilization.setHistoryNg(new BigDecimal(ngTime / sumTime * 100).setScale(2, BigDecimal.ROUND_HALF_UP));//历史稼动率
            }else{
                socketUtilization.setHistoryOk(new BigDecimal(0));
                socketUtilization.setHistoryNg(new BigDecimal(0));
            }
            if(nowTime>0){
                socketUtilization.setDayOk(new BigDecimal(ok/nowTime*100).setScale(2, BigDecimal.ROUND_HALF_UP));//当日稼动率
                socketUtilization.setDayNg(new BigDecimal(ng/nowTime*100).setScale(2, BigDecimal.ROUND_HALF_UP));//当日故障率
            }else{
                socketUtilization.setDayOk(new BigDecimal(0));
                socketUtilization.setDayNg(new BigDecimal(0));
            }
            socketUtilization.setOk(ok);//稼动时间
            socketUtilization.setNg(ng);//故障时间
            socketUtilizations.add(socketUtilization);
        }

        return socketUtilizations;
    }

    @Override
    public int updateByLineNoAndEqNoDownTime(DzEquipment dzEquipment) {
        if (StringUtils.isEmpty(dzEquipment.getLineNo()) ||
                StringUtils.isEmpty(dzEquipment.getEquipmentNo()) ||
                dzEquipment.getEquipmentType() == null ||
                StringUtils.isEmpty(dzEquipment.getOrderNo())) {
            log.warn("更新设备状态参数错误：dzeq:{}", dzEquipment);
        }
        QueryWrapper<DzEquipment> wp = new QueryWrapper<>();
        wp.eq("line_no", dzEquipment.getLineNo());
        wp.eq("equipment_no", dzEquipment.getEquipmentNo());
        wp.eq("equipment_type", dzEquipment.getEquipmentType());
        wp.eq("order_no", dzEquipment.getOrderNo());
        dzEquipment.setIsShow(null);
        int update = dzEquipmentMapper.update(dzEquipment, wp);
        return update;
    }




}
