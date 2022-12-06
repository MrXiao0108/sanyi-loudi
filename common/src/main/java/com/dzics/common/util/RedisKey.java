package com.dzics.common.util;

public final class RedisKey {

    //未知tcp指令key前缀
    public static final String UNKNOWN_TCP = "unknown:data:";

    //redis查询为空时  判断是否需要查询mysql的标识符  (查询)
    public static final String SELECT_MYSQL_TRUE = "1";
    //redis查询为空时  判断是否需要查询mysql的标识符  (不用查询)
    public static final String SELECT_MYSQL_FALSE = "-1";

    /**
     * 初始化指令 前缀
     */
    public static final String TCP_CMD_PREFIX = "tcp:cmd:prefix:";

    /**
     * 需要查询的  生产数据表key
     */
    public static final String PRO_NUM_TABLE_KEY = "pro_num_table_key";

    public static final String WORKPIECE_EXCHANGE_RESET = "udp:workpiece:exchange:flag:reset";

    public static final String Last_Work_Number = "dzics:last:work:number";
    public static final String PulseCompensation = "pulseCompensation:";
    public static final String FREQUENCY_MIN = "frequency:min:";
    public static final String MOM_REDIS_KEY = "mom:redis:key:";
    public static final String SysPlanTasDzProductionPlanDayMapperSelDateLinNok = "SysPlanTask:DzProduction:PlanDayMapper:SelDateLinNok";
    public static final String SysPlanTasDzProductionPlanDayMapperSelDateLinNokSignal = "SysPlanTask:DzProduction:PlanDayMapper:SelDateLinNok:Signal";

    /**
     * 产品检测数据缓存键
     */
    public static final String INSPECTION_DATA = "product:inspection:data:";

    // 看板单项检测 检测字段名(列名)key
    public static final String TABLE_COL_CON = "product:table_col_con";
    // 看板单项检测 检测字段值
    public static final String TABLE_COL_VAL = "product:table_col_val";
    // 看板单项检测 检测结果字段值
    public static final String OUT_OK_VAL = "product:out_ok_val";

    // 频繁触发异常的指令前缀
    public static final String ERR_TCP_VALUE = "err_tcp_value:";
    public static final String cacheService_getMomOrderNoProducBarcode = "cache:service:getmom:orderNoProducBarcode:";
    public static final String cacheService_getMomOrderNoProducBarcode_SUM = "cache:service:getmom:orderNoProducBarcode:sum:";
    /**
     * 摩擦焊机 设备状态信息缓存
     */
    public static final String socketIoHandler_accqDzEquipmentService_getEquimentStateX = "socketIoHandler:accqDzEquipmentService:getEquimentStateX:";
    public static final String getLineIdIsShowAcc = "ProductionQuantityService:getEquipmentAvailable:getLineIdIsShowAcc:";
    public static final String Rob_Call_Material = "ProTaskOrderServiceImpl:processDistribution:";

    //产品绑定的需要显示的检测项缓存标识
    public static final String TEST_ITEM = "test_item:";
    public static final String momHttpRequestService_getMyReqTypeId = "momHttpRequestService:getMyReqTypeId:";


    /**
     * 登录用户缓存
     */
    public static final String FILE_CAR = "FILE:CAR:";

    public static final String USERPERSIONPFXKEY = "USERPERSIONPFXKEY";
    public static final String REF_TOEKN_TIME = "Ref:Toekn:Time:";
    public static final String REF_TOEKN_TIME_TOKEN = "Ref:Toekn:Time:Token";
    public static final String USER_NAME_AND_USER_TYPE = "User:Name:And:User:Type";

    public static final String LEASE_CAR_TOKEN_HISTORY = "lease:car:token:history:";
    public static final String SYS_BUS_TASK_ARRANGE = "SysBusTask:Arrange";
    public static final String KEY_RUN_MODEL_DANGER = "SYSTEM:KEY:RUN:MODEL:DANGER";


    /**
     * 根据订单产线号获取绑定设备的五日内产量
     */
    public static final String BU_PRODUCTION_QUANTITY_SERVICE_IMPL_GET_OUTPUT_BY_LINE_ID = "BuProductionQuantityServiceImpl:getOutputByLineId";
    /**
     * 根据订单产线号查询所有设备当日用时分析(旧)
     */
    public static final String BU_PRODUCTION_QUANTITY_SERVICE_IMPL_GET_EQUIPMENT_AVAILABLE = "BuProductionQuantityServiceImpl:getEquipmentAvailable";
    /**
     * 根据订单产线号查询近五日稼动率
     */
    public static final String BU_PRODUCTION_QUANTITY_SERVICE_IMPL_GET_PRODUCTION_PLAN_FIVE_DAY = "BuProductionQuantityServiceImpl:getProductionPlanFiveDay";
    /**
     * 根据订单产线号查询近五日产线计划分析
     */
    public static final String BU_PRODUCTION_QUANTITY_SERVICE_IMPL_GET_PLAN_ANALYSIS = "BuProductionQuantityServiceImpl:getPlanAnalysis";
    /**
     * 订单产线号查询刀具信息数据
     */
    public static final String BU_PRODUCTION_QUANTITY_SERVICE_IMPL_GET_TOOL_INFO_DATA = "BuProductionQuantityServiceImpl:getToolInfoData";
    /**
     * 合格/不合格数量  日产为单位
     */
    public static final String BU_PRODUCTION_QUANTITY_SERVICE_IMPL_GET_MONTHLY_CAPACITY = "BuProductionQuantityServiceImpl:getMonthlyCapacity";


    public static final String BU_PRODUCTION_QUANTITY_SERVICE_IMPL_GET_DEVICEPRODUCTION_QUANTITY = "BuProductionQuantityServiceImpl:getDeviceproductionQuantity";

    public static final String BU_PRODUCTION_QUANTITY_SERVICE_IMPL_GET_LINE_SUM_QUANTITY = "BuProductionQuantityServiceImpl:getLineSumQuantity:";

    public static final String GET_INPUT_OUTPUT_DEFECTIVE_PRODUCTS = "BuProductionQuantityServiceImpl:getInputOutputDefectiveProducts:";

    public static final String GET_SOCKET_UTILIZATION = "BuProductionQuantityServiceImpl:getSocketUtilization:";
    public static final String GET_BUUND_QR_CODE = "BuProductionQuantityServiceImpl:boundQrCode:";
    public static final String GET_UN_BOUND_QR_CODE = "BuProductionQuantityServiceImpl:unBoundQrCode:";
    /**
     * 合格/不合格数量  月产为单位
     */
    public static final String BU_PRODUCTION_QUANTITY_SERVICE_IMPL_GET_MONTH_DATA = "BuProductionQuantityServiceImpl:getMonthData";
    public static final String GET_INSPECTION_DATA = "BuProductionQuantityServiceImpl:getInspectionData:";
    /**
     * 五日内生产数量 NG和OK
     */
    public static final String GET_DATA_NG_AND_OK = "BuProductionQuantityServiceImpl:getDataNgAndOk:";
    /**
     * 查询所有设备当日用时分析(新)
     */
    public static final String GET_EQUIPMENT_TIME_ANALYSIS = "BuProductionQuantityServiceImpl:equipmentTimeAnalysis:";
    /**
     * 查询当日生产合格率
     */
    public static final String GET_DAILY_PASS_RATE = "BuProductionQuantityServiceImpl:dailyPassRate:";

    public static final String MomOrderController_busMomOrderService_addOrder = "momOrderController:busMomOrderService:addOrder:";

    public static final String MomOrderController_busMomOrderService_dailyProductionDetails = "momOrderController:busMomOrderService:dailyProductionDetails:";
    public static final String MomOrderController_busMomOrderService_productionDailyReport = "momOrderController:busMomOrderService:productionDailyReport:";
    public static final String BuProductionQuantityServiceImpl_allEquipmentDailyCapacity = "buProductionQuantityServiceImpl:allEquipmentDailyCapacity:";
    public static final String BuProductionQuantityServiceImpl_allEquipmentDailyCapacityTwo = "buProductionQuantityServiceImpl:allEquipmentDailyCapacityTwo:";

    public static final String BuProductionQuantityServiceImpl_GetProductionLineNumberByDay = "buProductionQuantityServiceImpl:getProductionLineNumberByDay:";

    public static final String BU_PRODUCTION_QUANTITY_SERVICE_IMPL_GET_MONTHLY_CAPACITY_SHIFT = "BuProductionQuantityServiceImpl:getMonthlyCapacityShift";
    public static final String BU_PRODUCTION_QUANTITY_SERVICE_IMPL_GET_MONTH_DATA_SHITF = "BuProductionQuantityServiceImpl:getMonthDataShift";

    public static final String getLineIdIsShow = "BuProductionQuantityService:equipmentTimeAnalysis:getLineIdIsShow:";
    public static final String dzProductionLineMapperGetLineEqmentId = "BuProductionQuantityService:getLineSumQuantity:";
    public static final String BuProductionQuantityService_getDeviceTimeAnalysis = "BuProductionQuantityService:getDeviceTimeAnalysis:";
    public static final String REF = "htmlRefreshController:wareHouseServiceImpl:refresh";
    public static final String SPLIT_DATE_DAY_TIME_INS = "split:Date:Day:TimeIns:";
    public static final String OPEN_SERIALNOS = "open:serialnos:";
    public static final String MOM_REDIS_KEY_GROUPID = "mom:redis:key:groupid:";
    public static final String GET_CURRENT_DATE = "BuProductionQuantityServiceImpl:getCurrentDate:";
    public static final String SANY_MOM_USER_FREQUENCY = "sany:mom:user:frequency:";
    public static final String AGAIN_REQUEST_LOGID = "again:request:logid:";
    public static final String MATERIAL_POINT_STATUS = "material:point:status:";
    /**
     * 脉冲统计
     */
    public static final String DAY_PRO_SUN_SIGNAL = "dayPro:sumSinagl:";




    public static final String MA_ER_BIAO_CHECK_HISTORY = "maerbiao:checkHistory:";

    /**
     * 手动订单 当前在做产品型号,用来实时更新手动订单与现场生产产品型号保持一致
     * */
    public static final String Now_Work_ProductAlias = "NowWorkProductAlias:";

    /**
     * 校验报工是否正常
     * */
    public static final String Work_Report_Status = "WorkReportStatus:";

    /**
     * 马尔表excel数据 最新行数
     * */
    public static final String Get_Polish_Data = "GetPolishData:";

    /**
     * 当前人工打磨台最新检测的产品二维码
     * */
    public static final String Get_Polish_QrCode = "GetPolishDataQrCode:";
}
