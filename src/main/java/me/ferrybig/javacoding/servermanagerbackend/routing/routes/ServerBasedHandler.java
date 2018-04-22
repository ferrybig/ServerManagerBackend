/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.routing.routes;

import java.util.Objects;
import java.util.function.Function;
import me.ferrybig.javacoding.servermanagerbackend.api.request.Request;
import me.ferrybig.javacoding.servermanagerbackend.api.response.InstantResponse;
import me.ferrybig.javacoding.servermanagerbackend.internal.Server;
import me.ferrybig.javacoding.servermanagerbackend.internal.ServerManager;
import me.ferrybig.javacoding.servermanagerbackend.routing.Client;
import me.ferrybig.javacoding.servermanagerbackend.routing.Handler;

public abstract class ServerBasedHandler<T extends Request> implements Handler<T> {

	protected final ServerManager manager;

	private final Function<? super T, String> serverConvertor;

	public ServerBasedHandler(Function<? super T, String> serverConvertor, ServerManager manager) {
		this.serverConvertor = Objects.requireNonNull(serverConvertor, "serverConvertor");
		this.manager = Objects.requireNonNull(manager, "manager");
	}

	@Override
	public void handleRequest(Client client, T req) {
		Server server = manager.getServer(serverConvertor.apply(req));
		if (server == null) {
			handleMissingServerRequest(client, req);
		} else {
			handleRequest(client, req, server);
		}
	}

	public abstract void handleRequest(Client client, T req, Server server);

	public void handleMissingServerRequest(Client client, T req) {
		client.getOutgoing().writeResponse(new InstantResponse(false, req, "Server not found"));
	}

}
