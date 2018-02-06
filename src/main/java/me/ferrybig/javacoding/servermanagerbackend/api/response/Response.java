/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.api.response;

import com.google.gson.annotations.Expose;

/**
 *
 * @author Fernando van Loenhout
 */
public class Response {

	@Expose
	public boolean success;

	@Expose
	public ReplyResponse.Type type;

	public Response(boolean success, Type type) {
		this.success = success;
		this.type = type;
	}

	public enum Type {
		INSTANT,
		DELAYED,
		STREAM,
		STREAM_DATA,
		STREAM_END,
		DELAYED_DATA,
	}
}
