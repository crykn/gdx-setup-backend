package com.badlogic.gdx.setup.util;


import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

/**
 * A simple JSON parser based on {@linkplain Gson Gson}.
 */
public class SimpleJSONParser {

	private final Gson gson;

	protected SimpleJSONParser(Gson gson) {
		this.gson = gson;
	}

	protected SimpleJSONParser(GsonBuilder gsonBuilder) {
		this(gsonBuilder.excludeFieldsWithModifiers(Modifier.STATIC).setDateFormat("yyyy-MM-dd HH:mm:ss").create());
	}

	public SimpleJSONParser() {
		this(new GsonBuilder());
	}

	public <T> T fromJson(String jsonInput, Class<T> clazz) throws JsonSyntaxException {
		return gson.fromJson(jsonInput, clazz);
	}

	public <T> T fromJson(byte[] jsonInput, Class<T> clazz) throws JsonSyntaxException {
		return gson.fromJson(new String(jsonInput), clazz);
	}

	@SuppressWarnings("unchecked")
	public <T> T fromJson(String jsonInput, Type type) throws JsonSyntaxException {
		return (T) gson.fromJson(jsonInput, type);
	}

	@SuppressWarnings("unchecked")
	public <T> T fromJson(byte[] jsonInput, Type type) throws JsonSyntaxException {
		return (T) gson.fromJson(new String(jsonInput), type);
	}

	public String toJson(Object object) throws JsonSyntaxException {
		return gson.toJson(object);
	}

}