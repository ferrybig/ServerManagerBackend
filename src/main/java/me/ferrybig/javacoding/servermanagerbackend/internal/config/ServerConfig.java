/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.internal.config;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author Fernando van Loenhout
 */
public class ServerConfig {

	private final Map<ConfigKey<?>, String> config;
	private final Map<String, Map<String, ConfigKey<?>>> keyMapping;

	public ServerConfig(Map<ConfigKey<?>, String> config, Map<String, Map<String, ConfigKey<?>>> keyMapping) {
		this.config = config;
		this.keyMapping = keyMapping;
	}

	public <T> Optional<T> getValue(ConfigKey<? extends T> key) {
		String value = this.config.get(key);
		if (value == null) {
			return Optional.empty();
		}
		return Optional.of(key.getFormat().convert(value));
	}

	public synchronized <T> void setValue(ConfigKey<T> key, T value) {
		if (!config.containsKey(key)) {
			throw new IllegalArgumentException("Key " + key + " Does not exists on this map");
		}
		this.config.put(key, key.getFormat().deconvert(value));
	}

	public void setValuesFromRequest(Map<String, Map<String, String>> map) {
		List<Map.Entry<ConfigKey<?>, String>> list = new ArrayList<>(map.size());
		for (Map.Entry<String, Map<String, String>> group : map.entrySet()) {
			Map<String, ConfigKey<?>> groupKeyMapping = keyMapping.get(group.getKey());
			if (groupKeyMapping == null) {
				throw new IllegalArgumentException("Key '" + group.getKey() + "' does not exists for this config");
			}
			for (Map.Entry<String, String> entry : group.getValue().entrySet()) {
				ConfigKey<?> configKey = groupKeyMapping.get(entry.getKey());

				if (configKey == null) {
					throw new IllegalArgumentException("Key '" + group.getKey() + "'.'" + entry.getKey() + "' does not exists for this config");
				}
				if (!configKey.getFormat().validate(entry.getValue())) {
					throw new IllegalArgumentException("Key '" + group.getKey() + "'.'" + entry.getKey() + "' doesn't validate for config key: " + configKey);
				}
				list.add(new AbstractMap.SimpleImmutableEntry<>(configKey, entry.getValue()));
			}
		}
		synchronized (this) {
			for (Map.Entry<ConfigKey<?>, String> entry : list) {
				this.config.put(entry.getKey(), entry.getValue());
			}
		}
	}

	public static TypeAdapter<ServerConfig> typeAdapter(Gson gson) {
		TypeAdapter<ConfigKey<?>> keyAdaptor = ConfigKey.typeAdapter(gson);

		return new TypeAdapter<ServerConfig>() {
			@Override
			public void write(JsonWriter out, ServerConfig value) throws IOException {
				out.beginObject();
				out.name("format");
				out.beginObject();

				for (Map.Entry<String, Map<String, ConfigKey<?>>> group : value.keyMapping.entrySet()) {
					out.name(group.getKey());
					out.beginObject();

					for (Map.Entry<String, ConfigKey<?>> entry : group.getValue().entrySet()) {
						out.name(entry.getKey());
						keyAdaptor.write(out, entry.getValue());
					}
					out.endObject();
				}
				out.endObject();
				out.name("values");
				out.beginObject();
				for (Map.Entry<String, Map<String, ConfigKey<?>>> group : value.keyMapping.entrySet()) {
					out.name(group.getKey());
					out.beginObject();
					for (Map.Entry<String, ConfigKey<?>> entry : group.getValue().entrySet()) {
						out.name(entry.getKey());
						out.value(value.config.get(entry.getValue()));
					}
					out.endObject();
				}
				out.endObject();
				out.endObject();
			}

			@Override
			public ServerConfig read(JsonReader in) throws IOException {
				throw new IOException("ConfigFormat cannot be read yet");
			}

		}.nullSafe();
	}

}
