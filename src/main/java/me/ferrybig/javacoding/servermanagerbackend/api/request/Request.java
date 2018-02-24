/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.api.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import me.ferrybig.javacoding.servermanagerbackend.api.request.server.ListRequest;
import me.ferrybig.javacoding.servermanagerbackend.api.request.server.ActionRequest;
import me.ferrybig.javacoding.servermanagerbackend.api.request.server.ChannelRequest;
import me.ferrybig.javacoding.servermanagerbackend.api.request.server.InfoRequest;
import me.ferrybig.javacoding.servermanagerbackend.api.request.server.UpdatePropertiesRequest;

/**
 *
 * @author Fernando van Loenhout
 */
public abstract class Request {

	@Expose
	public final Type type;

	public Request(Type type) {
		this.type = type;
	}

	public enum Type {
		@SerializedName("server_list")
		SERVER_LIST(ListRequest.class),
		@SerializedName("server_action")
		SERVER_ACTION(ActionRequest.class),
		@SerializedName("server_info")
		SERVER_INFO(InfoRequest.class),
		@SerializedName("server_update_properties")
		SERVER_UPDATE_PROPERTIES(UpdatePropertiesRequest.class),
		@SerializedName("server_listen")
		SERVER_LISTEN(ChannelRequest.class),
		@SerializedName("kill_stream")
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
