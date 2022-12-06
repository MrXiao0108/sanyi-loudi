package com.dzics.kanban.stereotype;

import org.springframework.web.servlet.mvc.condition.RequestCondition;

import javax.servlet.http.HttpServletRequest;

/**
 * @author neverend
 */
public class ApiVersionCondition implements RequestCondition<ApiVersionCondition> {
    private ApiVersionItem version;

    public ApiVersionCondition(ApiVersionItem version) {
        this.version = version;
    }

    @Override
    public ApiVersionCondition combine(ApiVersionCondition other) {
        // 选择版本最大的接口
        return version.compareTo(other.version) >= 0 ?
                new ApiVersionCondition(version) :
                new ApiVersionCondition(other.version);
    }

    @Override
    public ApiVersionCondition getMatchingCondition(HttpServletRequest request) {
//        获取请求版本号
        String version = request.getHeader("ApiVersion");
        ApiVersionItem item = ApiConverter.convert(version);
        // 获取所有小于等于版本的接口
        if (item.compareTo(this.version) >= 0) {
            return this;
        }
        return null;
    }

    @Override
    public int compareTo(ApiVersionCondition other, HttpServletRequest request) {
        // 获取最大版本对应的接口
        return other.version.compareTo(this.version);
    }
}
