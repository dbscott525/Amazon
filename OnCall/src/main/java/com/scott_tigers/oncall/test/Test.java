package com.scott_tigers.oncall.test;

import java.util.stream.Stream;

import com.scott_tigers.oncall.utility.Utility;

public class Test extends Utility {

    public static void main(String[] args) throws Exception {
	new Test().run();
    }

    private void run() throws Exception {
	Stream.of("E1234", "5678").forEach(input -> {
	    String regex = "E?(.*)";
	    System.out.println("input=" + (input));
	    String output = input.replaceAll(regex, "$1");
	    int value = Integer.parseInt(output);
	    System.out.println("output=" + (output));
	    System.out.println("value=" + (value));

	});
    }

}