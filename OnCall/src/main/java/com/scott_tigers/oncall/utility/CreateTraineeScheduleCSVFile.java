package com.scott_tigers.oncall.utility;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.OnlineScheduleEvent;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.EngineerType;
import com.scott_tigers.oncall.shared.OnCallContainer;
import com.scott_tigers.oncall.shared.ScheduleTypeIterator;
import com.scott_tigers.oncall.shared.TimeZone;

public class CreateTraineeScheduleCSVFile extends Utility {

    public static void main(String[] args) {
	new CreateTraineeScheduleCSVFile().run();
    }

    private List<Integer> hours;

    private void run() {

	AtomicInteger start = new AtomicInteger(0);

	hours = ((ScheduleTypeIterator) EngineerType.Trainee.getScheduleTypeIterator())
		.getScheduleTypes()
		.stream()
		.map(scheduleType -> {
		    Integer startHour = start.getAndAdd(scheduleType.getHours());
		    return scheduleType.getTimeZone().equals(TimeZone.SKIP) ? null : startHour;
		})
		.filter(Objects::nonNull)
		.collect(Collectors.toList());
	System.out.println("hours=" + (hours));

	Stream<String> scheduleStream = EngineerFiles.TRAINEE_ONCALL_SCHEDULE_CONTAINER
		.readJson(OnCallContainer.class)
		.getSchedule()
		.stream()
		.collect(Collectors.groupingBy(OnlineScheduleEvent::getStartDate))
		.entrySet()
		.stream()
		.sorted(Comparator.comparing(Entry<String, List<OnlineScheduleEvent>>::getKey))
		.map(this::getEventLine);

	List<String> lines = Stream.concat(Stream.of(getHeader()), scheduleStream).collect(Collectors.toList());

	EngineerFiles.TRAINEE_CSV_SCHEDULE.write(w -> w.lines(lines));
    }

    private String getHeader() {
	return Stream
		.concat(Stream.of("Date", "Day"),
			hours.stream()
				.map(start -> start + ":00"))
		.collect(Collectors.joining(","));
    }

    private String getEventLine(Entry<String, List<OnlineScheduleEvent>> entry) {
	Map<Integer, String> hourMap = getHourMap();

	entry
		.getValue()
		.stream()
		.forEach(event -> hourMap.put(event.getStartHour(), getEngineer(event.getUid()).getFullName()));

	Stream<String> nameStream = hourMap
		.entrySet()
		.stream()
		.sorted(Comparator.comparing(Entry<Integer, String>::getKey))
		.map(Entry<Integer, String>::getValue);

	String date = entry.getKey();

	return Stream
		.concat(Stream.of(date, Dates.SORTABLE.getDayOfWeekName(date)), nameStream)
		.collect(Collectors.joining(","));
    }

    private Map<Integer, String> getHourMap() {
	return hours
		.stream()
		.collect(Collectors.toMap(x -> x, x -> ""));
    }
}
