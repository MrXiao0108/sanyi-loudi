# 看板`socket`协议连接介绍

1.连接地址:`/equiment/status/socket/server/eqc7NS`
 
2.发送参数介绍
   -  `UTILIZATION`获取稼动相关信息
   -  `ALLDEVICE`获取所有设备
   -   `DEVICE` 设备信息
   
3.返回参数介绍
   - 通用对象
   - ```javascript
      参数名
       code: 0 正常 500异常
       count: 数量
       data: 常用返回数据对象
       msg: 提示信息
     
  - 基础消息分类定义
     
  - ```javascript
      设备消息分类定义
       type: RUNSTATUS状态信息,DOWNSTATUS停机次数,PROSUM生产数量,UTILIZATION稼动
       equipmentNo:设备序号
       equipmentType:设备类型 1检测设备,2机床,3机器人
  
 -  机器人停机次数信息
 
   - ```javascript
      downSum: 停机次数

 -  机器人生产数量信息
      
   - ```javascript
     nowNum: 当前产量
     roughNum: 投入数量
     badnessNum: 不良品数量
     
 -  机器人状态实时信息  
   - ```javascript
       currentLocation:位置
       operatorMode:操作模式
       connectState:连接状态
       runStatus:运行状态
       emergencyStatus:急停状态
       alarmStatus:告警状态
     
 - 稼动相关
 
   - ```javascript
     historyOk:历史稼动率
     historyNg:历史故障率
     dayOk:当日稼动率
     dayNg:当日故障率
     ok:稼动时间
     ng:故障时间 
     ```