package com.lww.proxy.server.bio.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ProxyThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(ProxyThread.class);

    /**
     * client send msg
     */
    private InputStream clientInput;
    /**
     * send client msg to server
     */
    private OutputStream proxyOutput;

    public ProxyThread(InputStream clientInput, OutputStream proxyOutput) {
        this.clientInput = clientInput;
        this.proxyOutput = proxyOutput;
    }

    @Override
    public void run() {
        while (true) {
            try {

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int count = 0;

                while (clientInput.available() != 0) {
                    count = clientInput.read(buffer);
                    bos.write(buffer, 0, count);
                }
                byte[] bytes = bos.toByteArray();
                logger.info("client send msg :{}", new String(bytes));

                proxyOutput.write(bytes);
            } catch (IOException e) {
                logger.info("send msg to real server exception{}", e.getMessage());
                logger.debug("Exception:", e);
                break;
            }
        }
    }
}
