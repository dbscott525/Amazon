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
	Test1("2020-09-15 09:07:48PM GMT-0700", "(.*?)(PM|AM) GMT.*", "$1 $2", "2020-09-15 09:07:48 PM");

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
