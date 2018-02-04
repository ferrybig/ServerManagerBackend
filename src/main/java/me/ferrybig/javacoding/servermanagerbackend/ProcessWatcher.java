/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

class ProcessWatcher {

	private final ByteListener listener;
	private Process process;
	private final ExecutorService threadpool;
	private BufferedWriter out = null;

	public ProcessWatcher(ByteListener listener, ExecutorService threadpool) {
		this.listener = listener;
		this.threadpool = threadpool;
	}

	public synchronized void start(List<String> commandLine, String directory) throws IOException {
		if (process != null) {
			throw new IllegalStateException("Process already running");
		}
		ProcessBuilder builder = new ProcessBuilder(commandLine);
		builder.directory(new File(directory));
		builder.redirectErrorStream(true);
		Process process = builder.start();
		this.process = process;
		InputStream in = this.process.getInputStream();
		threadpool.submit(() -> {
			try {
				try {
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
					}
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
					Logger.getLogger(ProcessWatcher.class.getName()).log(Level.SEVERE, null, ex);
				}
			} finally {
				synchronized(this) {
					this.process = null;
				}
			}
		});
	}

	public synchronized void kill() {
		if (process == null) {
			throw new IllegalStateException("Process already stopped");
		}
		this.process.destroy();
	}
}
