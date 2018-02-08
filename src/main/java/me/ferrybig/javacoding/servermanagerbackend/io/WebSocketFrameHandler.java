/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.io;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import me.ferrybig.javacoding.servermanagerbackend.api.request.ActionRequest;
import me.ferrybig.javacoding.servermanagerbackend.api.request.ChannelRequest;
import me.ferrybig.javacoding.servermanagerbackend.api.request.KillStreamRequest;
import me.ferrybig.javacoding.servermanagerbackend.api.request.Request;
import me.ferrybig.javacoding.servermanagerbackend.api.response.InstantResponse;
import me.ferrybig.javacoding.servermanagerbackend.api.response.Response;
import me.ferrybig.javacoding.servermanagerbackend.api.response.StreamingDataResponse;
import me.ferrybig.javacoding.servermanagerbackend.api.response.StreamingResponse;
import me.ferrybig.javacoding.servermanagerbackend.internal.ByteListener;
import me.ferrybig.javacoding.servermanagerbackend.internal.Server;

/**
 * Echoes uppercase content of text frames.
 */
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<Request> {

	private static final Logger LOG = Logger.getLogger(WebSocketFrameHandler.class.getName());
	private ChannelHandlerContext ctx;
	private final Server server;
	private final Map<Integer, ListenerRegistration> listeners = new HashMap<>();
	private int nextStreamId = 1;

	WebSocketFrameHandler(Server server) {
		this.server = server;
	}

	private void sendBytes(int id, byte[] data, int start, int end) {
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		this.ctx = ctx;
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		super.channelReadComplete(ctx);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Request req) throws Exception {
		try {
			final Response response;
			switch (req.type) {
				case ACTION: {
					ActionRequest action = (ActionRequest) req;
					switch (action.action) {
						case "start": {
							response = new InstantResponse(true, req, "Starting server... " + server.tryStart());
						}
						break;
						case "kill": {
							response = new InstantResponse(true, req, "Stopping server... " + server.tryKill());
						}
						break;
						case "send_command": {
							server.sendMessage(action.arguments);
							response = new InstantResponse(true, req, "Sending command...");
						}
						break;
						default: {
							response = new InstantResponse(false, req, "Reqeust action not implemented");
						}
						break;
					}
				}
				break;
				case INFO: {
					response = new InstantResponse(false, req, "Reqeust info not implemented");
				}
				break;
				case KILL_STREAM: {
					KillStreamRequest request = (KillStreamRequest) req;
					ListenerRegistration registration = listeners.get(request.id);
					server.removeByteListener(registration);
					response = new InstantResponse(true, req, "Killed!");
				}
				break;
				case REGISTER_CHANNEL: {
					ChannelRequest channelRequest = (ChannelRequest) req;
					if ("console".equals(channelRequest.channelName)) {
						int id = nextStreamId++;
						ListenerRegistration registration = new ListenerRegistration(id, ctx);
						listeners.put(id, registration);
						ctx.writeAndFlush(new StreamingResponse(true, req, id));
						// Set response to null here because timing matters here.
						response = null;
						server.addByteListener(registration, true);
					} else {
						response = new InstantResponse(false, req, "Reqeust channel not implemented");
					}
				}
				break;
				default: {
					response = new InstantResponse(false, req, "Reqeust type not implemented");
				}
				break;
			}
			if (response != null) {
				ctx.writeAndFlush(response);
			}
		} catch (Exception e) {
			ctx.writeAndFlush(new InstantResponse(false, req, "Exception during execution of your command"));
			throw e;
		}
	}

	private class ListenerRegistration implements ByteListener {

		private final int id;
		private final ChannelHandlerContext ctx;

		public ListenerRegistration(int id, ChannelHandlerContext ctx) {
			this.id = id;
			this.ctx = ctx;
		}

		@Override
		public void onIncomingBytes(byte[] bytes, int start, int end) {
			this.ctx.writeAndFlush(new StreamingDataResponse(true, id, new String(bytes, start, end - start), true));
		}

	}
}
