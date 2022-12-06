package com.dzics.queueforwarding.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 删除日志
 *
 * @author ZhangChengJun
 * Date 2021/10/13.
 * @since
 */
@Component
@Slf4j
public class DelLogConfig {

    /**
     * 清理E磁盘日志文件
     */
    @Scheduled(fixedDelay = 3600000, initialDelay = 20000)
    public void delDiskFileE(){
        List<String> files = new ArrayList<>();
        List<String>dirs = new ArrayList<>();
        String path = "E:\\logs\\";
        File file = new File(path);
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
            delFile(files);
        }
        if (!dirs.isEmpty()){
            analysisFile(dirs);
        }
    }

    /**
     * 清理D磁盘日志文件
     */
    @Scheduled(fixedDelay = 3600000, initialDelay = 20000)
    public void delDiskFileD(){
        List<String> files = new ArrayList<>();
        List<String>dirs = new ArrayList<>();
        String path = "D:\\logs\\";
        File file = new File(path);
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
            delFile(files);
        }
        if (!dirs.isEmpty()){
            analysisFile(dirs);
        }
    }

    //文件判断是否需要删除
    private static void delFile(List<String>list){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //定义30天前的时间戳
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE,- 30);
        //指定历史删除时间  （小于删，大于留）
        long queryTime = calendar.getTime().getTime();
        for(int i=0;i<list.size();i++){
            File file = new File(list.get(i));
            long upTime = new Date(file.lastModified()).getTime();
            if(upTime<queryTime){
                file.delete();
                log.debug("删除日志文件{},时间为{}",file.getName(),dateFormat.format(new Date(file.lastModified())));
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
            delFile(filesList);
        }
        if (!newDirList.isEmpty()){
            analysisFile(newDirList);
        }
    }
}
