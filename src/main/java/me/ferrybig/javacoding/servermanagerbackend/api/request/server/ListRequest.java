/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.api.request.server;

import com.google.gson.annotations.SerializedName;
import me.ferrybig.javacoding.servermanagerbackend.api.request.Request;

/**
 *
 * @author Fernando van Loenhout
 */
public class ListRequest extends Request {

	@SerializedName("object_type")
	public ObjectType objectType;

	public ListRequest() {
		super(Type.SERVER_LIST);
	}

	public enum ObjectType {
		@SerializedName("server")
		SERVER,
	}

}
