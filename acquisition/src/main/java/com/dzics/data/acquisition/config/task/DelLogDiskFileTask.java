package com.dzics.data.acquisition.config.task;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 清除日志操作
 *
 * @author ZhangChengJun
 * Date 2021/11/24.
 * @since
 */
@Service
@Slf4j
public class DelLogDiskFileTask implements SimpleJob {


//    @Value("${del.diskfile.log.day}")
    private static Integer delDiskFilesLog = 10;

    /**
     * 清理磁盘日志文件
     */
    public static void delDiskFile() {
        List<String> files = new ArrayList<>();
        List<String>dirs = new ArrayList<>();
        String path = "E:\\logs\\";
        File file = new File(path);
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
                delFile(files,delDiskFilesLog);
            }
            if (!dirs.isEmpty()){
                analysisFile(dirs);
            }
        }else{
            log.warn("指定清除数据库备份文件路径：{}不存在,请检查核对路径",path);
        }
    }

    //文件判断是否需要删除
    private static void delFile(List<String>list,Integer days){
        //定义30天前的时间戳
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

    //文件夹对象继续解刨
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
            delFile(filesList,delDiskFilesLog);
        }
        if (!newDirList.isEmpty()){
            analysisFile(newDirList);
        }
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        log.warn("开始清除日志文件..........");
        delDiskFile();
        log.warn("清除日志文件结束..........");
    }
}
