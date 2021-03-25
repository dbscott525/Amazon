package com.scott_tigers.oncall.utility;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.EngineerType;
import com.scott_tigers.oncall.shared.Json;

public class CreatePrimaryTimeZoneReport extends Utility {

    public static void main(String[] args) {
	new CreatePrimaryTimeZoneReport().run();
    }

    private Long total;

    private void run() {
	Map<String, Long> counts = EngineerFiles.MASTER_LIST.readCSV()
		.stream()
		.filter(eng -> EngineerType.Primary.engineerIsType(eng))
		.map(eng -> eng.getTimeZone())
		.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
	Json.print(counts);
//	Stream<Long> xxx = counts.values().stream();
//	xxx.collect()
	total = counts.values().stream().collect(Collectors.summingLong(Long::longValue));
	System.out.println("total=" + (total));
	counts.entrySet().stream().map(entry -> new TimeZoneRow(entry));

    }

    private class TimeZoneRow {
	private String timeZone;
	private long count;

	public TimeZoneRow(Entry<String, Long> entry) {
	    timeZone = entry.getKey();
	    count = entry.getValue();
	}

    }
}
