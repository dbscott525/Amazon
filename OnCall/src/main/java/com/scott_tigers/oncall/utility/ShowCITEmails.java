package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class ShowCITEmails {

    public static void main(String[] args) {
	new ShowCITEmails().run();
    }

    private void run() {
	List<Engineer> engineers = EngineerFiles.MASTER_LIST
		.readCSV()
		.stream()
		.filter(Engineer::isCurrent)
		.collect(Collectors.toList());

	System.out.println("EMAIL LIST");
	engineers
		.stream()
		.map(Engineer::getEmail)
		.sorted()
		.forEach(System.out::println);

	System.out.println("EMAIL STRING");
	System.out.println(
		engineers
			.stream()
			.map(Engineer::getEmail)
			.sorted()
			.collect(Collectors.joining(";")));

	System.out.println("UIDS");
	engineers
		.stream()
		.map(Engineer::getUid)
		.sorted()
		.forEach(System.out::println);

    }
}
