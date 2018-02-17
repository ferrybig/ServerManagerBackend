/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.internal;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import me.ferrybig.javacoding.servermanagerbackend.internal.config.ServerConfig;

public class ServerManager {

	private final ConcurrentMap<String, Server> servers = new ConcurrentHashMap<>();
	private final Object changeLock = new Object();
	private final ExecutorService taskGroup;

	public ServerManager(ExecutorService taskGroup) {
		this.taskGroup = taskGroup;
	}

	public Server getServer(String id) {
		return servers.get(id);
	}

	public Server createServer(String id, ServerConfig config) {
		synchronized (changeLock) {
			Server server = servers.get(id);
			if (server != null) {
				throw new IllegalArgumentException("Server already exists with id");
			}
			server = new Server(config, taskGroup);
			servers.put(id, server);
			return server;
		}
	}

	public boolean deleteServer(String id) {
		synchronized (changeLock) {
			Server server = servers.get(id);
			if (server == null) {
				return false;
			}
			synchronized (server) {
				server.tryKill();
				server.lock();
			}
			servers.remove(id);
		}
		return true;
	}
}
