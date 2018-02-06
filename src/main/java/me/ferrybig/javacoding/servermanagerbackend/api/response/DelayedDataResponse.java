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
public class DelayedDataResponse extends Response {

	@Expose
	public final int id;

	@Expose
	public final Object data;

	public DelayedDataResponse(boolean success, int id, Object data) {
		super(success, Type.DELAYED_DATA);
		this.id = id;
		this.data = data;
	}

}
