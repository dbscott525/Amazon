package com.scott_tigers.oncall;

import com.google.gson.GsonBuilder;

public class Json {

    public static void print(Object object) {
	String json = new GsonBuilder()
		.setPrettyPrinting()
		.create()
		.toJson(object);
	String className = object.getClass().getSimpleName();
	System.out.println(className
		+ ": \n"
		+ json);
    }
}
