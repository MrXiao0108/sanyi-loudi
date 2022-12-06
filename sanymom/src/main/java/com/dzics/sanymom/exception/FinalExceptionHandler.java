package com.dzics.sanymom.exception;

import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author ZhangChengJun
 * Date 2021/4/22.
 * @since
 */
@Component
public class FinalExceptionHandler implements ErrorViewResolver {

    private final JsonView jsonView = new JsonView();
    @Override
    public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
        jsonView.setModel(model);
        jsonView.setRequest(request);
        jsonView.setStatus(status);
        return new ModelAndView(jsonView);
    }
}
