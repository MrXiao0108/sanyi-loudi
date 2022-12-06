package com.dzics.business.config.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author xnb
 * @date 2022/10/10 0010 16:04
 */
@Component
@Slf4j
public class DelCpFileTask {

    private static final Integer DEL_DAYS = 15;

    @Value("${dzics.mysql.data.backUps.path}")
    private String url;

    /**
     * 定时清除电脑 备份数据库的ps文件
     * 每日中午12点触发
     * */
    @Scheduled(cron = "0 0 12 * * ?")
    public void delCpFile(){
        List<String> files = new ArrayList<>();
        List<String>dirs = new ArrayList<>();
        File file = new File(url);
        if(file.exists()==true){
            File [] array = file.listFiles();
            if(array!=null){
                for (int i = 0; i < array.length; i++) {
                    if (array[i].isFile()){
                        files.add(array[i].getPath());
                    }
                    if (array[i].isDirectory()){
                        dirs.add(array[i].getPath());
                    }
                }
            }
            if (!files.isEmpty()){
                delFile(files,DEL_DAYS);
            }
            if (!dirs.isEmpty()){
                analysisFile(dirs);
            }
        }else{
            log.warn("指定清除数据库备份文件路径：{}不存在,请检查核对路径",url);
        }
    }


    /**
     * 判断是否是文件，true删除
     * */
    private static void delFile(List<String> list, Integer days){
        //定义时间戳
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE,- days);
        //指定历史删除时间  （小于删，大于留）
        long queryTime = calendar.getTime().getTime();
        for(int i=0;i<list.size();i++){
            File file = new File(list.get(i));
            long upTime = new Date(file.lastModified()).getTime();
            if(upTime<queryTime){
                BasicFileAttributes attr = null;
                try {
                    attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                }catch(IOException e){
                    e.printStackTrace();
                }
                Instant instant = attr.creationTime().toInstant();
                file.delete();
                log.debug("删除日志文件{},创建时间为{}",file.getName(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault()).format(instant));
            }
        }
    }


    /**
     * 文件递归
     * */
    private static void analysisFile(List<String>dirList){
        List<String>dirsList = dirList;
        List<String>newDirList = new ArrayList<>();
        List<String>filesList = new ArrayList<>();
        for (int i=0;i<dirsList.size();i++)
        {
            File file = new File(dirsList.get(i));
            File [] files = file.listFiles();
            if (files!=null){
                for (int j=0;j<files.length;j++){
                    if (files[j].isFile()){
                        filesList.add(files[j].getPath());
                    }
                    if (files[j].isDirectory()){
                        newDirList.add(files[j].getPath());
                    }
                }
            }
        }
        if(!filesList.isEmpty()){
            delFile(filesList,DEL_DAYS);
        }
        if (!newDirList.isEmpty()){
            analysisFile(newDirList);
        }
    }


}
