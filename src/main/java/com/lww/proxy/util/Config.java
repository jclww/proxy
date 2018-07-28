package com.lww.proxy.util;

public class Config {

    public static Integer port = 10086;
    public static Integer DEFAULT_HTTP_PORT = 80;
    public static Integer DEFAULT_HTTPS_PORT = 443;
    public static String HTTPS = "https";

    public static final int BUF_SIZE=1024;
    public static final int PORT = 10086;
    public static final int TIMEOUT = 3000;
    public static final String THREAD_NAMEPREFIX = "proxy";
    public static final String HTTP_LINE_SPLIT = "\r\n";
    public static final String HOST_POST_REGEX = ":";

    public static final String BLANK = " ";
    public static final String HOST = "Host";
    public static final String HTTPS_PROXY_RESPONSE = "HTTP/1.1 200 Connection Established\n\n";
    public static final String HTTPS_CONNECT_SIGN = "CONNECT";


    public static final int DEFAULT_COREPOOLSIZE = 16;
    public static final int DEFAULT_MAXPOOLSIZE = 16;

}
