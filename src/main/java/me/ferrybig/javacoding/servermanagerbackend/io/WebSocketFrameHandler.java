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
import me.ferrybig.javacoding.servermanagerbackend.api.request.KillStreamRequest;
import me.ferrybig.javacoding.servermanagerbackend.api.request.Request;
import me.ferrybig.javacoding.servermanagerbackend.api.request.server.ActionRequest;
import me.ferrybig.javacoding.servermanagerbackend.api.request.server.ChannelRequest;
import me.ferrybig.javacoding.servermanagerbackend.api.request.server.InfoRequest;
import me.ferrybig.javacoding.servermanagerbackend.api.request.server.UpdatePropertiesRequest;
import me.ferrybig.javacoding.servermanagerbackend.api.response.InstantResponse;
import me.ferrybig.javacoding.servermanagerbackend.api.response.Response;
import me.ferrybig.javacoding.servermanagerbackend.api.response.StreamingDataResponse;
import me.ferrybig.javacoding.servermanagerbackend.api.response.StreamingResponse;
import me.ferrybig.javacoding.servermanagerbackend.internal.ByteListener;
import me.ferrybig.javacoding.servermanagerbackend.internal.Server;
import me.ferrybig.javacoding.servermanagerbackend.internal.ServerManager;
import me.ferrybig.javacoding.servermanagerbackend.internal.StateListener;

/**
 * Echoes uppercase content of text frames.
 */
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<Request> {

	private static final Logger LOG = Logger.getLogger(WebSocketFrameHandler.class.getName());
	private ChannelHandlerContext ctx;
	private final ServerManager serverManager;
	private final Map<Integer, ListenerRegistration> listeners = new HashMap<>();
	private int nextStreamId = 1;

	WebSocketFrameHandler(ServerManager server) {
		this.serverManager = server;
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
	protected void channelRead0(ChannelHandlerContext ctx, Request req) throws Exception {
		try {
			final Response response;

			switch (req.type) {
				case SERVER_ACTION: {
					ActionRequest action = (ActionRequest) req;
					Server server = this.serverManager.getServer(action.server);
					if (server == null) {
						response = new InstantResponse(false, req, "Server not found");
					} else {
						switch (action.action) {
							case START: {
								response = new InstantResponse(true, req, "Starting server... " + server.tryStart());
							}
							break;
							case KILL: {
								response = new InstantResponse(true, req, "Stopping server... " + server.tryKill());
							}
							break;
							case SEND_COMMAND: {
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
				}
				break;
				case SERVER_INFO: {
					InfoRequest info = (InfoRequest) req;
					Server server = this.serverManager.getServer(info.server);
					if (server == null) {
						response = new InstantResponse(false, req, "Server not found");
					} else {
						response = new InstantResponse(true, req, server.getConfig());
					}
				}
				break;
				case KILL_STREAM: {
					KillStreamRequest request = (KillStreamRequest) req;
					ListenerRegistration registration = listeners.get(request.id);
					if (registration != null) {
						registration.cancel();
					}
					response = new InstantResponse(true, req, "Killed!");
				}
				break;
				case SERVER_LISTEN: {
					ChannelRequest channelRequest = (ChannelRequest) req;
					Server server = this.serverManager.getServer(channelRequest.server);
					if ("console".equals(channelRequest.channelName)) {
						int id = nextStreamId++;
						ByteListenerRegistration registration = new ByteListenerRegistration(server, id, ctx);
						listeners.put(id, registration);
						ctx.write(new StreamingResponse(true, req, id));
						// Set response to null here because timing matters here.
						response = null;
						server.addByteListener(registration, true);
					} else if ("state".equals(channelRequest.channelName)) {
						int id = nextStreamId++;
						StateListenerRegistration registration = new StateListenerRegistration(server, id, ctx);
						listeners.put(id, registration);
						ctx.write(new StreamingResponse(true, req, id));
						// Set response to null here because timing matters here.
						response = null;
						server.addStateListener(registration);
					} else {
						response = new InstantResponse(false, req, "Reqeust channel not implemented");
					}
				}
				break;
				case SERVER_UPDATE_PROPERTIES: {
					UpdatePropertiesRequest updated = (UpdatePropertiesRequest)req;
					LOG.info(updated.getProperties().toString());
					response = new InstantResponse(false, req, "Reqeust type not implemented");
				}
				break;
				case SERVER_LIST: {
					response = new InstantResponse(false, req, "Reqeust type not implemented");
				}
				break;
				default: {
					response = new InstantResponse(false, req, "Reqeust type not implemented");
				}
				break;
			}
			if (response != null) {
				ctx.write(response);
			}
		} catch (Exception e) {
			ctx.writeAndFlush(new InstantResponse(false, req, "Exception during execution of your command"));
			throw e;
		}
	}

	private static abstract interface ListenerRegistration {

		void cancel();
	}

	private class ByteListenerRegistration implements ByteListener, ListenerRegistration {

		private final Server server;
		private final int id;
		private final ChannelHandlerContext ctx;

		public ByteListenerRegistration(Server server, int id, ChannelHandlerContext ctx) {
			this.server = server;
			this.id = id;
			this.ctx = ctx;
		}

		@Override
		public void cancel() {
			server.removeByteListener(this);
		}

		@Override
		public void onIncomingBytes(byte[] bytes, int start, int end) {
			this.ctx.writeAndFlush(new StreamingDataResponse(true, id, new String(bytes, start, end - start), true));
		}

	}

	private class StateListenerRegistration implements StateListener, ListenerRegistration {

		private final Server server;
		private final int id;
		private final ChannelHandlerContext ctx;

		public StateListenerRegistration(Server server, int id, ChannelHandlerContext ctx) {
			this.server = server;
			this.id = id;
			this.ctx = ctx;
		}

		@Override
		public void cancel() {
			server.removeStateListener(this);
		}

		@Override
		public void onStateChange(State newState) {
			this.ctx.writeAndFlush(new StreamingDataResponse(true, id, newState, true));
		}

	}
}
