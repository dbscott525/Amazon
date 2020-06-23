package com.scott_tigers.oncall;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class DateStringContainer {

    // json={"dates":["Geeks","for","Geeks"]}

    List<String> dates;

    public List<String> getDates() {
	return dates;
    }

    public static void main(String[] args) {
	DateStringContainer test = new DateStringContainer();
	test.dates = new ArrayList<String>() {
	    private static final long serialVersionUID = 1L;

	    {
		add("Geeks");
		add("for");
		add("Geeks");
	    }
	};

	Gson gson = new Gson();
	String json = gson.toJson(test);
	System.out.println("json=" + (json));
    }

}
