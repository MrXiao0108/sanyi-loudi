package com.dzics.generator;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.ArrayList;
import java.util.List;

public class CodeGeneratorApp {
    //    作者
    private static String author = "NeverEnd";
    //    项目输出路径
    private static String outPutDir = "E:\\zcj\\zcjsrc\\oldzics\\common\\src\\main\\java";
    //    resource路径ttttttttttt
    private static String resourcePath = "E:\\zcj\\zcjsrc\\oldzics\\common\\src\\main\\resources";
    //    顶级包结构
    private static String parentPackage = "com.dzics.common";
    //    驱动
    private static String driver = "com.mysql.cj.jdbc.Driver";
    //    数据库连接地址
    private static String url = "jdbc:mysql://127.0.0.1/dzics_analysis_display?useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&serverTimezone=GMT%2B8&characterEncoding=UTF-8&allowMultiQueries=true";
    //    用户名
    private static String userName = "root";
    //    密码
    private static String password = "password";
    //    数据表前缀
    private static String tableFperFix = "";
    //    生成的表
    private static String[] tables = {"dz_equipment_pro_total_signal"};

    //    数据访问层包名
    private static String dao = "dao";
    //     业务逻辑层包名
    private static String service = "service";
    //     业务逻辑层包名
    private static String impl = "service.impl";
    //    实体类包名
    private static String entity = "model.entity";
    //    控制器层包名
    private static String controller = "controller";
    //    mapper映射包名
    private static String mapperxml = "mapper";

    private static String xmlVM = "/templates/mapper.xml.vm";

    public static void main(String[] args) {
        AutoGenerator autoGenerator = new AutoGenerator();
//        全局配置
        GlobalConfig config = new GlobalConfig();
        config.setAuthor(author)
                .setOpen(false)
                .setSwagger2(true)
                .setOutputDir(outPutDir)
                .setFileOverride(true)
                .setIdType(IdType.AUTO)
                .setServiceName("%sService")
                .setEntityName("%s")
                .setBaseResultMap(true)
                .setBaseColumnList(true)
                .setControllerName("%sController");
        autoGenerator.setGlobalConfig(config);
//        数据源配置
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDbType(DbType.MYSQL)
                .setDriverName(driver)
                .setTypeConvert(new MySqlTypeConvert() {
                    @Override
                    public IColumnType processTypeConvert(GlobalConfig globalConfig, String fieldType) {
                        //将数据库中datetime转换成date
                        if (fieldType.toLowerCase().contains("datetime")) {
                            return DbColumnType.DATE;
                        }
                        return super.processTypeConvert(globalConfig, fieldType);
                    }
                })
                .setUrl(url)
                .setUsername(userName)
                .setPassword(password);
        autoGenerator.setDataSource(dataSourceConfig);
//        包名策略配置
        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setParent(parentPackage)
                .setMapper(dao)
                .setService(service)
                .setServiceImpl(impl)
                .setController(controller)
                .setEntity(entity);
        autoGenerator.setPackageInfo(packageConfig);


        autoGenerator.setCfg(getInjectionConfig());
        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig.setXml(null);
        autoGenerator.setTemplate(templateConfig);
//        策略配置
        StrategyConfig strategyConfig = new StrategyConfig();
        List<TableFill> tableFillList = new ArrayList<TableFill>();
        tableFillList.add(new TableFill("create_time", FieldFill.INSERT));
        tableFillList.add(new TableFill("update_time", FieldFill.INSERT_UPDATE));
        strategyConfig.setCapitalMode(true)
                .setNaming(NamingStrategy.underline_to_camel)
                .setTablePrefix(tableFperFix)
                .setInclude(tables)
                .setTableFillList(tableFillList)
                .setEntityTableFieldAnnotationEnable(true)
                .setEntityLombokModel(true)
                .setRestControllerStyle(true);
        autoGenerator.setStrategy(strategyConfig);
        autoGenerator.execute();
    }

    public static InjectionConfig getInjectionConfig() {
        InjectionConfig injectionConfig = new InjectionConfig() {
            @Override
            public void initMap() {

            }
        };
        List<FileOutConfig> focList = new ArrayList<FileOutConfig>();
        //生成xml
        focList.add(new FileOutConfig("/templates/mapper.xml.vm") {
            // 自定义输出文件目录
            @Override
            public String outputFile(TableInfo tableInfo) {
                return resourcePath + "/mapper/" + tableInfo.getEntityName() + "Mapper.xml";
            }
        });
        injectionConfig.setFileOutConfigList(focList);
        return injectionConfig;

    }

}
