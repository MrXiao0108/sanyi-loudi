package com.dzics.business.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
@EnableKnife4j
public class SwaggerConfig {

    @Value("${swagger.enable}")
    private boolean enableSwagger;

    @Bean(name = "createBackgroundApiD")
    public Docket createBackgroundApiD() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("A数据中心")
                .apiInfo(backgroundApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.business.controller.datacenter.datacenter"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }


    @Bean(name = "createBackgroundApiH")
    public Docket createBackgroundApiH() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("B产品管理")
                .apiInfo(backgroundApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.business.controller.product"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }


    @Bean(name = "createBackgroundApiE")
    public Docket createBackgroundApiE() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("C设备管理")
                .apiInfo(backgroundApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.business.controller.equipment"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }

    @Bean(name = "createBackgroundApiL")
    public Docket createBackgroundApiL() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("D机器人管理")
                .apiInfo(backgroundApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.business.controller.robot"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }


    @Bean(name = "createBackgroundApiM")
    public Docket createBackgroundApiM() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("E机床管理")
                .apiInfo(backgroundApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.business.controller.machinetool"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }


    @Bean(name = "createBackgroundApiF")
    public Docket createBackgroundApiF() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("F检测管理")
                .apiInfo(backgroundApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.business.controller.inspection"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }


    @Bean(name = "createBackgroundApiJ")
    public Docket createBackgroundApiJ() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("G订单管理")
                .apiInfo(backgroundApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.business.controller.order"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }

    @Bean(name = "createBackgroundApiK")
    public Docket createBackgroundApiK() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("H产线管理")
                .apiInfo(backgroundApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.business.controller.line"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }


    @Bean(name = "createBackgroundApiG")
    public Docket createBackgroundApiG() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("K生产计划管理")
                .apiInfo(backgroundApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.business.controller.plan"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }


    @Bean(name = "BreateBackgroundApi")
    public Docket breateBackgroundApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("L系统管理")
                .apiInfo(backgroundApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.business.controller.system"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }

    @Bean(name = "createBackgroundApiI")
    public Docket createBackgroundApiI() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("M日志管理")
                .apiInfo(backgroundApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.business.controller.log"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }

    @Bean(name = "createBackgroundApiN")
    public Docket createBackgroundApiN() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("N看板接口")
                .apiInfo(backgroundApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.business.api.http"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }

    @Bean(name = "AreateRestApi")
    public Docket areateRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("N用户登录认证相关")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.business.controller.authorlogin"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }

    @Bean(name = "homePageApi")
    public Docket homePageApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("O首页")
                .apiInfo(backgroundApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.business.controller.index"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }

    @Bean(name = "WorkHistoryApi")
    public Docket workHistoricalApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("Q-ShardingSphere")
                .apiInfo(backgroundApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.business.controller.zookeeper"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }


    @Bean(name = "agvManageApi")
    public Docket agvManageApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("AGV管理")
                .apiInfo(backgroundApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.business.controller.agvmanage"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }
    @Bean(name = "methodsGroup")
    public Docket methodsGroup() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("接口组")
                .apiInfo(backgroundApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.business.api.http"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }

    @Bean(name = "workPcedured")
    public Docket workPcedured() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("工序管理")
                .apiInfo(backgroundApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.business.controller.productiontask"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }

    @Bean(name = "apiCommons")
    public Docket apiCommons() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("公共接口")
                .apiInfo(backgroundApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.business.controller.commons"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }

    @Bean(name = "apiDownload")
    public Docket apiDownload() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("Excel导出")
                .apiInfo(backgroundApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.business.controller.download"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }
    @Bean(name = "momOrder")
    public Docket momOrder() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("MOM相关")
                .apiInfo(backgroundApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dzics.business.controller.dzmom"))
                .paths(PathSelectors.any())
                .build()
                .enable(enableSwagger);
    }
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                // 设置页面标题
                .title("大正数据采集api接口文档")
                // 描述
                .description("欢迎使用大正数据采集api接口文档")
                // 设置联系人
                .contact(new Contact("大正数据采集api", "http://127.0.0.1:8082/swagger-ui.html", "xxxxxxx@qq.com"))
                // 定义版本号
                .version("1.0")
                .build();
    }

    private ApiInfo backgroundApiInfo() {
        return new ApiInfoBuilder()
                // 设置页面标题
                .title("大正数据采集后台api接口文档")
                // 描述
                .description("欢迎使用大正数据采集后台api接口文档")
                // 设置联系人
                .contact(new Contact("大正数据采集api", "http://127.0.0.1:8081/swagger-ui.html", "xxxxxxx@qq.com"))
                // 定义版本号
                .version("1.0")
                .build();
    }
}
