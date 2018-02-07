/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import me.ferrybig.javacoding.servermanagerbackend.api.request.ActionRequest;
import me.ferrybig.javacoding.servermanagerbackend.api.request.ChannelRequest;
import me.ferrybig.javacoding.servermanagerbackend.api.request.KillStreamRequest;
import me.ferrybig.javacoding.servermanagerbackend.api.request.Request;
import me.ferrybig.javacoding.servermanagerbackend.api.request.RequestDeserializer;
import me.ferrybig.javacoding.servermanagerbackend.api.request.RequestTypeDeserializer;
import me.ferrybig.javacoding.servermanagerbackend.api.response.InstantResponse;
import me.ferrybig.javacoding.servermanagerbackend.api.response.Response;
import me.ferrybig.javacoding.servermanagerbackend.api.response.StreamingResponse;
import me.ferrybig.javacoding.servermanagerbackend.internal.ByteListener;
import me.ferrybig.javacoding.servermanagerbackend.internal.Server;

/**
 * Echoes uppercase content of text frames.
 */
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

	private static final Logger LOG = Logger.getLogger(WebSocketFrameHandler.class.getName());
	private ChannelHandlerContext ctx;
	private final Server server;
	private static final Gson JSON_PARSER = new GsonBuilder()
			.registerTypeAdapter(Request.class, new RequestDeserializer())
			.registerTypeAdapter(Request.Type.class, new RequestTypeDeserializer())
			.create();
	private final Map<Integer, ListenerRegistration> listeners = new HashMap<>();
	private int nextStreamId = 1;

	WebSocketFrameHandler(Server server) {
		this.server = server;
	}

	private void sendBytes(int id, byte[] data, int start, int end) {
		ByteBuf buf = ctx.alloc().buffer(end - start);
		buf.writeBytes(data, start, end);
		this.ctx.writeAndFlush(new TextWebSocketFrame(buf));
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

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
		// ping and pong frames already handled

		if (frame instanceof TextWebSocketFrame) {
			String json = ((TextWebSocketFrame) frame).text();
			Request req = JSON_PARSER.fromJson(json, Request.class);
			final Response response;
			try {
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
						response = new InstantResponse(true, req, "Reqeust channel not implemented");
					}
					break;
					case REGISTER_CHANNEL: {
						ChannelRequest channelRequest = (ChannelRequest) req;
						if ("console".equals(channelRequest.channelName)) {
							int id = nextStreamId++;
							ListenerRegistration registration = new ListenerRegistration(id);
							listeners.put(id, registration);
							server.addByteListener(registration, true);
							response = new StreamingResponse(true, req, id);
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
				ctx.write(response);
			} catch (Exception e) {
				ctx.write(new InstantResponse(false, req, "Exception during execution of your command"));
				throw e;
			}
		} else {
			String message = "unsupported frame type: " + frame.getClass().getName();
			throw new UnsupportedOperationException(message);
		}
	}

	private class ListenerRegistration implements ByteListener {

		private final int id;

		public ListenerRegistration(int id) {
			this.id = id;
		}

		@Override
		public void onIncomingBytes(byte[] bytes, int start, int end) {

		}

	}
}
