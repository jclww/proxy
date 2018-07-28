package com.lww.proxy.server.nettty;

import com.lww.proxy.util.Config;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;

public class HttpProxyServerHandle extends ChannelInboundHandlerAdapter {

    private ChannelFuture cf;
    private String host;
    private int port;

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            String host = request.headers().get(Config.HOST);
            String[] temp = host.split(Config.HOST_POST_REGEX);
            int port = Config.DEFAULT_HTTP_PORT;
            if (temp.length > 1) {
                port = Integer.parseInt(temp[1]);
            } else {
                if (request.uri().indexOf(Config.HTTPS) == 0) {
                    port = Config.DEFAULT_HTTPS_PORT;
                }
            }
            this.host = temp[0];
            this.port = port;
            if (Config.HTTPS_CONNECT_SIGN.equalsIgnoreCase(request.method().name())) {
                HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, new HttpResponseStatus(200, "Connection established"));
                ctx.writeAndFlush(response);
                ctx.pipeline().remove("httpCodec");
                ctx.pipeline().remove("httpObject");
                return;
            }
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(ctx.channel().eventLoop())
                    .channel(ctx.channel().getClass())
                    .handler(new HttpProxyInitializer(ctx.channel()));
            ChannelFuture cf = bootstrap.connect(temp[0], port);
            cf.addListener((ChannelFuture future)-> {
                if (future.isSuccess()) {
                    future.channel().writeAndFlush(msg);
                } else {
                    ctx.channel().close();
                }
            });
        } else {
            if (cf == null) {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(ctx.channel().eventLoop())
                        .channel(ctx.channel().getClass())
                        .handler(new ChannelInitializer() {
                            @Override
                            protected void initChannel(Channel ch) throws Exception {
                                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx0, Object msg) throws Exception {
                                        ctx.channel().writeAndFlush(msg);
                                    }
                                });
                            }
                        });
                cf = bootstrap.connect(host, port);
                cf.addListener((ChannelFuture future) -> {
                    if (future.isSuccess()) {
                        future.channel().writeAndFlush(msg);
                    } else {
                        ctx.channel().close();
                    }
                });
            } else {
                cf.channel().writeAndFlush(msg);
            }
        }
    }


}