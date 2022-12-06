package com.dzics.kanban.config;



import com.dzics.kanban.stereotype.ApiConverter;
import com.dzics.kanban.stereotype.ApiVersion;
import com.dzics.kanban.stereotype.ApiVersionCondition;
import com.dzics.kanban.stereotype.ApiVersionItem;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

/**
 * @author neverend
 */
public class ApiVersionHandlerMapping extends RequestMappingHandlerMapping {
    @Override
    protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
        return buildFrom(AnnotationUtils.findAnnotation(handlerType, ApiVersion.class));
    }

    @Override
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        return buildFrom(AnnotationUtils.findAnnotation(method, ApiVersion.class));
    }

    private ApiVersionCondition buildFrom(ApiVersion platform) {
        return platform == null ? new ApiVersionCondition(new ApiVersionItem()) :
                new ApiVersionCondition(ApiConverter.convert(platform.value()));
    }
}
