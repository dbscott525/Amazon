package com.scott_tigers.oncall.test;

import java.io.IOException;

public class Test {

    public static void main(String[] args) {
	Runtime runtime = Runtime.getRuntime(); // getting Runtime object

	String[] s = new String[] {
		"C:\\Program Files (x86)\\Microsoft Office\\Office16\\excel.exe",
		"J:\\SupportEngineering\\OnCallData\\Customer Issue Backlog.csv"
	};

	try {
	    runtime.exec(s); // opens "https://javaconceptoftheday.com/" in chrome browser
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

}
