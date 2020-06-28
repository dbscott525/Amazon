package com.scott_tigers.oncall.test;

import java.util.Arrays;
import java.util.stream.Stream;

import com.google.gson.GsonBuilder;

public class Top100Company {
    String company;
    static String[] removablePostfixes = { ", inc.", " corp.", ", llc", " inc.",
	    " aka threewide, realtor.com, listhub.com, moving.com", " co., ltd.", " co. limited", " ltd",
	    " (smile family \u0026 jiver)", " corporation", " oy", " ag", " bv", " japan", ", llc.", ", inc", " plc",
	    " llc" };

    public String getCompany() {
	return company;
    }

    public void setCompany(String company) {
	this.company = normalize(company);
    }

    private String normalize(String coName) {
	String t1 = coName.toLowerCase();
	Stream<String> t2 = Arrays.stream(removablePostfixes);
	t1 = t2.reduce(t1, (current, postfix) -> {
	    return removePostfix(current, postfix);
	});

	return t1.trim();
    }

    private String removePostfix(String current, String postfix) {
	if (current.endsWith(postfix)) {
	    return current.substring(0, current.length() - postfix.length());
	}
	return current;
    }

    @Override
    public String toString() {
	return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }

}
