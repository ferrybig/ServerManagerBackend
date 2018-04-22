/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend;

import java.util.concurrent.ExecutorService;
import me.ferrybig.javacoding.servermanagerbackend.api.request.KillStreamRequest;
import me.ferrybig.javacoding.servermanagerbackend.api.request.Request;
import me.ferrybig.javacoding.servermanagerbackend.api.request.server.ActionRequest;
import me.ferrybig.javacoding.servermanagerbackend.api.request.server.ChannelRequest;
import me.ferrybig.javacoding.servermanagerbackend.api.request.server.InfoRequest;
import me.ferrybig.javacoding.servermanagerbackend.api.request.server.ListRequest;
import me.ferrybig.javacoding.servermanagerbackend.api.request.server.UpdatePropertiesRequest;
import me.ferrybig.javacoding.servermanagerbackend.internal.ServerManager;
import me.ferrybig.javacoding.servermanagerbackend.routing.Client;
import me.ferrybig.javacoding.servermanagerbackend.routing.Handler;
import me.ferrybig.javacoding.servermanagerbackend.routing.routes.KillStreamHandler;
import me.ferrybig.javacoding.servermanagerbackend.routing.TypeHandler;
import me.ferrybig.javacoding.servermanagerbackend.routing.routes.ServerActionHandler;
import me.ferrybig.javacoding.servermanagerbackend.routing.routes.ServerInfoHandler;
import me.ferrybig.javacoding.servermanagerbackend.routing.routes.ServerListHandler;
import me.ferrybig.javacoding.servermanagerbackend.routing.routes.ServerListenHandler;
import me.ferrybig.javacoding.servermanagerbackend.routing.routes.ServerUpdateProperties;

public class MasterServer implements Handler<Request> {

	private final ServerManager manager;
	private final ExecutorService taskGroup;
	private final Handler<Request> mainRouter;

	private MasterServer(ExecutorService taskGroup) {
		this(new ServerManager(taskGroup), taskGroup);
	}

	public MasterServer(ServerManager manager, ExecutorService taskGroup) {
		this.manager = manager;
		this.taskGroup = taskGroup;
		this.mainRouter = makeRouter(manager, taskGroup);
	}

	@Override
	public void handleRequest(Client client, Request req) {
		this.mainRouter.handleRequest(client, req);
	}

	private static Handler<Request> makeRouter(ServerManager manager, ExecutorService taskGroup) {
		return new TypeHandler<>()
			.addRoute(KillStreamRequest.class, new KillStreamHandler())
			.addRoute(ListRequest.class, new ServerListHandler(manager))
			.addRoute(ActionRequest.class, new ServerActionHandler(manager))
			.addRoute(ChannelRequest.class, new ServerListenHandler(manager))
			.addRoute(UpdatePropertiesRequest.class, new ServerUpdateProperties(manager))
			.addRoute(InfoRequest.class, new ServerInfoHandler(manager));
	}

	public ServerManager getManager() {
		return manager;
	}

	public ExecutorService getTaskGroup() {
		return taskGroup;
	}
}
