package com.dzics.udp.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * 返回信息封装类
 *
 * @author ZhangChengJun
 * Date 2021/3/19.
 * @since
 */
public class Response implements Serializable {
    private static final long serialVersionUID = 1L;
    public final static int SUCCESS = 0;
    public final static int FAIL = 1;
    @NotNull
    @Min(0L)
    @Max(1L)
    public Integer code;

    @Null
    public String msg;

    public byte[] encode() throws IOException {
        ByteArrayOutputStream bais=new ByteArrayOutputStream();
        return bais.toByteArray();
    }
    public static Response success(String msg) {
        Response resp = new Response();
        resp.code = Response.SUCCESS;
        resp.msg = msg;
        return resp;
    }

    public static Response fail(String msg) {
        Response resp = new Response();
        resp.code = Response.FAIL;
        resp.msg = msg;
        return resp;
    }
}
