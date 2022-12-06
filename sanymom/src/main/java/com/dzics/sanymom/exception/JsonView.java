package com.dzics.sanymom.exception;

import com.dzics.common.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.View;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * @Classname JsonView
 * @Description 描述
 * @Date 2022/3/24 15:00
 * @Created by NeverEnd
 */
@Slf4j
public class JsonView implements View {

    private static final MediaType APPLICATION_JSON_UTF8 = new MediaType("application", "json", StandardCharsets.UTF_8);

    public JsonView() {
        // TODO Auto-generated constructor stub
    }

    private HttpServletRequest request;
    private HttpStatus status;
    private Map<String, Object> model;


    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }


    public void setModel(Map<String, Object> model) {
        this.model = model;
    }

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        if (response.isCommitted()) {
            String message = getMessage(model);
            log.error(message);
            return;
        }
        response.setContentType(APPLICATION_JSON_UTF8.toString());
        StringBuilder builder = new StringBuilder();
        String timestamp = DateUtil.getDateStr((Date) this.model.get("timestamp"));
        Object message = this.model.get("message");
        Object trace = this.model.get("trace");
        if (response.getContentType() == null) {
            response.setContentType(getContentType());
        }
        builder.append("{");
        builder.append("\"data\":\"error\",");
        builder.append("\"path\":\""+this.model.get("path")+"\",");
        builder.append("\"visitTime\":\""+timestamp+"\",");
        builder.append("\"type\":\""+htmlEscape(this.model.get("error"))+"\",");
        builder.append("\"code\":\""+htmlEscape(this.model.get("status"))+"\",");
        builder.append("\"msg\":\""+htmlEscape(message)+"\"");
        builder.append("}");
        /*
         * builder.append("<html><body><h1>Whitelabel Error Page</h1>").append(
         * "<p>This application has no explicit mapping for /error, so you are seeing this as a fallback.</p>"
         * ) .append("<div id='created'>").append(timestamp).append("</div>")
         * .append("<div>There was an unexpected error (type=").append(htmlEscape(model.
         * get("error")))
         * .append(", status=").append(htmlEscape(model.get("status"))).append(
         * ").</div>"); if (message != null) {
         * builder.append("<div>").append(htmlEscape(message)).append("</div>"); } if
         * (trace != null) {
         * builder.append("<div style='white-space:pre-wrap;'>").append(htmlEscape(trace
         * )).append("</div>"); }
         */
        /* builder.append("</body></html>"); */
        response.getWriter().append(builder.toString());
    }

    private String htmlEscape(Object input) {
        return (input != null) ? HtmlUtils.htmlEscape(input.toString()) : null;
    }

    private String getMessage(Map<String, ?> model) {
        Object path = model.get("path");
        String message = "Cannot render error page for request [" + path + "]";
        if (model.get("message") != null) {
            message += " and exception [" + model.get("message") + "]";
        }
        message += " as the response has already been committed.";
        message += " As a result, the response may have the wrong status code.";
        return message;
    }

    @Override
    public String getContentType() {
        return "application/json";
    }

}

