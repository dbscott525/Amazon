package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Json;

public class ShowWeeksPerEngineer extends Utility {

    public static void main(String[] args) {
	new ShowWeeksPerEngineer().run();
    }

    private void run() {

	List<EngineerTally> list = getShiftStream()
		.flatMap(shift -> shift.getEngineers().stream())
		.filter(eng -> eng.isNotServerless())
		.filter(eng -> eng.isBeforeEndDate())
		.map(Engineer::getFullName)
		.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
		.entrySet()
		.stream()
		.map(x -> new EngineerTally(x))
		.sorted()
		.collect(Collectors.toList());
	Json.print(list);
	EngineerFiles.ENGINEERS_AND_SHIFTS.write(w -> w.CSV(list, EngineerTally.class));

    }

    private class EngineerTally implements Comparable<EngineerTally> {
	Long count;
	String name;

	public EngineerTally(Entry<String, Long> entry) {
	    count = entry.getValue();
	    name = entry.getKey();
	}

	@SuppressWarnings("unused")
	public Long getCount() {
	    return count;
	}

	@SuppressWarnings("unused")
	public void setCount(Long count) {
	    this.count = count;
	}

	@SuppressWarnings("unused")
	public String getName() {
	    return name;
	}

	@SuppressWarnings("unused")
	public void setName(String name) {
	    this.name = name;
	}

	@Override
	public int compareTo(EngineerTally o) {
	    return (int) (o.count - count);
	}
    }

}
