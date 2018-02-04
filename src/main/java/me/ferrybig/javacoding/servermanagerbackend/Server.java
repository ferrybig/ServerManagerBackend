/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.ferrybig.javacoding.servermanagerbackend;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

public class Server {
	private final ServerConfig config;
	private final LogRecorder logFile;
	private final ProcessWatcher processWatcher;

	public Server(ServerConfig config, ExecutorService threadpool) {
		this.config = config;
		this.logFile = new LogRecorder();
		this.processWatcher = new ProcessWatcher(logFile, threadpool);
	}

	public void start() throws IOException {
		this.processWatcher.start(this.config.getCommandLine(), this.config.getDirectory());
	}

	public void stop() throws IOException {
		this.processWatcher.kill();
	}

	public long addByteListener(ByteListener listener) {
		return logFile.addByteListener(listener);
	}

	public synchronized long addByteListener(ByteListener listener, boolean sendBuffer) {
		return logFile.addByteListener(listener, sendBuffer);
	}

	public synchronized void removeByteListener(ByteListener listener) {
		logFile.removeByteListener(listener);
	}

	public ServerConfig getConfig() {
		return config;
	}

	public LogRecorder getLogFile() {
		return logFile;
	}


}
