/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.io;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import me.ferrybig.javacoding.servermanagerbackend.internal.Server;

/**
 */
public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {

	private static final String WEBSOCKET_PATH = "/websocket";
	private final Server server;

	private final SslContext sslCtx;

	public WebSocketServerInitializer(Server server, SslContext sslCtx) {
		this.server = server;
		this.sslCtx = sslCtx;
	}

	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		if (sslCtx != null) {
			pipeline.addLast(sslCtx.newHandler(ch.alloc()));
		}
		pipeline.addLast(new HttpServerCodec());
		pipeline.addLast(new HttpObjectAggregator(65536));
		pipeline.addLast(new HttpCorsHandler());
		pipeline.addLast(new WebSocketServerCompressionHandler());
		pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, null, true));
		pipeline.addLast(new WebSocketIndexPageHandler());
		pipeline.addLast(new LoggingHandler(LogLevel.INFO));
		pipeline.addLast(new ResponseHandler());
		pipeline.addLast(new WebSocketFrameHandler(server));
	}
}
