package com.lww.proxy.server.nettty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpResponse;

public class HttpProxyClientHandle extends ChannelInboundHandlerAdapter {

    private Channel clientChannel;

    public HttpProxyClientHandle(Channel clientChannel) {
        this.clientChannel = clientChannel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpResponse response = (FullHttpResponse) msg;
        // 只有http 请求可以获取详细信息 并且修改 请求信息 & 服务器返回信息
        response.headers().add("proxy","Boom ~");
        clientChannel.writeAndFlush(msg);
    }
}