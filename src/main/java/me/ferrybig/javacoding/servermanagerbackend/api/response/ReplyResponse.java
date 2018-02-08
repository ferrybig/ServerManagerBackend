/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.api.response;

import com.google.gson.annotations.Expose;
import me.ferrybig.javacoding.servermanagerbackend.api.request.Request;

/**
 *
 * @author Fernando van Loenhout
 */
public abstract class ReplyResponse extends Response {
	@Expose
	public Request request;

	public ReplyResponse(boolean success, Type type) {
		super(success, type);
		this.type = type;
	}

	public ReplyResponse(boolean success, Request request, Type type) {
		this(success, type);
		this.success = success;
		this.request = request;
	}

}
