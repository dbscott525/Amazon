package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.EngineerType;

public class ShowEmailsAndUids extends Utility {

    public static void main(String[] args) {
	new ShowEmailsAndUids().run();
    }

    private List<Engineer> list;

    private void run() {
	list = EngineerFiles.MASTER_LIST
		.readCSV();
	Stream.of(EngineerType.values())
		.forEach(engineerType -> {
		    System.out.println(engineerType + " count: " + getEngineerStream(engineerType).count());
		    System.out.println();

		    printList("UIDs", Engineer::getUid, engineerType);
		    printList("Emails", Engineer::getEmail, engineerType);
		    System.out.println(engineerType + " Email String");
		    System.out.println();
		    String string = getSortedEgineerStream(Engineer::getEmail, engineerType)
			    .collect(Collectors.joining(";"));
		    System.out.println(string);
		    System.out.println();
		});
    }

    private void printList(String message, Function<Engineer, String> mapper, EngineerType engineerType) {
	printListsByType(message, mapper, engineerType);
    }

    private void printListsByType(String message, Function<Engineer, String> mapper,
	    EngineerType engineerrType) {
	System.out.println(engineerrType.toString() + " " + message + ":");
	System.out.println();
	getSortedEgineerStream(mapper, engineerrType)
		.forEach(System.out::println);
	System.out.println();
    }

    private Stream<String> getSortedEgineerStream(Function<Engineer, String> mapper, EngineerType engineerrType) {
	return getEngineerStream(engineerrType)
		.map(mapper)
		.sorted();
    }

    private Stream<Engineer> getEngineerStream(EngineerType engineerrType) {
	return list
		.stream()
		.filter(engineerrType::engineerIsType);
    }

}
