package com.dzics.common.model.write;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

public class DeviceTypeConverter implements Converter {
    @Override
    public Class supportJavaTypeKey() {
        return null;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return null;
    }

    @Override
    public Object convertToJavaData(CellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return null;
    }

    @Override
    public CellData convertToExcelData(Object value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {

        String x = "";
        if (value != null) {
            String integer =  value.toString();
            if (integer.equals("1")) {
                x = "检测设备";
            } else if (integer.equals("2")) {
                x = "机床";
            }else if(integer.equals("3")){
                x = "机器人";
            }
        }
        return new CellData(x);
    }
}
