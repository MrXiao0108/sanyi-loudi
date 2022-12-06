package com.dzics.kanban.util.upload;

import java.nio.charset.Charset;

/**
 * @author ZhangChengJun
 * Date 2019/12/20.
 */
public class Constants {
    public static final String VERSION = "7.2.25";
    public static final int BLOCK_SIZE = 4194304;
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    public static final int CONNECT_TIMEOUT = 10;
    public static final int WRITE_TIMEOUT = 0;
    public static final int READ_TIMEOUT = 30;
    public static final int DISPATCHER_MAX_REQUESTS = 64;
    public static final int DISPATCHER_MAX_REQUESTS_PER_HOST = 16;
    public static final int CONNECTION_POOL_MAX_IDLE_COUNT = 32;
    public static final int CONNECTION_POOL_MAX_IDLE_MINUTES = 5;

    private Constants() {
    }
}
