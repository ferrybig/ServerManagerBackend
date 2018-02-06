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
public class DelayedResponse extends ReplyResponse {

	@Expose
	public int id;

	public DelayedResponse(boolean success, Request request, int id) {
		super(success, request, ReplyResponse.Type.DELAYED);
		this.id = id;
	}
}
