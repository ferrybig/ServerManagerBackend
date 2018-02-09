/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import me.ferrybig.javacoding.servermanagerbackend.internal.ServerConfig;
import me.ferrybig.javacoding.servermanagerbackend.internal.ServerManager;
import me.ferrybig.javacoding.servermanagerbackend.io.WebSocketServerInitializer;

public final class WebSocketServer {

	static final boolean SSL = System.getProperty("ssl") != null;
	static final int PORT = Integer.parseInt(System.getProperty("port", SSL ? "8443" : "8070"));

	public static void main(String[] args) throws Exception {
		// Configure SSL.
		final SslContext sslCtx;
		if (SSL) {
			SelfSignedCertificate ssc = new SelfSignedCertificate();
			sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
		} else {
			sslCtx = null;
		}

		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		ExecutorService taskGroup = Executors.newCachedThreadPool(Executors.defaultThreadFactory());

		ServerManager serverManager = new ServerManager(taskGroup);

		serverManager.createServer("test", new ServerConfig(
				Arrays.asList("java", "-Dferrydebug=true", "-jar", "C:\\Users\\fernando\\Downloads\\mc\\spigot-1.12.2.jar"),
				"C:\\Users\\fernando\\Downloads\\mc"
		));
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new WebSocketServerInitializer(serverManager, sslCtx));

			Channel ch = b.bind(PORT).sync().channel();

			System.out.println("Open your web browser and navigate to "
					+ (SSL ? "https" : "http") + "://127.0.0.1:" + PORT + '/');

			ch.closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
