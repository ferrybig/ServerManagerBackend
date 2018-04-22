/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.routing.routes;

import java.util.LinkedHashMap;
import java.util.Map;
import me.ferrybig.javacoding.servermanagerbackend.api.request.server.ListRequest;
import me.ferrybig.javacoding.servermanagerbackend.api.response.InstantResponse;
import me.ferrybig.javacoding.servermanagerbackend.internal.Server;
import me.ferrybig.javacoding.servermanagerbackend.internal.ServerManager;
import me.ferrybig.javacoding.servermanagerbackend.internal.config.DefaultConfigKeys;
import me.ferrybig.javacoding.servermanagerbackend.routing.Client;
import me.ferrybig.javacoding.servermanagerbackend.routing.Handler;

public class ServerListHandler implements Handler<ListRequest> {

	private final ServerManager manager;

	public ServerListHandler(ServerManager manager) {
		this.manager = manager;
	}

	@Override
	public void handleRequest(Client client, ListRequest req) {
		Map<String, Map<String, Object>> result = new LinkedHashMap<>();
		for (Map.Entry<String, Server> server : this.manager.getAllServers().entrySet()) {
			Map<String, Object> perServer = new LinkedHashMap<>();
			Server serverInstance = server.getValue();
			perServer.put("state", serverInstance.getState());
			perServer.put("name", serverInstance.getConfig().getValue(DefaultConfigKeys.DISPLAY_NAME).orElse(server.getKey()));
			perServer.put("host", serverInstance.getConfig().getValue(DefaultConfigKeys.VIRTUAL_HOST_IP).orElse("[::]"));
			perServer.put("port", serverInstance.getConfig().getValue(DefaultConfigKeys.VIRTUAL_HOST_PORT).orElse(0));
			result.put(server.getKey(), perServer);
		}
		client.getOutgoing().writeResponse(new InstantResponse(true, req, result));
	}

}
