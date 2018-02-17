/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.internal.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ServerConfigBuilder {

	private final Map<ConfigKey<?>, String> config = new HashMap<>();

	public <T> Optional<T> getValue(ConfigKey<? extends T> key) {
		String value = this.config.get(key);
		if (value == null) {
			return Optional.empty();
		}
		return Optional.of(key.getFormat().convert(value));
	}

	public <T> ServerConfigBuilder setValue(ConfigKey<T> key, T value) {
		this.config.put(key, key.getFormat().deconvert(value));
		return this;
	}

	public ServerConfig build() {

		Map<String, Map<String, ConfigKey<?>>> keyMapping = config.keySet().stream().collect(
			Collectors.groupingBy(
				ConfigKey::getGroup,
				Collectors.toMap(
					ConfigKey::getName,
					Function.identity()
				)
			)
		);
		return new ServerConfig(
			new ConcurrentHashMap<>(config),
			keyMapping
		);
	}
}
