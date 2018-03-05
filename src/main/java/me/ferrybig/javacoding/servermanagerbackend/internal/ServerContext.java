/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.ferrybig.javacoding.servermanagerbackend.internal;

import me.ferrybig.javacoding.servermanagerbackend.util.LogRecorder;

public class ServerContext {
	private final LogRecorder recorder;
	private boolean shutdownRequested = false;

	public ServerContext(LogRecorder recorder) {
		this.recorder = recorder;
	}

	public void setRunning() {

	}

	public enum NativeAction {
		START,
		STOP,
		SUSPEND,
		WAKEUP,
		CREATE,
		DESTROY
	}

	public interface NativeServerHandler extends StateNotifier {
		void dispatchAction(NativeAction action);
	}

}
