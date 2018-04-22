/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.routing.routes;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import me.ferrybig.javacoding.servermanagerbackend.api.request.server.ChannelRequest;
import me.ferrybig.javacoding.servermanagerbackend.api.request.server.InfoRequest;
import me.ferrybig.javacoding.servermanagerbackend.api.response.InstantResponse;
import me.ferrybig.javacoding.servermanagerbackend.api.response.StreamingDataResponse;
import me.ferrybig.javacoding.servermanagerbackend.api.response.StreamingResponse;
import me.ferrybig.javacoding.servermanagerbackend.internal.ByteListener;
import me.ferrybig.javacoding.servermanagerbackend.internal.Server;
import me.ferrybig.javacoding.servermanagerbackend.internal.ServerManager;
import me.ferrybig.javacoding.servermanagerbackend.internal.StateListener;
import me.ferrybig.javacoding.servermanagerbackend.websocket.WebSocketFrameHandler;
import me.ferrybig.javacoding.servermanagerbackend.routing.Client;
import me.ferrybig.javacoding.servermanagerbackend.routing.OutboundChannel;
import me.ferrybig.javacoding.servermanagerbackend.routing.OutboundChannel.OutgoingInteraction;

public class ServerListenHandler extends ServerHandler<ChannelRequest> {

	public ServerListenHandler(ServerManager manager) {
		super(manager);
	}

	@Override
	public void handleRequest(Client client, ChannelRequest req, Server server) {
		if ("console".equals(req.channelName)) {
			OutgoingInteraction interaction = client.getOutgoing().startStream(req);
			ByteListenerRegistration registration = new ByteListenerRegistration(server, interaction);

			server.addByteListener(registration, true);
			interaction.addDoneListener(registration);
		} else if ("state".equals(req.channelName)) {
			OutgoingInteraction interaction = client.getOutgoing().startStream(req);
			StateListenerRegistration registration = new StateListenerRegistration(server, interaction);

			server.addStateListener(registration);
			interaction.addDoneListener(registration);
		} else {
			client.getOutgoing().writeResponse(new InstantResponse(false, req, "Reqeust channel not implemented"));
		}
	}

	private class ByteListenerRegistration implements ByteListener, GenericFutureListener<Future<Object>> {

		private final Server server;
		private final OutgoingInteraction interaction;

		public ByteListenerRegistration(Server server, OutgoingInteraction interaction) {
			this.server = server;
			this.interaction = interaction;
		}

		@Override
		public void onIncomingBytes(byte[] bytes, int start, int end) {
			interaction.onStream(true, new String(bytes, start, end - start));
		}

		@Override
		public void operationComplete(Future<Object> future) throws Exception {
			server.removeByteListener(this);
		}

	}

	private class StateListenerRegistration implements StateListener, GenericFutureListener<Future<Object>> {

		private final Server server;
		private final OutgoingInteraction interaction;

		public StateListenerRegistration(Server server, OutgoingInteraction interaction) {
			this.server = server;
			this.interaction = interaction;
		}

		@Override
		public void operationComplete(Future<Object> future) throws Exception {
			server.removeStateListener(this);
		}

		@Override
		public void onStateChange(StateListener.State newState) {
			interaction.onStream(true, newState);
		}

	}

}
