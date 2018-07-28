package com.lww.proxy.server.nettty;

import com.lww.proxy.util.Config;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup(2);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .option(ChannelOption.TCP_NODELAY, true)
//                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<Channel>() {

                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast("httpCodec", new HttpServerCodec());
                            ch.pipeline().addLast("httpObject", new HttpObjectAggregator(65536));
                            ch.pipeline().addLast("serverHandle", new HttpProxyServerHandle());
                        }
                    });
            ChannelFuture f = b
                    .bind(Config.PORT)
                    .sync();
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("start error:", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


}
