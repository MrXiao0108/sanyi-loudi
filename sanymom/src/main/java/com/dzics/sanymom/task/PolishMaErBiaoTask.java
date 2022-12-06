package com.dzics.sanymom.task;

import com.alibaba.excel.util.StringUtils;
import com.dzics.common.util.RedisKey;
import com.dzics.sanymom.util.RedisUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
@Slf4j
@Component
public class PolishMaErBiaoTask {
    @Value("${order.code}")
    private String orderCode;
    @Value("${spring.rabbitmq.username}")
    private String rabbitmqUser;
    @Value("${spring.rabbitmq.password}")
    private String rabbitmqPass;

    private final String TheServerIp = "10.36.8.60";

    @Value("${sanyi.polish.ip}")
    private String excelUrl;

    @Autowired
    private RedisUtil redisUtil;

    //每30s执行一次
//    @Scheduled(cron = "0/30 * * * * ? ")
    public void main1(){
        if("DZ-1887".equals(orderCode) || "DZ-1888".equals(orderCode) || "DZ-1889".equals(orderCode) || "DZ-1890".equals(orderCode) || "DZ-1891".equals(orderCode)) {
            getNewExcelData();
        }
    }

    public void getNewExcelData(){
        String fileName = excelUrl;
        File file = new File(fileName);
        int i;
        Sheet sheet;
        Workbook workbook;
        Cell cell1;
        try {
            workbook = Workbook.getWorkbook(file);
            sheet = workbook.getSheet(0);
            i = realRows(sheet);
            //此处判断当前读取excel行数是否等于缓存行数，如果等于无需继续，不等于读取信号模拟mq完成发送
            int o = Integer.valueOf(String.valueOf(redisUtil.get(RedisKey.Get_Polish_Data + orderCode)));
            //获取检测二维码
            String qrCode = String.valueOf(redisUtil.get(RedisKey.Get_Polish_QrCode+orderCode));
            //判断excel表数据是否有新的变化 并且 redis缓存中的打磨台二维码是否是空码
            if((Integer.valueOf(String.valueOf(o)) != i) && (StringUtils.isEmpty(qrCode))==false){
                //获取检测值
                cell1 = sheet.getCell(0,i-1);
                //如果读取为空,不进行任何操作，反之开始模拟mq信号发送
               if("".equals(cell1.getContents())==false && "".equals(qrCode)==false){
                    //Q,6,1300,DZ-1887,1,W72B-2-2110030080,123.35,1
                    String msg = "Q,6,1300,"+orderCode+",1,"+qrCode+","+cell1.getContents()+",1";
                    ConnectionFactory factory = new ConnectionFactory();
                    factory.setHost(TheServerIp);
                    factory.setPort(5672);
                    factory.setUsername(rabbitmqUser);
                    factory.setPassword(rabbitmqPass);
                    Connection connection = factory.newConnection();
                    Channel channel = connection.createChannel();
                    channel.queueDeclare("dzics-dev-gather-v1-exchange-udp-check-qrodeup",false,false,false,null);
                    channel.basicPublish("dzics-dev-gather-v1-exchange-udp-check-qrodeup","dzics-dev-gather-v1-routing-udp-check-qrodeup",null,msg.getBytes());
                    channel.close();
                    connection.close();
                    factory.clone();
                    log.info("读取马尔表excel，数据读取上传成功，msg："+msg);
               }
                //设置最新缓存数量
                redisUtil.set(RedisKey.Get_Polish_Data+orderCode,i);
            }
            workbook.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 循环判断--获取excel的真实行数
     */
    private static int realRows(Sheet sheet) {
        int rows = sheet.getRows();
        int columns=sheet.getColumns();
        List<Integer> realRows=new ArrayList<Integer>();
        for (int i = 0; i < rows; i++) {
            boolean isBlankRow=false;
            for (int j = 0; j < columns; j++) {
                if (!StringUtils.isEmpty(sheet.getCell(j, i).getContents())) {
                    isBlankRow=false;
                    break;
                } else {
                    isBlankRow=true;
                }
            }
            if (!isBlankRow) {
                //如果行的每一列不为空，把循环到的行添加到集合
                realRows.add(i);
            }
        }
        //集合的size就是真实数据的行数，去掉表头，就是需要的真实行数
        int row = realRows.size();
        return row;
    }
}
