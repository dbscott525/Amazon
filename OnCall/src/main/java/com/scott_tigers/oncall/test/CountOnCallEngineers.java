package com.scott_tigers.oncall.test;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.bean.OnCallScheduleRow;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.EngineerType;
import com.scott_tigers.oncall.shared.Json;

public class CountOnCallEngineers {

    public static void main(String[] args) {
	new CountOnCallEngineers().run();
    }

    private void run() {
	List<String> engineerTypes = Stream
		.of(EngineerType.values())
		.map(EngineerType::toString)
		.collect(Collectors.toList());

	Map<String, Long> typeCounts = EngineerFiles.ON_CALL_SCHEDULE
		.readCSVToPojo(OnCallScheduleRow.class)
		.stream()
		.filter(engineer -> engineerTypes.contains(engineer.getType()))
		.map(Engineer::new)
		.distinct()
		.collect(Collectors.groupingBy(Engineer::getType, Collectors.counting()));

	Integer total = typeCounts
		.entrySet()
		.stream()
		.map(Entry<String, Long>::getValue)
		.collect(Collectors.summingInt(Long::intValue));

	Json.print(typeCounts);
	System.out.println("total=" + (total));
    }

}
