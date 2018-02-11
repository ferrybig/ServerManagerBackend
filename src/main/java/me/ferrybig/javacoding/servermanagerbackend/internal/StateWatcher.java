/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.internal;

import java.util.concurrent.CopyOnWriteArrayList;

public class StateWatcher implements StateListener {

	public volatile State state = State.STOPPED;
	private final CopyOnWriteArrayList<StateListener> listeners = new CopyOnWriteArrayList<>();

	@Override
	public void onStateChange(State newState) {
		this.state = newState;
		for (StateListener listener : listeners) {
			listener.onStateChange(newState);
		}

	}

	public void addListener(StateListener listener) {
		listener.onStateChange(state);
		listeners.add(listener);
	}

	public void removeListener(StateListener listener) {
		listeners.remove(listener);
	}

}
