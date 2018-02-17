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

/**
 *
 * @author Fernando van Loenhout
 */
public interface ConfigKey<T> {

	boolean allowsEmptyValue();

	String getDescription();

	String getGroup();

	String getName();

	ConfigFormat<T> getFormat();

	public static TypeAdapter<ConfigKey<?>> typeAdapter(Gson gson) {
		TypeAdapter<ConfigFormat<?>> formatAdaptor = ConfigFormat.typeAdapter(gson);

		return new TypeAdapter<ConfigKey<?>>() {
			@Override
			public ConfigKey<?> read(JsonReader in) throws IOException {
				throw new IOException("ConfigKey cannot be read");
			}

			@Override
			public void write(JsonWriter out, ConfigKey<?> value) throws IOException {
				out.beginObject();
				out.name("group").value(value.getGroup());
				out.name("name").value(value.getName());
				out.name("description").value(value.getDescription());
				out.name("allowsEmptyValue").value(value.allowsEmptyValue());
				out.name("format");
				formatAdaptor.write(out, value.getFormat());
				out.endObject();
			}

		}.nullSafe();
	}
}
