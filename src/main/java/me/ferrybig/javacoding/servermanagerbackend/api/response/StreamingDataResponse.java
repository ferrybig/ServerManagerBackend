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
public class StreamingDataResponse extends Response {

	@Expose
	public final int id;

	@Expose
	public final Object data;

	@Expose
	public final boolean moreData;

	public StreamingDataResponse(boolean success, int id, Object data) {
		this(success, id, data, true);
	}

	public StreamingDataResponse(boolean success, int id, Object data, boolean moreData) {
		super(success, Type.STREAM_DATA);
		this.id = id;
		this.data = data;
		this.moreData = moreData;
	}
}
