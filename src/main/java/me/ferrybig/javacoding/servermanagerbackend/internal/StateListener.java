/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.internal;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Fernando
 */
public interface StateListener {

	void onStateChange(State newState);

	public enum State {

		@SerializedName("crashed")
		CRASHED,
		@SerializedName("stopped")
		STOPPED,
		@SerializedName("started")
		STARTED,
		@SerializedName("prepare_start")
		PREPARE_START,
	}
}
