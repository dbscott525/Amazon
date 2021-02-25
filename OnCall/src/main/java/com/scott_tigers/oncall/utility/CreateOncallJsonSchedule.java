package com.scott_tigers.oncall.utility;

import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.bean.OnlineScheduleEvent;
import com.scott_tigers.oncall.bean.Unavailability;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.EngineerType;
import com.scott_tigers.oncall.shared.Json;
import com.scott_tigers.oncall.shared.OnCallContainer;
import com.scott_tigers.oncall.shared.UnavailabilityDate;

@JsonIgnoreProperties
public abstract class CreateOncallJsonSchedule extends Utility {

    private static final int ONCALL_MINIMUM_SCHEDULE_GAP = 5;

    private static final boolean USE_TEST_DATA = false;

    private List<String> holidays;
    private Random random;
    private List<OnlineScheduleEvent> newScedule;
    private List<Engineer> engineers;
    private List<UnavailabilityDate> unavailability;

    protected void run() throws Exception {

	EngineerType engineerType = getType();
	engineers = getRosterFile()
		.readCSVToPojo(Engineer.class)
		.stream()
		.filter(Engineer::isCurrent)
		.peek(eng -> Json.print(eng))
		.filter(engineerType::engineerIsType)
		.collect(Collectors.toList());

	random = new Random();

	holidays = EngineerFiles.AMAZON_HOLIDAYS
		.readCSVToPojo(Holiday.class)
		.stream()
		.map(Holiday::getDate)
		.map(date -> Dates.ONLINE_SCHEDULE.convertFormat(date, Dates.SORTABLE))
		.collect(Collectors.toList());

	unavailability = EngineerFiles.UNAVAILABILITY
		.readCSVToPojo(Unavailability.class)
		.stream()
		.flatMap(Unavailability::getUnvailabilityKeyStream)
		.collect(Collectors.toList());

	OnCallContainer historicalSchedule;

	historicalSchedule = readOnCallSchedule(engineerType, USE_TEST_DATA);

	newScedule = historicalSchedule
		.getSchedule()
		.stream()
		.filter(x -> x.before(startDate()))
		.collect(Collectors.toList());

	engineerType
		.getStream(s -> s
			.startDate(startDate())
			.days(getNumberOfDays()))
		.filter(getEventFilter())
		.forEach(event -> {

		    List<OncallMetric> metrics = engineers
			    .stream()
			    .filter(eng -> !engineerType.useTimeZones() || event.inTimeZone(eng))
			    .filter(eng -> event.available(eng, unavailability))
			    .map(eng -> new OncallMetric(eng, event))
			    .collect(Collectors.toList());

		    OncallMetric metric = metrics
			    .stream()
			    .filter(x -> x.scheduleGap >= ONCALL_MINIMUM_SCHEDULE_GAP)
			    .min(Comparator.comparing(x -> x))
			    .get();

		    event.setUid(metric.getUid());
		    event.setScheduleGap(metric.scheduleGap);

		    newScedule.add(event);
		});

	List<OnlineScheduleEvent> newSchedule = newScedule
		.stream()
		.filter(x -> x.after(startDate()))
		.collect(Collectors.toList());

	newSchedule
		.stream()
		.map(x -> x.getFormattedLine())
		.forEach(System.out::println);

	newSchedule
		.stream()
		.collect(Collectors.groupingBy(x -> x.getUid()))
		.entrySet()
		.stream()
		.forEach(entry -> {
		    System.out.println(entry.getKey());
		    entry.getValue().stream().map(x -> "  " + x.getFormattedLine()).forEach(System.out::println);
		});

	engineerType.getScheduleFile().write(w -> w.json(newSchedule));
    }

    protected Predicate<OnlineScheduleEvent> getEventFilter() {
	return event -> true;
    }

    protected abstract EngineerFiles getRosterFile();

    protected abstract EngineerType getType();

    protected abstract String startDate();

    protected abstract int getNumberOfDays();

    private boolean isHoliday(OnlineScheduleEvent event) {
	return isHoliday(event.getStartDate()) || isHoliday(event.getEndDateTime());
    }

    private boolean isWeekend(OnlineScheduleEvent event) {
	return isWeekend(event.getStartDate()) || isWeekend(event.getEndDate());
    }

    private boolean isHoliday(String date) {
	return holidays.contains(date);
    }

    private boolean isWeekend(String date) {
	return Dates.SORTABLE.isWeekend(date);
    }

    protected boolean isDublinSchedule(OnlineScheduleEvent event) {
	switch (event.getStartDayOfWeek()) {

	case Calendar.MONDAY:
	    return event.getStartHour() == 3;

	default:
	    return false;

	}
    }

//    class OnCallContainer {
//	List<OnlineScheduleEvent> schedule;
//
//	public OnCallContainer(List<OnlineScheduleEvent> schedule) {
//	    this.schedule = schedule;
//	}
//
//	public List<OnlineScheduleEvent> getSchedule() {
//	    return schedule;
//	}
//
//    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Holiday {
	private String date;

	public String getDate() {
	    return date;
	}

    }

    private class OncallMetric implements Comparable<OncallMetric> {

	private static final int COUNT_FACTOR = 10;
	private String uid;
	private String lastScheduledDate = "2000-01-01";
	private int randomSort;
	private int scheduleGap;
	private boolean holiday;
	private boolean weekend;
	private int numberOfHolidays = 0;
	private int numberOfWeekends = 0;
	private OnlineScheduleEvent nextEvent;
	private int numberOfHours = 0;
	private Engineer engineer;

	public OncallMetric(Engineer engineer, OnlineScheduleEvent nextEvent) {
	    this.engineer = engineer;
	    this.nextEvent = nextEvent;
	    this.randomSort = random.nextInt(100);
	    this.uid = engineer.getUid();
	    String date = nextEvent.getStartDate();
	    holiday = isHoliday(nextEvent);
	    weekend = isWeekend(nextEvent);
	    adjustForNewEntry();

	    newScedule
		    .stream()
		    .filter(scheduleRow -> scheduleRow.getUid().equals(uid))
		    .forEach(this::processEvent);

	    scheduleGap = Optional
		    .ofNullable(lastScheduledDate)
		    .map(lastDate -> Dates.SORTABLE.getDifference(lastScheduledDate, date))
		    .orElse(1000);
	}

	private void adjustForNewEntry() {

	    if (engineer.getOncallStartDate() == null) {
		return;
	    }

	    List<Engineer> timeZoneEngineers = engineers
		    .stream()
		    .filter(eng -> eng.getTimeZone().equals(engineer.getTimeZone()))
		    .filter(eng -> !eng.equals(engineer))
		    .collect(Collectors.toList());

	    List<String> timeZoneUids = timeZoneEngineers.stream().map(x -> x.getUid()).collect(Collectors.toList());

	    HistoricalCounter historicalCount = new HistoricalCounter();

	    newScedule
		    .stream()
		    .filter(previousEvent -> previousEvent.before(engineer.getOncallStartDate()))
		    .filter(previousEvent -> timeZoneUids.contains(previousEvent.getUid()))
		    .forEach(previousEvent -> {
			historicalCount.addHours(previousEvent.getCommonHours(nextEvent));
			historicalCount.addWeekend(weekend && isWeekend(previousEvent));
			historicalCount.addHoliday(holiday && isHoliday(previousEvent));
		    });

	    numberOfHours = (int) (historicalCount.getHours() / timeZoneUids.size());
	    numberOfHolidays = (int) (historicalCount.getHolidays() / timeZoneUids.size() * COUNT_FACTOR);
	    numberOfWeekends = (int) (historicalCount.getWeekends() / timeZoneUids.size() * COUNT_FACTOR);
	}

	private void processEvent(OnlineScheduleEvent historicalEvent) {
	    lastScheduledDate = Stream
		    .of(lastScheduledDate, historicalEvent.getStartDate())
		    .max(Comparator.comparing(x -> x))
		    .get();

	    numberOfHours += historicalEvent.getCommonHours(nextEvent);
	    numberOfHolidays += isHoliday(historicalEvent) && holiday ? COUNT_FACTOR : 0;
	    numberOfWeekends += isWeekend(historicalEvent) && weekend ? COUNT_FACTOR : 0;

	}

	public String getUid() {
	    return uid;
	}

	@Override
	public String toString() {
	    return String.format("uid:%10s holidays:%2d weekends:%2d hours:%2d gap:%3d random:%2d", uid,
		    numberOfHolidays, numberOfWeekends, numberOfHours, scheduleGap, randomSort);
	}

	@Override
	public int compareTo(OncallMetric o) {

	    Stream<Supplier<Integer>> comparators = Stream.of(
		    () -> numberOfHolidays - o.numberOfHolidays,
		    () -> numberOfWeekends - o.numberOfWeekends,
		    () -> numberOfHours - o.numberOfHours,
		    () -> o.scheduleGap - scheduleGap,
		    () -> o.randomSort - randomSort);

	    return comparators
		    .map(Supplier<Integer>::get)
		    .filter(x -> x != 0)
		    .findFirst()
		    .orElse(0);
	}

    }

    private class HistoricalCounter {
	int hours = 0;
	int holidays = 0;
	int weekends = 0;

	public void addHours(int hours) {
	    this.hours += hours;
	}

	public void addHoliday(boolean holiday) {
	    holidays += holiday ? 1 : 0;
	}

	public void addWeekend(boolean weekend) {
	    weekends += weekend ? 1 : 0;
	}

	public int getHours() {
	    return hours;
	}

	public int getHolidays() {
	    return holidays;
	}

	public int getWeekends() {
	    return weekends;
	}

	@Override
	public String toString() {
	    return "HistoricalCounter [hours=" + hours + ", holidays=" + holidays + ", weekends=" + weekends + "]";
	}
    }
}