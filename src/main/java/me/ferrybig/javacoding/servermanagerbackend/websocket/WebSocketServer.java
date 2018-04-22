/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import me.ferrybig.javacoding.servermanagerbackend.MasterServer;
import me.ferrybig.javacoding.servermanagerbackend.internal.ServerManager;
import me.ferrybig.javacoding.servermanagerbackend.internal.config.DefaultConfigKeys;
import me.ferrybig.javacoding.servermanagerbackend.internal.config.ServerConfigBuilder;

public final class WebSocketServer implements Closeable {

	private final MasterServer server;
	private final EventLoopGroup bossGroup;
	private final EventLoopGroup workerGroup;

	public WebSocketServer(MasterServer server) {
		this.bossGroup = new NioEventLoopGroup(1);
		this.workerGroup = new NioEventLoopGroup();
		this.server = server;
	}

	public ChannelFuture startServer(int port) {

		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new WebSocketServerInitializer(this.server, null));

		return b.bind(port);
	}

	@Override
	public void close() throws IOException {
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}
}
