/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.internal.config;

import java.util.Objects;

/**
 *
 * @author Fernando van Loenhout
 */
public class DefaultConfigKey<T> implements ConfigKey<T>{

	private final boolean allowsEmptyValue;

	private final ConfigFormat<T> format;
	private final String description;

	public DefaultConfigKey(String name, boolean allowsEmptyValue, ConfigFormat<T> format, String description) {
		this.description = Objects.requireNonNull(description, "description");
		this.allowsEmptyValue = allowsEmptyValue;
		this.format = Objects.requireNonNull(format, "format");
	}

	@Override
	public boolean allowsEmptyValue() {
		return allowsEmptyValue;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public ConfigFormat<? super T> getFormat() {
		return format;
	}
}
