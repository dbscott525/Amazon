package com.scott_tigers.oncall.test;

import java.util.Optional;
import java.util.stream.Stream;

public class RegexTester {

    public static void main(String[] args) {
	new RegexTester().run();
    }

    private void run() {
	Stream.of(Test.values()).forEach(x -> x.test());
    }

    enum Test {
	Test1("Displaying 1 to 6 of 6 matches", "Displaying(.*)", "$1", " 1 to 6 of 6 matches"),
	Test2("Displaying 1 to 6 of 6 matches", "Displaying .*?of (\\d*) matches", "$1", "6"),
	Test3("Displaying 1 to 17 of 17 matches", "Displaying .*?of (\\d*) matches", "$1", "17");

	private String input;
	private String regex;
	private String replacement;
	private String expectedOutput;

	Test(String input, String regex, String replacement, String expectedOutput) {
	    this.regex = regex;
	    this.input = input;
	    this.replacement = replacement;
	    this.expectedOutput = expectedOutput;
	}

	void test() {
	    Optional<String> o1 = Optional.ofNullable(input.replaceAll(regex, replacement));
	    Optional<String> o2 = o1.filter(x -> expectedOutput.compareTo(x) != 0);
	    o2.ifPresentOrElse(
		    result -> {
			System.out.println("invalid result: " + result);
		    },
		    () -> {
			System.out.println("regex " + regex + " was successful");
		    });
	}

    }

}
