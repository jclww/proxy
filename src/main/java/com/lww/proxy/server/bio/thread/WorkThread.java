package com.lww.proxy.server.bio.thread;

import com.lww.proxy.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class WorkThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(WorkThread.class);

    private Socket socket;
    /**
     * send msg to client stream
     */
    private OutputStream clientOutput = null;
    /**
     * client send msg stream
     */
    private InputStream clientInput = null;
    /**
     * connect real server
     */
    private Socket proxySocket = null;
    /**
     * send msg to server stream
     */
    private InputStream proxyInput = null;
    /**
     * server back msg stream
     */
    private OutputStream proxyOutput = null;

    private String hostAndPort;
    /**
     * real server host
     */
    private String host;
    /**
     * real server port
     * default 80
     */
    private Integer port = 80;


    public WorkThread(Socket socket) {
        this.socket = socket;
        try {
            clientInput = socket.getInputStream();
            clientOutput = socket.getOutputStream();
        } catch (IOException e) {
            logger.info("client accept has exception:{}, server stop!", e.getMessage());
            logger.debug("IOException:", e);
        }
    }

    @Override
    public void run() {
        BufferedReader lineBuffer = new BufferedReader(new InputStreamReader(clientInput));
        StringBuilder headStrBuilder = new StringBuilder();
        try {
            String line;
            while (null != (line = lineBuffer.readLine())) {
                headStrBuilder.append(line).append(Config.HTTP_LINE_SPLIT);
                if (line.length() == 0) {
                    break;
                } else {
                    String[] temp = line.split(Config.BLANK);
                    if (temp[0].contains(Config.HOST)) {
                        hostAndPort = temp[1];
                    }
                }
            }
            logger.info("hostAndPort : {}", hostAndPort);
            String[] hostTemp = hostAndPort.split(Config.HOST_POST_REGEX);
            host = hostTemp[0];
            if (hostTemp.length > 1) {
                port = Integer.valueOf(hostTemp[1]);
            }
            String headString = headStrBuilder.toString();
            if (headString.contains(Config.HTTPS_CONNECT_SIGN)) {
                clientOutput.write(Config.HTTPS_PROXY_RESPONSE.getBytes());
                clientOutput.flush();
            }
            proxySocket = new Socket(host, port);
            proxyInput = proxySocket.getInputStream();
            proxyOutput = proxySocket.getOutputStream();
            new ProxyThread(clientInput, proxyOutput).start();

            while (true) {
                try {
                    clientOutput.write(proxyInput.read());
                } catch (IOException e) {
                    logger.info("get meg send to client IOException:{}, server stop!", e.getMessage());
                    break;
                }
            }
        } catch (Exception e) {
            logger.info("get meg from client exception:{}, server stop!", e.getMessage());
            logger.debug("Exception:", e);
        }
    }
}
