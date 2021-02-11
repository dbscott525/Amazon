package com.scott_tigers.oncall.test;

import java.util.Arrays;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.scott_tigers.oncall.utility.Utility;

public class Test extends Utility {

    public static void main(String[] args) throws Exception {
	new Test().run();
    }

    private void run() throws Exception {
	List<String> inputs = Arrays.asList(
		"SUMMARY:[Oncall] zyuhang@ and sargonb@ are Oncall for aurora-head-primary-ams",
		"SUMMARY:[Oncall] balarjun@ is Oncall for aurora-head-primary-ams from 2021/01/13 to 2021/01/14");
	inputs.stream().forEach(line -> {
	    System.out.println("line=" + (line));
	    getUIDStream(line)
		    .forEach(System.out::println);
	});

    }

    private Stream<String> getUIDStream(String line) {
	return Pattern.compile(" [a-zA-Z]*?@ ")
		.matcher(line)
		.results()
		.map(MatchResult::group)
		.map(x -> x.replaceAll(" (.*?)@ ", "$1"));
    }

//    private Stream<String> getUidStream(String line) {
//	return new IteratorStream<String>() {
//	    Matcher matcher = Pattern.compile(" [a-zA-Z]*? ").matcher(line);
//	    MatchResult pending;
//
//	    @Override
//	    protected String next() {
//		if (!hasNext()) {
//		    throw new NoSuchElementException();
//		}
//		// Consume pending so next call to hasNext() does a find().
//		MatchResult next = pending;
//		pending = null;
//		return next.get;
//	    }
//
//	    @Override
//	    protected boolean hasNext() {
//		if (pending == null && matcher.find()) {
//		    pending = matcher.toMatchResult();
//		}
//		return pending != null;
//	    }
//
//	}.getStream();
//    }

}