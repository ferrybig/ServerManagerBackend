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
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * @author Fernando van Loenhout
 */
public class ConfigFormat<T> {

	public static final ConfigFormat<String> STRING = new ConfigFormat<>(
		Pattern.compile("[^\n]*"), "string", "", Function.identity());
	
	public static final ConfigFormat<List<String>> STRING_LIST = new ConfigFormat<>(
		Pattern.compile("[^\0\n]*(\0[^\0\n]*)*"), "list", "string",
		s -> Arrays.asList(s.split("\0")),
		a -> a.stream().collect(Collectors.joining("\0")));

	public static final ConfigFormat<String> SERVER_FILE = new ConfigFormat<>(
		Pattern.compile("[^\n]*"), "string", "server_file", Function.identity());

	public static final ConfigFormat<Integer> PORT = new ConfigFormat<>(
		Pattern.compile("-?\\d+"), "ip", "port", Integer::parseInt);

	public static final ConfigFormat<String> IP_ADDRESS = new ConfigFormat<>(
		Pattern.compile("(\\[[0-9a-fA-F:]+\\]|[0-9.]+)"), "ip", "address", Function.identity());

	public static final ConfigFormat<Double> DOUBLE = new ConfigFormat<>(
		Pattern.compile("-?\\d*(\\.\\d+)?"), "number", "double", Double::parseDouble);

	public static final ConfigFormat<Float> FLOAT = new ConfigFormat<>(
		Pattern.compile("-?\\d*(\\.\\d+)?"), "number", "float", Float::parseFloat);

	public static final ConfigFormat<Integer> INTEGER = new ConfigFormat<>(
		Pattern.compile("-?\\d+"), "number", "int", Integer::parseInt);

	public static final ConfigFormat<Long> LONG = new ConfigFormat<>(
		Pattern.compile("-?\\d+"), "number", "long", Long::parseLong);

	private final Pattern regex;
	private final String name;
	private final String subName;
	private final Function<String, T> convertor;
	private final Function<T, String> deconvertor;

	public ConfigFormat(Pattern regex, String name, String subName, Function<String, T> convertor) {
		this(regex, name, subName, convertor, Object::toString);
	}

	public ConfigFormat(Pattern regex, String name, String subName, Function<String, T> convertor, Function<T, String> deconvertor) {
		this.regex = regex;
		this.name = name;
		this.subName = subName;
		this.convertor = convertor;
		this.deconvertor = deconvertor;
	}

	public Pattern getRegex() {
		return regex;
	}

	public String getName() {
		return name;
	}

	public String getSubName() {
		return subName;
	}

	public T convert(String input) {
		return convertor.apply(input);
	}

	public String deconvert(T input) {
		return deconvertor.apply(input);
	}

	public boolean validate(String input) {
		Matcher matcher = this.regex.matcher(input);
		if (!matcher.matches()) {
			return false;
		}
		return true;
	}

	public static TypeAdapter<ConfigFormat<?>> typeAdapter(Gson gson) {
		return new TypeAdapter<ConfigFormat<?>>() {
			@Override
			public void write(JsonWriter out, ConfigFormat<?> value) throws IOException {
				out.beginObject();
				out.name("name").value(value.name);
				out.name("sub_name").value(value.subName);
				out.name("regex").value(value.regex.toString());
				out.endObject();
			}

			@Override
			public ConfigFormat<?> read(JsonReader in) throws IOException {
				throw new IOException("ConfigFormat cannot be read yet");
			}

		};
	}
}
