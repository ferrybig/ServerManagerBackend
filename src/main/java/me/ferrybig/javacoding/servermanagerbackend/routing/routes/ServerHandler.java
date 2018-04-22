/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.routing.routes;

import me.ferrybig.javacoding.servermanagerbackend.api.request.server.ServerRequest;
import me.ferrybig.javacoding.servermanagerbackend.internal.ServerManager;

public abstract class ServerHandler<T extends ServerRequest> extends ServerBasedHandler<T> {

	public ServerHandler(ServerManager manager) {
		super(req -> req.server, manager);
	}

}
