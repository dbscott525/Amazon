package com.amazon.amsoperations.utility;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.amazon.amsoperations.bean.Engineer;
import com.amazon.amsoperations.shared.Constants;
import com.amazon.amsoperations.shared.EngineerFiles;

public class GenerateEmails extends Utility {

    public static void main(String[] args) {
	new GenerateEmails().run();
    }

    private List<String> uids;

    private void run() {

	uids = EngineerFiles.UID_INPUT
		.readCSVToPojo(Engineer.class).stream()
		.map(x -> x.getUid())
		.collect(Collectors.toList());

	printEmails(x -> x + Constants.AMAZON_EMAIL_POSTFIX);
	printEmails(x -> "page-" + x + Constants.AMAZON_EMAIL_POSTFIX);

    }

    private void printEmails(Function<String, String> mapper) {
	System.out.println(uids
		.stream()
		.map(mapper)
		.peek(System.out::println)
		.collect(Collectors.joining(";")));
    }
}
