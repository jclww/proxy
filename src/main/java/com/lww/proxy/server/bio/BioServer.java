package com.lww.proxy.server.bio;

import com.lww.proxy.server.bio.thread.WorkThread;
import com.lww.proxy.util.Config;
import com.lww.proxy.util.ThreadPoolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ThreadPoolExecutor;

public class BioServer {
    private static ThreadPoolExecutor threadPool = ThreadPoolUtil.getDefaultInstance();
    private static final Logger logger = LoggerFactory.getLogger(BioServer.class);

    public static void main(String[] args) {
        bind(Config.PORT);
    }

    public static void bind(int port) {
        //监听端口
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            logger.info("bind port:{} has exception:{}, server stop!", port, e.getMessage());
            logger.debug("IOException:", e);
            return;
        }
        while (true) {
            try {
                threadPool.submit(new WorkThread(serverSocket.accept()));
            } catch (IOException e) {
                logger.info("client accept has exception:{}, server stop!", e.getMessage());
                logger.debug("IOException:", e);
            }
        }
    }
}
