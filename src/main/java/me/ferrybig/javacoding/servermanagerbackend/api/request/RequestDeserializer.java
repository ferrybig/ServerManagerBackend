/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.api.request;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

/**
 *
 * @author Fernando van Loenhout
 */
public class RequestDeserializer implements JsonDeserializer<Request> {

	@Override
	public Request deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		Request.Type type = context.deserialize(json.getAsJsonObject().get("type"), Request.Type.class);
		return context.deserialize(json, type.getType());
	}

}
