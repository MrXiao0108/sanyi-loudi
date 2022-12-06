package com.dzics.business.config.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.dao.DzDataCollectionMapper;
import com.dzics.common.dao.DzEquipmentMapper;
import com.dzics.common.enums.EquiTypeEnum;
import com.dzics.common.model.entity.DzDataCollection;
import com.dzics.common.model.entity.DzEquipment;
import com.dzics.common.model.entity.DzLineShiftDay;
import com.dzics.common.model.response.equipmentstate.DzDataCollectionDo;
import com.dzics.common.service.DzLineShiftDayService;
import com.dzics.common.util.RedisKey;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * 系统任务
 *
 * @author ZhangChengJun
 * Date 2021/1/19.
 * @since
 */
@Component
@Slf4j
public class SysBusTask {
    @Autowired
    private DzLineShiftDayService dzLineShiftDayService;
    @Autowired
    public RedissonClient redissonClient;

    /**
     * 排班
     */
    public void arrange() {
        RLock lock = redissonClient.getLock(RedisKey.SYS_BUS_TASK_ARRANGE);
        try {
            lock.lock();
//            查询未排班的设备
//            当天班次排班
            LocalDate now = LocalDate.now();
            String substring = now.toString().substring(0, 7);
            int year = now.getYear();
            List<Long> eqId = dzLineShiftDayService.getNotPb(now);
            if (CollectionUtils.isNotEmpty(eqId)) {
                List<DzLineShiftDay> dzLineShiftDays = dzLineShiftDayService.getBc(eqId);
                dzLineShiftDays.stream().forEach(dzLineShiftDay -> {
                    dzLineShiftDay.setWorkData(now);
                    dzLineShiftDay.setWorkYear(year);
                    dzLineShiftDay.setWorkMouth(substring);
                });
                dzLineShiftDayService.saveBatch(dzLineShiftDays);
            }
//            当天班次加+ 1 排班
            LocalDate localDate = LocalDate.now().plusDays(1L);
            String substringAdd = localDate.toString().substring(0, 7);
            int yearAdd = localDate.getYear();
            List<Long> eqNext = dzLineShiftDayService.getNotPb(localDate);
            if (CollectionUtils.isNotEmpty(eqNext)) {
                List<DzLineShiftDay> dzLineShiftDays = dzLineShiftDayService.getBc(eqNext);
                dzLineShiftDays.stream().forEach(dzLineShiftDay -> {
                    dzLineShiftDay.setWorkData(localDate);
                    dzLineShiftDay.setWorkMouth(substringAdd);
                    dzLineShiftDay.setWorkYear(yearAdd);
                });
                dzLineShiftDayService.saveBatch(dzLineShiftDays);
            }
        } catch (Throwable e) {
            log.error("排班时发生错误：{}", e.getMessage(), e);
        } finally {
            lock.unlock();
        }

    }


    @Autowired
    private DzDataCollectionMapper dzDataCollectionDo;
    @Autowired
    private DzEquipmentMapper dzEquipmentMapper;

    /**
     * 本地测试数据使用
     * @Scheduled(fixedDelay = 30 * 1000, initialDelay = 10 * 1000)
     */
    public void test() {
        log.info("更新设备状态开始。。。。。。。。。。");
        QueryWrapper<DzEquipment> wp = new QueryWrapper<>();
        wp.eq("order_no", "DZ-1887");
        List<DzEquipment> equipments = dzEquipmentMapper.selectList(wp);
        for (DzEquipment equipment : equipments) {
            QueryWrapper<DzDataCollection> wpx = new QueryWrapper<>();
            wpx.eq("device_id", equipment.getId());
            DzDataCollection dzDataCollection = dzDataCollectionDo.selectOne(wpx);
            if (EquiTypeEnum.JC.getCode() == equipment.getEquipmentType()) {
//                机床
                String status = dzDataCollection.getB562();
                String alarm = dzDataCollection.getB569();
                String connState = dzDataCollection.getB561();
                //  1作业 2：待机 3：故障 4：关机
                int workState = 4;
                if (!StringUtils.isEmpty(status)) {
                    if ("1".equals(connState)) {
//                            连机
                        if ("1".equals(alarm)) {
//                                报警，设置故障
                            workState = 3;
                        } else {
                            if ("3".equals(status)) {
                                workState = 1;
                            }
                            if ("2".equals(status)) {
                                workState = 2;
                            }
                        }
                    }
                    int a = ((int) Math.random() * 10);
                    if (a <= 3) {
                        if (workState == 1) {
                            dzDataCollection.setB562("2");
                        }
                        if (workState == 2) {
                            dzDataCollection.setB562("3");
                        }
                        if (workState == 3) {
                            dzDataCollection.setB569("0");
                        }
                    } else if (a >= 7) {
                        dzDataCollection.setB569("1");
                    } else {
                        dzDataCollection.setB562("");
                    }
                } else {
                    dzDataCollection.setB562("2");
                }

            } else if (EquiTypeEnum.JQR.getCode() == equipment.getEquipmentType()) {
                String status = dzDataCollection.getA563();
                String alarm = dzDataCollection.getA566();
                String connState = dzDataCollection.getA561();
                String standby = dzDataCollection.getA567();
                //  1作业 2：待机 3：故障 4：关机
                int workState = 4;
                if (!StringUtils.isEmpty(status)) {
                    if ("1".equals(connState)) {
//                            连机
                        if ("1".equals(alarm)) {
//                                报警，设置故障
                            workState = 3;
                        } else {
                            if ("1".equals(standby)) {
                                workState = 2;
                            } else {
                                if ("1".equals(status)) {
                                    workState = 1;
                                } else {
                                    workState = 2;
                                }
                            }

                        }
                    }
                    int a = ((int) Math.random() * 10);
                    if (a <= 3) {
                        if (1 == workState) {
                            dzDataCollection.setA563("2");
                        }
                        if (2 == workState) {
                            dzDataCollection.setA567("2");
                            dzDataCollection.setA563("1");
                        }
                        if (3 == workState) {
                            dzDataCollection.setA566("0");
                        }
                    } else if (a >= 7) {
                        dzDataCollection.setA566("1");
                    } else {
                        dzDataCollection.setA563("");
                    }
                } else {
                    dzDataCollection.setA563("1");
                }

//                机器人
            } else if (EquiTypeEnum.MEN.getCode() == equipment.getEquipmentType()) {
//                门
                String status = dzDataCollection.getS561();
                if (StringUtils.isEmpty(status)) {
                    status = "0";
                } else {
                    if ("0".equals(status)) {
                        status = "1";
                    } else {
                        status = "0";
                    }
                }
                dzDataCollection.setS561(status);
            }
            dzDataCollection.setUpdateTime(new Date());
            dzDataCollectionDo.updateById(dzDataCollection);
        }
        log.info("更新设备状态完成。。。。。。。。。。");
    }
}
