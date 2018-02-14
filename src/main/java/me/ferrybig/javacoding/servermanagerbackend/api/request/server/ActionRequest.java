/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.api.request.server;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Fernando van Loenhout
 */
public class ActionRequest extends ServerRequest {

	@Expose
	public Action action;
	@Expose
	public String arguments;

	public ActionRequest() {
		super(Type.SERVER_ACTION);
	}

	public enum Action {
		@SerializedName("start")
		START,
		@SerializedName("kill")
		KILL,
		@SerializedName("send_command")
		SEND_COMMAND
	}
}
