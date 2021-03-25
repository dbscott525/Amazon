package com.amazon.amsoperations.utility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.amazon.amsoperations.bean.Engineer;
import com.amazon.amsoperations.bean.WIP;
import com.amazon.amsoperations.shared.EngineerFiles;
import com.amazon.amsoperations.shared.TimeZone;
import com.amazon.amsoperations.shared.URL;
import com.amazon.amsoperations.shared.Util;

public class CreateCITWorkInProgressOrderKey extends Utility {

    private Map<String, String> ownerMap = new HashMap<>();
    private Random random;
    private List<String> istEngineers;

    public static void main(String[] args) {
	new CreateCITWorkInProgressOrderKey().run();
    }

    private void run() {

	istEngineers = EngineerFiles.MASTER_LIST
		.readCSV()
		.stream()
		.filter(x -> TimeZone.IST.isIn(x))
		.map(Engineer::getFirstName)
		.map(String::toLowerCase)
		.collect(Collectors.toList());

	random = new Random();

	String sortKeys = readFromUrl(URL.CIT_TICKET_TRACKER, WIP.class)
		.map(this::getSortKey)
		.collect(Collectors.joining("\n"));

	Util.copyToClipboard(sortKeys);

	System.out.println("Work in Progress key copied to clipboard");

    }

    private String getSortKey(WIP wip) {

	String owner = wip.getOwner().toLowerCase();

	Stream<Function<String, String>> ownerKeyStream = Stream.of(

		fromMap(),
		fromRelease(),
		fromIst(),
		fromNew()

	);

	String ownerKey = ownerKeyStream
		.map(getter -> getter.apply(owner))
		.filter(Objects::nonNull)
		.findFirst()
		.orElse("000");

	ownerMap.put(owner, ownerKey);
	return ownerKey + "-" + wip.getDate();
    }

    private Function<String, String> fromMap() {
	return name -> ownerMap.get(name);
    }

    private Function<String, String> fromRelease() {
	return owner -> Character.isDigit(owner.charAt(0)) ? "999" : null;
    }

    private Function<String, String> fromIst() {
	return owner -> istEngineers.contains(owner) ? "000" : null;
    }

    private Function<String, String> fromNew() {
	return owner -> String.format("%03d", random.nextInt(900));
    }
}
