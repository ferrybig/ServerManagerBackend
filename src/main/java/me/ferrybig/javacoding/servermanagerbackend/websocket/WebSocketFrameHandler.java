/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import me.ferrybig.javacoding.servermanagerbackend.MasterServer;
import me.ferrybig.javacoding.servermanagerbackend.api.request.Request;
import me.ferrybig.javacoding.servermanagerbackend.routing.Client;
import me.ferrybig.javacoding.servermanagerbackend.routing.IncomingChannel;
import me.ferrybig.javacoding.servermanagerbackend.routing.OutboundChannel;

public class WebSocketFrameHandler extends SimpleChannelInboundHandler<Request> {

	private static final Logger LOG = Logger.getLogger(WebSocketFrameHandler.class.getName());
	private ChannelHandlerContext ctx;
	private final MasterServer master;
	private Client client;

	WebSocketFrameHandler(MasterServer server) {
		this.master = server;
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		this.ctx = ctx;
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		super.channelReadComplete(ctx);
		ctx.flush();
	}

	@SuppressWarnings("Convert2Lambda")
	private Client getOrMakeClient(ChannelHandlerContext ctx) {
		@SuppressWarnings("LocalVariableHidesMemberVariable")
		Client client = this.client;
		if (client == null) {
			client = new Client(new IncomingChannel() {
				@Override
				public String identify() {
					return ctx.channel().remoteAddress().toString();
				}
			}, new OutboundChannel(ctx::writeAndFlush, new AtomicInteger()::incrementAndGet, ctx.executor()::newPromise));
			this.client = client;
		}
		return client;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Request req) throws Exception {
		Client c = getOrMakeClient(ctx);
		this.master.handleRequest(client, req);
	}

}
