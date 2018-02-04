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
	private volatile boolean locked = false;

	public Server(ServerConfig config, ExecutorService threadpool) {
		this.config = config;
		this.logFile = new LogRecorder();
		this.processWatcher = new ProcessWatcher(logFile, threadpool, this);
	}

	public synchronized void start() throws IOException {
		if (locked) {
			throw new IllegalStateException("Server locked");
		}
		this.processWatcher.start(this.config.getCommandLine(), this.config.getDirectory());
	}

	public synchronized boolean tryStart() throws IOException {
		if (locked) {
			return false;
		}
		return this.processWatcher.tryStart(this.config.getCommandLine(), this.config.getDirectory());
	}

	public synchronized void kill() throws IOException {
		if (locked) {
			throw new IllegalStateException("Server locked");
		}
		this.processWatcher.kill();
	}

	public synchronized boolean tryKill() {
		if (locked) {
			return false;
		}
		return this.processWatcher.tryKill();
	}

	public synchronized void lock() {
		this.locked = true;
	}

	public synchronized void unlock() {
		this.locked = false;
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

	public synchronized void sendMessage(String cmd) {
		processWatcher.sendMessage(cmd);
	}

}