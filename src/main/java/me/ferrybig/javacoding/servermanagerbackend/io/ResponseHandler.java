/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import me.ferrybig.javacoding.servermanagerbackend.api.request.Request;
import me.ferrybig.javacoding.servermanagerbackend.api.request.RequestDeserializer;
import me.ferrybig.javacoding.servermanagerbackend.api.response.Response;

/**
 *
 * @author Fernando van Loenhout
 */
public class ResponseHandler extends ChannelDuplexHandler {

	private static final Gson JSON_PARSER = new GsonBuilder()
			.registerTypeAdapter(Request.class, new RequestDeserializer())
			.create();

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		if (msg instanceof Response) {
			msg = new TextWebSocketFrame(JSON_PARSER.toJson(msg));
		}
		ctx.write(msg, promise);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof TextWebSocketFrame) {
			String json = ((TextWebSocketFrame) msg).text();
			((TextWebSocketFrame) msg).release();
			msg = JSON_PARSER.fromJson(json, Request.class);
		}
		ctx.fireChannelRead(msg);
	}

}
