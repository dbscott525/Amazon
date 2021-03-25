package com.scott_tigers.oncall.utility;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.scott_tigers.oncall.bean.OnlineScheduleEvent;
import com.scott_tigers.oncall.shared.EngineerType;

public class ShowOncallScheduleReport extends Utility {

    private EngineerType engineerType;
    private boolean useSavedSchedule = false;

    protected void run() {
	readOnCallSchedule(engineerType, useSavedSchedule)
		.getSchedule()
		.stream()
		.collect(Collectors.groupingBy(OnlineScheduleEvent::getUid))
		.entrySet()
		.stream()
		.peek(entry -> {
		    List<OnlineScheduleEvent> events = entry.getValue();
		    IntStream.range(1, events.size())
			    .forEach(index -> events.get(index).previousEvent(events.get(index - 1)));
		})
		.sorted(Comparator.comparing(t -> t.getKey()))
		.forEach(entry -> {
		    System.out.println();
		    System.out.println(getEngineer(entry.getKey()).getFullName());

		    entry
			    .getValue()
			    .stream()
			    .map(t -> "  " + t.getFormattedLine())
			    .forEach(System.out::println);

		    double average = entry
			    .getValue()
			    .stream()
			    .map(OnlineScheduleEvent::getScheduleGap)
			    .filter(gap -> gap != 0)
			    .mapToInt(i -> i)
			    .average()
			    .orElse(0);

		    System.out.printf("  Average: %.1f", average);
		});

    }

    protected void setEngineeringType(EngineerType engineerType) {
	this.engineerType = engineerType;

    }

    protected void useSavedSchedule() {
	useSavedSchedule = true;
    }
}
