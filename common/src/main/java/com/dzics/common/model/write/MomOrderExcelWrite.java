package com.dzics.common.model.write;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

/**
 * @作者：xnb
 * @时间：2022/8/23 0023  17:26
 */
public class MomOrderExcelWrite implements Converter {
    @Override
    public Class supportJavaTypeKey() {
        return null;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return null;
    }

    @Override
    public Object convertToJavaData(CellData cellData, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return null;
    }

    @Override
    public CellData convertToExcelData(Object o, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        String x = "";
        if(o!=null){
            String s = o.toString();
            if(s.equals("1")){
                x="正常订单";
                return new CellData(x);
            }
            if(s.equals("2")){
                x="返工返修订单";
                return new CellData(x);
            }
            if(s.equals("110")){
                x="已下发";
                return new CellData(x);
            }
            if(s.equals("120")){
                x="进行中";
                return new CellData(x);
            }
            if(s.equals("130")){
                x="已完工";
                return new CellData(x);
            }
            if(s.equals("150")){
                x="强制关闭";
                return new CellData(x);
            }
            if(s.equals("160")){
                x="已暂停";
                return new CellData(x);
            }
            if(s.equals("170") || s.equals("140")){
                x="已删除";
                return new CellData(x);
            }
        }
        return null;
    }
}
