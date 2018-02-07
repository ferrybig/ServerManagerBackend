/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.api.request;

import com.google.gson.annotations.Expose;

/**
 *
 * @author Fernando van Loenhout
 */
public abstract class Request {

	@Expose
	public final Type type;

	@Expose
	public String server;

	public Request(Type type) {
		this.type = type;
	}

	public enum Type {
		ACTION(ActionRequest.class),
		INFO(InfoRequest.class),
		REGISTER_CHANNEL(ChannelRequest.class),
		KILL_STREAM(KillStreamRequest.class);

		private final Class<? extends Request> type;

		Type(Class<? extends Request> type) {
			this.type = type;
		}

		public Class<? extends Request> getType() {
			return type;
		}

	}
}
