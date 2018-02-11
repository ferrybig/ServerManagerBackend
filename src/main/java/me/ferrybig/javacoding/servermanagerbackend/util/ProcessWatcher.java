/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.ferrybig.javacoding.servermanagerbackend.internal.ByteListener;
import me.ferrybig.javacoding.servermanagerbackend.internal.StateListener;
import me.ferrybig.javacoding.servermanagerbackend.internal.StateListener.State;

public class ProcessWatcher {

	private final ByteListener listener;
	private final Object lock;
	private Process process;
	private final StateListener stateListener;
	private final ExecutorService threadpool;
	private PrintStream out = null;

	public ProcessWatcher(ByteListener listener, StateListener stateListener, ExecutorService threadpool, Object lock) {
		this.listener = listener;
		this.stateListener = stateListener;
		this.threadpool = threadpool;
		this.lock = lock;
	}

	public void start(List<String> commandLine, String directory) throws IOException {
		if (!tryStart(commandLine, directory)) {
			throw new IllegalStateException("Process already running");
		}
	}

	public void onStateChange(State state) {
		this.stateListener.onStateChange(state);
	}

	public boolean tryStart(List<String> commandLine, String directory) throws IOException {
		if (process != null) {
			return false;
		}
		ProcessBuilder builder = new ProcessBuilder(commandLine);
		builder.directory(new File(directory));
		builder.redirectErrorStream(true);
		Process process = builder.start();
		this.process = process;
		InputStream in = this.process.getInputStream();
		this.out = new PrintStream(process.getOutputStream(), true, "utf-8");
		onStateChange(State.PREPARE_START);
		threadpool.submit(() -> {
			try {
				try {
					onStateChange(State.STARTED);
					int length;
					byte[] buffer = new byte[1024];
					while ((length = in.read(buffer)) > 0) {
						listener.onIncomingBytes(buffer, 0, length);
					}
				} catch (IOException ex) {
					Logger.getLogger(ProcessWatcher.class.getName()).log(Level.SEVERE, null, ex);
				} finally {
					try {
						in.close();
					} catch (IOException ex) {
						Logger.getLogger(ProcessWatcher.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
				try {
					int code = process.waitFor();
					if (code != 0) {
						byte[] message = ("\n\nProcess exited with code: " + code + "\n\n").getBytes(StandardCharsets.UTF_8);
						listener.onIncomingBytes(message, 0, message.length);
						onStateChange(State.CRASHED);
					} else {
						onStateChange(State.STOPPED);
					}
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
					Logger.getLogger(ProcessWatcher.class.getName()).log(Level.SEVERE, null, ex);
				}
			} finally {
				synchronized (lock) {
					this.process = null;
					this.out = null;
				}
			}
		});
		return true;
	}

	public void kill() {
		if (!tryKill()) {
			throw new IllegalStateException("Process already stopped");
		}
	}

	public boolean tryKill() {
		if (process == null) {
			return false;
		}
		this.process.destroy();
		return true;
	}

	public boolean trySendMessage(String cmd) {
		if (this.out == null) {
			return false;
		}
		this.out.println(cmd);
		return true;
	}

	public void sendMessage(String cmd) {
		if (!trySendMessage(cmd)) {
			throw new IllegalStateException("Pipe closed");
		}
	}
}
