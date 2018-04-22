/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import me.ferrybig.javacoding.servermanagerbackend.internal.ServerManager;
import me.ferrybig.javacoding.servermanagerbackend.internal.config.DefaultConfigKeys;
import me.ferrybig.javacoding.servermanagerbackend.internal.config.ServerConfigBuilder;
import me.ferrybig.javacoding.servermanagerbackend.websocket.WebSocketServer;

public class Main {

	public static void main(String[] args) {

		ExecutorService taskGroup = Executors.newCachedThreadPool(Executors.defaultThreadFactory());

		ServerManager serverManager = new ServerManager(taskGroup);

		serverManager.createServer("test", new ServerConfigBuilder()
			.setValue(
				DefaultConfigKeys.START_COMMAND,
				Arrays.asList("java", "-jar", "C:\\Users\\fernando\\Downloads\\mc\\spigot-1.12.2.jar"))
			.setValue(
				DefaultConfigKeys.WORKING_DIRECTORY,
				"C:\\Users\\fernando\\Downloads\\mc")
			.setValue(
				DefaultConfigKeys.SHUTDOWN_COMMAND,
				Arrays.asList("say server shutting down", "stop"))
			.setValue(
				DefaultConfigKeys.VIRTUAL_HOST_PORT,
				25565)
			.build()
		);
		serverManager.createServer("test1", new ServerConfigBuilder()
			.setValue(
				DefaultConfigKeys.START_COMMAND,
				Arrays.asList("java", "-jar", "D:\\Servers-Active\\Test - kopie (2)\\spigot-1.12.jar"))
			.setValue(
				DefaultConfigKeys.WORKING_DIRECTORY,
				"D:\\Servers-Active\\Test - kopie (2)")
			.setValue(
				DefaultConfigKeys.SHUTDOWN_COMMAND,
				Arrays.asList("say server shutting down", "stop"))
			.setValue(
				DefaultConfigKeys.VIRTUAL_HOST_PORT,
				25565)
			.build()
		);

		MasterServer server = new MasterServer(serverManager, taskGroup);

		WebSocketServer web = new WebSocketServer(server);

		web.startServer(8070);

		System.out.println("Api served on ws://localhost:8070/websocket");
	}
}
