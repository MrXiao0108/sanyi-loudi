package com.dzics.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.business.model.response.proddetection.HeaderClom;
import com.dzics.business.model.response.proddetection.TcpLogProDetection;
import com.dzics.business.model.vo.CommuLogPrm;
import com.dzics.business.service.BusCommunicationLogService;
import com.dzics.common.dao.SysCommunicationLogMapper;
import com.dzics.common.enums.EquiTypeEnum;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.entity.SysCommunicationLog;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.SysCommunicationLogService;
import com.dzics.common.util.PageLimit;
import com.dzics.common.util.UnderlineTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ZhangChengJun
 * Date 2021/3/8.
 * @since
 */
@Service
@Slf4j
public class BusCommunicationLogServiceImpl extends ServiceImpl<SysCommunicationLogMapper, SysCommunicationLog> implements BusCommunicationLogService {
    @Autowired
    private SysCommunicationLogService sysCommunicationLogService;


    @Override
    public Result communicationLog(PageLimit pageLimit, CommuLogPrm commuLogPrm) {
        PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        QueryWrapper<SysCommunicationLog> wp = new QueryWrapper<>();
        if (!StringUtils.isEmpty(commuLogPrm.getOrdercode())) {
            wp.eq("OrderCode", commuLogPrm.getOrdercode());
        }
        if (!StringUtils.isEmpty(commuLogPrm.getDevicecode())) {
            wp.eq("DeviceCode", commuLogPrm.getDevicecode());
        }
        if (!StringUtils.isEmpty(commuLogPrm.getDevicetype())) {
            wp.eq("DeviceType", commuLogPrm.getDevicetype());
        }
        if (!StringUtils.isEmpty(commuLogPrm.getLineno())) {
            wp.eq("LineNo", commuLogPrm.getLineno());
        }
        if (!StringUtils.isEmpty(commuLogPrm.getQueuename())) {
            wp.eq("QueueName", commuLogPrm.getQueuename());
        }
        if (!StringUtils.isEmpty(commuLogPrm.getStartTime())) {
            wp.ge("Timestamp", commuLogPrm.getStartTime());
        }
        if (!StringUtils.isEmpty(commuLogPrm.getEndTime())) {
            long time = commuLogPrm.getEndTime().getTime() + (long) 24 * (3600 * 1000);
            wp.lt("`Timestamp`", new Date(time));
        }
        wp.select("communication_key");
        if (!StringUtils.isEmpty(pageLimit.getType())) {
            if ("DESC".equals(pageLimit.getType())) {
                wp.orderByDesc(UnderlineTool.humpToLine(pageLimit.getField()));
            } else if ("ASC".equals(pageLimit.getType())) {
                wp.orderByAsc(UnderlineTool.humpToLine(pageLimit.getField()));
            } else {
                wp.orderByDesc("`Timestamp`");
            }
        } else {
            wp.orderByDesc("`Timestamp`");
        }
        List<SysCommunicationLog> list = sysCommunicationLogService.list(wp);
        PageInfo<SysCommunicationLog> sysCommunicationLogPageInfo = new PageInfo<>(list);
        if (sysCommunicationLogPageInfo.getList().isEmpty()) {
            return new Result(CustomExceptionType.OK, sysCommunicationLogPageInfo.getList(), sysCommunicationLogPageInfo.getTotal());
        }
        //       再次查询
        List<String> collect = sysCommunicationLogPageInfo.getList().stream().map(dd -> dd.getCommunicationKey()).collect(Collectors.toList());
        QueryWrapper<SysCommunicationLog> wpT = new QueryWrapper<>();
        wpT.in("communication_key", collect);
        if (!StringUtils.isEmpty(pageLimit.getType())) {
            if ("DESC".equals(pageLimit.getType())) {
                wpT.orderByDesc(UnderlineTool.humpToLine(pageLimit.getField()));
            } else if ("ASC".equals(pageLimit.getType())) {
                wpT.orderByAsc(UnderlineTool.humpToLine(pageLimit.getField()));
            } else {
                wpT.orderByDesc("`Timestamp`");
            }
        } else {
            wpT.orderByDesc("`Timestamp`");
        }
        List<SysCommunicationLog> communicationLogs = sysCommunicationLogService.list(wpT);
        return new Result(CustomExceptionType.OK, communicationLogs, sysCommunicationLogPageInfo.getTotal());
    }

    @Override
    public Result communicationLogTcp(PageLimit pageLimit, CommuLogPrm commuLogPrm) {
        PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        QueryWrapper<SysCommunicationLog> wp = new QueryWrapper<>();
        wp.select("communication_key");
        if (!StringUtils.isEmpty(commuLogPrm.getQueuename())) {
            wp.eq("QueueName", commuLogPrm.getQueuename());
        }
        if (!StringUtils.isEmpty(commuLogPrm.getOrdercode())) {
            wp.eq("OrderCode", commuLogPrm.getOrdercode());
        }
        if (!StringUtils.isEmpty(commuLogPrm.getLineno())) {
            wp.eq("LineNo", commuLogPrm.getLineno());
        }
        if (!StringUtils.isEmpty(commuLogPrm.getDevicetype())) {
            wp.eq("DeviceType", commuLogPrm.getDevicetype());
        }
        if (!StringUtils.isEmpty(commuLogPrm.getDevicecode())) {
            wp.eq("DeviceCode", commuLogPrm.getDevicecode());
        }
        if (!StringUtils.isEmpty(commuLogPrm.getStartTime())) {
            wp.ge("Timestamp", commuLogPrm.getStartTime());
        }
        if (!StringUtils.isEmpty(commuLogPrm.getEndTime())) {
            long time = commuLogPrm.getEndTime().getTime() + (long) 24 * (3600 * 1000);
            wp.lt("`Timestamp`", new Date(time));
        }
        if (!StringUtils.isEmpty(pageLimit.getType())) {
            if ("DESC".equals(pageLimit.getType())) {
                wp.orderByDesc(pageLimit.getField());
            } else if ("ASC".equals(pageLimit.getType())) {
                wp.orderByAsc(pageLimit.getField());
            }
        }
        List<SysCommunicationLog> list = sysCommunicationLogService.list(wp);
        PageInfo<SysCommunicationLog> logPageInfo = new PageInfo<>(list);
        if (logPageInfo.getList().isEmpty()) {
            return new Result(CustomExceptionType.OK, logPageInfo.getList(), logPageInfo.getTotal());
        }
        List<String> collect = logPageInfo.getList().stream().map(dd -> dd.getCommunicationKey()).collect(Collectors.toList());
        QueryWrapper<SysCommunicationLog> wrapper = new QueryWrapper<>();
        wrapper.in("communication_key", collect);
        if (!StringUtils.isEmpty(pageLimit.getType())) {
            if ("DESC".equals(pageLimit.getType())) {
                wrapper.orderByDesc(pageLimit.getField());
            } else if ("ASC".equals(pageLimit.getType())) {
                wrapper.orderByAsc(pageLimit.getField());
            }
        }
        List<Map<String, Object>> maps = sysCommunicationLogService.listMaps(wrapper);
        List<Map<String, Object>> communicationLogs = maps;
        TcpLogProDetection proDetection = new TcpLogProDetection();
        List<HeaderClom> tableColumn = proDetection.getTableColumn();
//        定义动态获取的所有需要添加的表头
        Map<String, String> tabClom = new HashMap<>();
        for (Map<String, Object> communicationLog : communicationLogs) {
            for (Map.Entry<String, Object> stringObjectEntry : communicationLog.entrySet()) {
                String key = stringObjectEntry.getKey();
                Object obj = stringObjectEntry.getValue();
                if (key.equals("Message")) {
                    String message = obj.toString();
                    List<String> tcpCmd = Arrays.asList(message.split("#"));
                    for (String tcp : tcpCmd) {
                        String[] tcpAndValue = tcp.split("\\|");
                        tabClom.put(tcpAndValue[0], tcpAndValue[0]);
                    }
                }
            }

        }
//        添加指令需要添加的表头
        for (Map.Entry<String, String> stringStringEntry : tabClom.entrySet()) {
            String key = stringStringEntry.getKey();
            HeaderClom clom = new HeaderClom();
            clom.setColData(key);
            clom.setColName(key);
            tableColumn.add(clom);
        }
        List<Map<String, Object>> tableData = new ArrayList<>();
        for (int i = 0; i < maps.size(); i++) {
//            元数据
            Map<String, Object> map = new HashMap<>();
            for (Map.Entry<String, String> stringStringEntry : tabClom.entrySet()) {
                map.put(stringStringEntry.getKey(), "");
            }
            Map<String, Object> tcpData = maps.get(i);
            for (Map.Entry<String, Object> stringObjectEntry : tcpData.entrySet()) {
                String key = stringObjectEntry.getKey();
                Object value = stringObjectEntry.getValue();
                if (!key.equals("Message")) {
                    if (!key.equals("communication_key")) {
                        if (key.equals("DeviceType")) {
                            if (String.valueOf(EquiTypeEnum.JC.getCode()).equals(value)) {
                                map.put(key, EquiTypeEnum.JC.getDesc());
                            } else if (String.valueOf(EquiTypeEnum.JCSB.getCode()).equals(value)) {
                                map.put(key, EquiTypeEnum.JCSB.getDesc());
                            } else if (String.valueOf(EquiTypeEnum.JQR.getCode()).equals(value)) {
                                map.put(key, EquiTypeEnum.JQR.getDesc());
                            } else if (String.valueOf(EquiTypeEnum.XJ.getCode()).equals(value)) {
                                map.put(key, EquiTypeEnum.XJ.getDesc());
                            } else if (String.valueOf(EquiTypeEnum.EQCODE.getCode()).equals(value)) {
                                map.put(key, EquiTypeEnum.EQCODE.getDesc());
                            } else if (String.valueOf(EquiTypeEnum.AVG.getCode()).equals(value)) {
                                map.put(key, EquiTypeEnum.AVG.getDesc());
                            } else if (String.valueOf(EquiTypeEnum.CHJ.getCode()).equals(value)) {
                                map.put(key, EquiTypeEnum.CHJ.getDesc());
                            }else if (String.valueOf(EquiTypeEnum.JZJ.getCode()).equals(value)) {
                                map.put(key, EquiTypeEnum.JZJ.getDesc());
                            } else {
                                map.put(key, value);
                            }
                        } else {
                            map.put(key, value);
                        }
                    }
                } else {
                    String tcps = value.toString();
                    List<String> tcpCmd = Arrays.asList(tcps.split("#"));
                    for (String tcp : tcpCmd) {
                        String[] tcpAndValue = tcp.split("\\|");
                        if (tcpAndValue.length == 2) {
                            map.put(tcpAndValue[0], tcpAndValue[1]);
                        } else {
                            map.put(tcpAndValue[0], "");
                        }
                    }

                }

            }
            tableData.add(map);
        }
        proDetection.setTableColumn(tableColumn);
        proDetection.setTableData(tableData);
        return new Result(CustomExceptionType.OK, proDetection, logPageInfo.getTotal());
    }


    @Override
    public void delDiskFile(Integer days){
        //定义十天前的时间戳
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE,- days);
        Date time = calendar.getTime();
        String ctime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(time);
        //获取定义指定文件夹下所有文件
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String path = "E:\\logs\\";
        File file = new File(path);
        File[] array = file.listFiles();
        if(array!=null){
            //循环判断文件夹下所有的文件是否符合删除条件
            for (int i = 0; i < array.length; i++) {
                if (array[i].isFile()) {
                    String filePath = array[i].getPath();
                    String strTime = null;
                    try {
                        Process p = Runtime.getRuntime().exec("cmd /C dir "
                                + filePath
                                + "/tc");
                        InputStream is = p.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(is));
                        String line;
                        while ((line = br.readLine()) != null) {
                            if (line.endsWith(".txt")) {
                                strTime = line.substring(0, 17);
                                break;
                            }
                        }
                        String year = strTime.substring(0, 4);
                        String mouth = strTime.substring(5, 7);
                        String day = strTime.substring(8, 10);
                        String hours = strTime.substring(12, 14);
                        String mm = strTime.substring(15, 17);
                        String createDate = year + "-" + mouth + "-" + day + " " + hours + ":" + mm;
                        //判断每个文件的创建日期是否小于等于删除日期范围内，符合则del
                        Date tjTime = dateFormat.parse(ctime);
                        Date createTime = dateFormat.parse(createDate);
                        if (createTime.getTime() < tjTime.getTime()) {
                            array[i].delete();
                        }
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

}
