package com.scott_tigers.oncall.shared;

import com.google.gson.GsonBuilder;

public class Json {

    public static void print(Object object) {
	String json = getJsonString(object);
	String className = object.getClass().getSimpleName();
	System.out.println(className
		+ ": \n"
		+ json);
    }

    public static String getJsonString(Object object) {
	return new GsonBuilder()
		.setPrettyPrinting()
		.create()
		.toJson(object);
    }
}
