/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.ferrybig.javacoding.servermanagerbackend.internal;

import io.netty.util.concurrent.ScheduledFuture;
import me.ferrybig.javacoding.servermanagerbackend.util.LogRecorder;

public class ServerContext {
	private final LogRecorder recorder;
	private boolean shutdownRequested = false;
	private boolean startupRequested = false;
	private ScheduledFuture<?> forceStopTask;


	public ServerContext(LogRecorder recorder) {
		this.recorder = recorder;
	}

	public void setRequestedState(RequestState state) {

	}
	
	public boolean isRunning() {
		return false;
	}

	public void sendCommand(String cmd) {

	}

	public enum RequestState {
		RUNNING,
		RESTARTING,
		STOPPED,
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
