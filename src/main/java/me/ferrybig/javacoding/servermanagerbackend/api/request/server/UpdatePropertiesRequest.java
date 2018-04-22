/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.api.request.server;

import java.util.Map;

public class UpdatePropertiesRequest extends ServerRequest {

	private Map<String, Map<String, String>> properties;

	public UpdatePropertiesRequest() {
		super(Type.SERVER_UPDATE_PROPERTIES);
	}

	public Map<String, Map<String, String>> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Map<String, String>> properties) {
		this.properties = properties;
	}

}
