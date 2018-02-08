/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.io;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

/**
 *
 * @author Fernando van Loenhout
 */
public class HttpCorsHandler extends ChannelDuplexHandler {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof FullHttpRequest) {
			FullHttpRequest req = (FullHttpRequest) msg;
			if (req.method().equals(HttpMethod.OPTIONS)) {
				FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.EMPTY_BUFFER.retain());
				response.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "GET");
				response.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
				response.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "*");
				response.headers().add(HttpHeaderNames.ACCESS_CONTROL_MAX_AGE, "86400");
				response.headers().add(HttpHeaderNames.CONTENT_LENGTH, "0");
				ctx.write(response);
				req.release();
			} else {
				ctx.fireChannelRead(req);
			}
		} else {
			ctx.fireChannelRead(msg);
		}
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		if (msg instanceof FullHttpResponse) {
			FullHttpResponse response = (FullHttpResponse) msg;
			response.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "GET");
			response.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
			response.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "*");
			response.headers().add(HttpHeaderNames.ACCESS_CONTROL_MAX_AGE, "86400");
		}
		super.write(ctx, msg, promise);
	}

}
