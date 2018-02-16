/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.internal.config;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Fernando van Loenhout
 */
public class ServerConfig {
	private final Map<ConfigKey<?>, String> config = new HashMap<>();
	private final Map<String, ConfigKey<?>> keyMapping = new HashMap<>();

	public <T> T getValue(ConfigKey<? extends T> key) {
		return key.getFormat().convert(this.config.get(key));
	}


}
