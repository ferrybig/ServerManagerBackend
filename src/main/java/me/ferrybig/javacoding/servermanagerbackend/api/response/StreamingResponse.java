/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.api.response;

import me.ferrybig.javacoding.servermanagerbackend.api.request.Request;

/**
 *
 * @author Fernando van Loenhout
 */
public class StreamingResponse extends ReplyResponse {

	public StreamingResponse(boolean success, Request request) {
		super(success, request, Type.STREAM);
	}

}
