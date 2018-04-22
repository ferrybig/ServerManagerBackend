/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.routing.routes;

import me.ferrybig.javacoding.servermanagerbackend.api.request.server.InfoRequest;
import me.ferrybig.javacoding.servermanagerbackend.api.response.InstantResponse;
import me.ferrybig.javacoding.servermanagerbackend.internal.Server;
import me.ferrybig.javacoding.servermanagerbackend.internal.ServerManager;
import me.ferrybig.javacoding.servermanagerbackend.routing.Client;

public class ServerInfoHandler extends ServerHandler<InfoRequest> {

	public ServerInfoHandler(ServerManager manager) {
		super(manager);
	}

	@Override
	public void handleRequest(Client client, InfoRequest req, Server server) {
		client.getOutgoing().writeResponse(new InstantResponse(true, req, server.getConfig()));
	}

}
