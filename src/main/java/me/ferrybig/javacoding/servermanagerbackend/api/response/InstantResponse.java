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
public class InstantResponse extends ReplyResponse {

	@Expose
	public Object data;

	public InstantResponse(boolean success, Request request, Object data) {
		super(success, request, ReplyResponse.Type.INSTANT);
		this.data = data;
	}
}
