/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.servermanagerbackend.util;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import java.util.function.Function;

public class GenericTypeAdaptorFactory<T> implements TypeAdapterFactory {

	private final Function<Gson, TypeAdapter<T>> function;
	private final Class<T> type;

	public GenericTypeAdaptorFactory(Function<Gson, TypeAdapter<T>> function, Class<T> type) {
		this.function = function;
		this.type = type;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
		if (this.type.isAssignableFrom(type.getRawType())) {
			return (TypeAdapter<T>) this.function.apply(gson);
		}
		return null;
	}

}
