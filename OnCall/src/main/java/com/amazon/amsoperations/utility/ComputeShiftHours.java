package com.amazon.amsoperations.utility;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.amazon.amsoperations.shared.EngineerFiles;
import com.amazon.amsoperations.shared.Json;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ComputeShiftHours extends Utility {

    private static final String TIME_ZONE_RATIOS = "Time Zone Ratios";
    private static final String HOURS_IN_SHIFT = "Hours in Shift";
    private static final String PRIMARIES = "Primaries";
    private static final String TIME_ZONE = "Time Zone";

    public static void main(String[] args) {
	new ComputeShiftHours().run();
    }

    private long totalPrimaries;

    private void run() {
	totalPrimaries = getPrimaryStream().count();
	System.out.println("totalPrimaries=" + (totalPrimaries));
	Map<String, Long> map = getPrimaryStream()
		.map(x -> x.getTimeZone())
		.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

	List<ShiftRatioTable> ratioTable = map.entrySet()
		.stream()
		.map(entry -> new ShiftRatioTable(entry))
		.collect(Collectors.toList());

	Json.print(ratioTable);
	EngineerFiles.TIME_ZONE_RATIOS.write(w -> w.CSV(ratioTable, TIME_ZONE, PRIMARIES, HOURS_IN_SHIFT));

    }

    private class ShiftRatioTable {

	private String timeZone;
	private long count;
	private int hours;

	public ShiftRatioTable(Entry<String, Long> entry) {
	    timeZone = entry.getKey();
	    count = entry.getValue();
	    hours = (int) Math.round(((double) (24 * count)) / totalPrimaries);
	}

	@JsonProperty(TIME_ZONE)
	public String getTimeZone() {
	    return timeZone;
	}

	@SuppressWarnings("unused")
	public void setTimeZone(String timeZone) {
	    this.timeZone = timeZone;
	}

	@JsonProperty(PRIMARIES)
	public long getCount() {
	    return count;
	}

	@SuppressWarnings("unused")
	public void setCount(long count) {
	    this.count = count;
	}

	@JsonProperty(HOURS_IN_SHIFT)
	public int getHours() {
	    return hours;
	}

	@SuppressWarnings("unused")
	public void setHours(int hours) {
	    this.hours = hours;
	}

    }
}
