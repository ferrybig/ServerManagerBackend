/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.routing.routes;

import java.io.IOException;
import me.ferrybig.javacoding.servermanagerbackend.api.request.server.ActionRequest;
import me.ferrybig.javacoding.servermanagerbackend.api.response.InstantResponse;
import me.ferrybig.javacoding.servermanagerbackend.internal.Server;
import me.ferrybig.javacoding.servermanagerbackend.internal.ServerManager;
import me.ferrybig.javacoding.servermanagerbackend.routing.Client;

public class ServerActionHandler extends ServerHandler<ActionRequest> {

	public ServerActionHandler(ServerManager manager) {
		super(manager);
	}

	@Override
	public void handleRequest(Client client, ActionRequest req, Server server) {
		switch (req.action) {
			case START: {
				try {
					client.getOutgoing().writeResponse(new InstantResponse(true, req, "Starting server... " + server.tryStart()));
				} catch (IOException ex) {
					ex.printStackTrace();
					client.getOutgoing().writeResponse(new InstantResponse(false, req, "Error during start of server!"));
				}
			}
			break;
			case KILL: {
				client.getOutgoing().writeResponse(new InstantResponse(true, req, "Stopping server... " + server.tryKill()));
			}
			break;
			case SEND_COMMAND: {
				server.sendMessage(req.arguments);
				client.getOutgoing().writeResponse(new InstantResponse(true, req, "Sending command..."));
			}
			break;
			default: {
				client.getOutgoing().writeResponse(new InstantResponse(false, req, "Reqeust action not implemented"));
			}
			break;
		}
	}

}
