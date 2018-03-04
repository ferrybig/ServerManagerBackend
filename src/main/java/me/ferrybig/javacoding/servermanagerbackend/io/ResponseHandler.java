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
import me.ferrybig.javacoding.servermanagerbackend.internal.config.ServerConfig;
import me.ferrybig.javacoding.servermanagerbackend.util.GenericTypeAdaptorFactory;

/**
 *
 * @author Fernando van Loenhout
 */
public class ResponseHandler extends ChannelDuplexHandler {

	private static final Gson JSON_PARSER = new GsonBuilder()
		.registerTypeAdapter(Request.class, new RequestDeserializer())
		.registerTypeAdapterFactory(new GenericTypeAdaptorFactory<>(ServerConfig::typeAdapter, ServerConfig.class))
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
			Request req = JSON_PARSER.fromJson(json, Request.class);
			String validation = req.validate();
			if (validation != null) {
				ctx.channel().close();
				throw new IllegalArgumentException("Wrong input data for channel:" + ctx.channel() + ": " + validation);
			}
			msg = req;
		}
		ctx.fireChannelRead(msg);
	}

}
