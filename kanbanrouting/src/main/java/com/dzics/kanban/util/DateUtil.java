package com.dzics.kanban.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class DateUtil {

    /**
     * 底层发送来的数据中，时间格式化方法
     *
     * @param dataStr
     * @return
     */
    public static Date formatmmss(String dataStr) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            return dateFormat.parse(dataStr);
        } catch (ParseException e) {
            log.error("日期格式化错误直接返回当前时间:{}", e);
            return new Date();
        }
    }

    public static Date formatymd(String dataStr) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.parse(dataStr);
        } catch (ParseException e) {
            log.error("日期格式化错误直接返回当前时间:{}", e);
            return new Date();
        }
    }





    public static LocalDate dataToLocalDate(Date nowDate) {
        Instant instant = nowDate.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        LocalDate localDate = localDateTime.toLocalDate();
        return localDate;
    }

    public static LocalTime dataToLocalTime(Date nowDate) {
        Instant instant = nowDate.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        LocalTime localTime = localDateTime.toLocalTime();
        return localTime;
    }

    /**
     * 获取当天日期（yyyy-MM-dd）
     *
     * @return 当天日期
     */
    public String getDate() {
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(new Date());
    }

    /**
     * 获取当天日期（yyyy-MM-dd HH:mm:ss）
     *
     * @return 当天日期
     */
    public String getDateTime() {
        /**
         * 日期时间类型格式
         */
         SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }

    public String getDateTime1() {
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        return dateFormat.format(new Date());
    }


    public String getDate(Date date) {
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }


    public String getDateTime(Date date) {
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    public String getDateTime1(Date date) {
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        return dateFormat.format(date);
    }

    public int minus(String time1, String time2) {
        try {
            SimpleDateFormat dateFormat =
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date datetime1 = dateFormat.parse(time1);
            Date datetime2 = dateFormat.parse(time2);

            Long minut = (datetime1.getTime() - datetime2.getTime()) / (1000 * 60);

            return Integer.valueOf(String.valueOf(minut));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public String dayjia1(String stareDate) throws ParseException {
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd");

        Date sDate = dateFormat.parse(stareDate);
        Calendar c = Calendar.getInstance();
        c.setTime(sDate);
        c.add(Calendar.DAY_OF_MONTH, 1);
        sDate = c.getTime();
        stareDate = dateFormat.format(sDate);
        return stareDate;
    }


    public Date dayjiaDay(Date stareDate, int day) {
        Calendar c = Calendar.getInstance();
        c.setTime(stareDate);
        c.add(Calendar.DAY_OF_MONTH, day);
        return c.getTime();
    }

    //获取本月第一天
    public String firstDay() {
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);//1:本月第一天
        String day1 = dateFormat.format(c.getTime());
        return day1;
    }

    //获取本月最后一天
    public String lastDay() {
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd");
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        String day2 = dateFormat.format(ca.getTime());
        return day2;
    }

    //获取当前所有月份
    public List<String> getAllMonth() {
        List<String> month = new ArrayList<>();
        Calendar date = Calendar.getInstance();
        String year = String.valueOf(date.get(Calendar.YEAR));
        for (int i = 1; i < 13; i++) {
            if (i < 10) {
                month.add(year + "-0" + i);
            } else {
                month.add(year + "-" + i);
            }

        }
        return month;
    }
}
