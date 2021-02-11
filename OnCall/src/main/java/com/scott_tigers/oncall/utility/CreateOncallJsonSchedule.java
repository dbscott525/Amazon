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
import com.scott_tigers.oncall.shared.UnavailabilityDate;

@JsonIgnoreProperties
public abstract class CreateOncallJsonSchedule extends Utility {

    private static final boolean USE_TEST_DATA = false;

    private List<String> holidays;
    private Random random;
    private List<OnlineScheduleEvent> newScedule;
    private List<Engineer> engineers;

    private List<UnavailabilityDate> unavailability;

    protected void run() throws Exception {

	engineers = getRosterFile()
		.readCSVToPojo(Engineer.class)
		.stream()
		.filter(Engineer::isCurrent)
		.filter(getType()::engineerIsType)
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
		.flatMap(x -> x.getUnvailabilityKeyStream())
		.collect(Collectors.toList());

	OnCallContainer historicalSchedule;

	if (USE_TEST_DATA) {
	    historicalSchedule = EngineerFiles.TEST_HISTORICAL_SCHEDULE.readJson(OnCallContainer.class);

	} else {
	    historicalSchedule = new OnCallContainer(getType()
		    .getHistoricalOnCallScheduleStream()
		    .collect(Collectors.toList()));

	    EngineerFiles.TEST_HISTORICAL_SCHEDULE.write(w -> w.json(historicalSchedule));
	}

	newScedule = historicalSchedule
		.getSchedule()
		.stream()
		.filter(x -> x.before(startDate()))
		.collect(Collectors.toList());

	getType()
		.getStream(s -> s
			.startDate(startDate())
			.days(getNumberOfDays()))
		.filter(getEventFilter())
		.forEach(event -> {

		    List<OncallMetric> metrics = engineers
			    .stream()
			    .filter(eng -> !getType().useTimeZones() || event.inTimeZone(eng))
			    .filter(eng -> event.available(eng, unavailability))
			    .map(te -> new OncallMetric(te, event))
			    .collect(Collectors.toList());
//		    System.out.println("event.getStartDayOfWeek()=" + (event.getStartDayOfWeek()));
//		    System.out.println("event.getStartDate()=" + (event.getStartDate()));
//		    metrics.stream().sorted().forEach(System.out::println);
//		    System.exit(1);

		    OncallMetric metric = metrics
			    .stream()
//			    .peek(x -> System.out.println(x.uid + "->" + x.scheduleGap))
			    .filter(x -> x.scheduleGap > 3)
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

	EngineerFiles.ONLINE_SCHEDULE.write(w -> w.json(newSchedule));
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

    private boolean isHoliday(String date) {
	return holidays.contains(date);
    }

    protected boolean isDublinSchedule(OnlineScheduleEvent event) {
	switch (event.getStartDayOfWeek()) {

	case Calendar.MONDAY:
	    return event.getStartHour() == 3;

	default:
	    return false;

	}
    }

    private class OnCallContainer {
	List<OnlineScheduleEvent> schedule;

	public OnCallContainer(List<OnlineScheduleEvent> schedule) {
	    this.schedule = schedule;
	}

	public List<OnlineScheduleEvent> getSchedule() {
	    return schedule;
	}

    }

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
	private int numberOfHolidays = 0;
//	private int shiftType;
//	private int shifts = 0;
	private OnlineScheduleEvent nextEvent;
	private int numberOfHours = 0;
	private Engineer engineer;

	public OncallMetric(Engineer engineer, OnlineScheduleEvent nextEvent) {
	    this.engineer = engineer;
	    this.nextEvent = nextEvent;
	    this.randomSort = random.nextInt(100);
	    this.uid = engineer.getUid();
	    String date = nextEvent.getStartDate();
//	    shiftType = nextEvent.getShiftType();
	    holiday = isHoliday(nextEvent);
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

	    int engineerCount = (int) engineers
		    .stream()
		    .map(Engineer::getUid)
		    .filter(uid -> !engineer.getUid().equals(uid))
		    .distinct()
		    .count();

	    int historicalHoursInShift = newScedule
		    .stream()
		    .filter(previousEvent -> !previousEvent.getUid().equals(uid))
		    .filter(previousEvent -> previousEvent.before(engineer.getOncallStartDate()))
		    .map(previousEvent -> previousEvent.getCommonHours(nextEvent))
		    .collect(Collectors.summingInt(Integer::intValue));

//		double shiftTypeCount = newScedule
//			.stream()
//			.filter(e -> e.getShiftType() == shiftType)
//			.count();

//		shifts = (int) (shiftTypeCount / engineerCount * COUNT_FACTOR);
	    numberOfHours = (int) (historicalHoursInShift / engineerCount);

	    if (holiday) {
		double holidayCount = newScedule
			.stream()
			.filter(e -> isHoliday(e))
			.count();
		System.out.println("holidayCount=" + (holidayCount));
		numberOfHolidays = (int) (holidayCount / engineerCount * COUNT_FACTOR);
		System.out.println("numberOfHolidays=" + (numberOfHolidays));
	    }
	}

	private void processEvent(OnlineScheduleEvent historicalEvent) {
	    lastScheduledDate = Stream
		    .of(lastScheduledDate, historicalEvent.getStartDate())
		    .max(Comparator.comparing(x -> x))
		    .get();
//	    String scheduleDate = historicalEvent.getStartDate();

//	    lastScheduledDate = Optional
//		    .ofNullable(lastScheduledDate)
//		    .filter(lastDate -> lastDate.compareTo(scheduleDate) > 0)
//		    .orElse(scheduleDate);

	    numberOfHours += historicalEvent.getCommonHours(nextEvent);

//	    if (shiftType == historicalEvent.getShiftType()) {
//		shifts += COUNT_FACTOR;
//	    }

	    if (isHoliday(historicalEvent) && holiday) {
		numberOfHolidays += COUNT_FACTOR;
	    }
	}

	public String getUid() {
	    return uid;
	}

	@Override
	public String toString() {
	    return "OncallMetric [uid=" + uid + ", randomSort=" + randomSort + ", scheduleGap=" + scheduleGap
		    + ", numberOfHolidays=" + numberOfHolidays + ", numberOfHours=" + numberOfHours + "]";
	}

	@Override
	public int compareTo(OncallMetric o) {

	    Stream<Supplier<Integer>> comparators = Stream.of(
		    () -> numberOfHolidays - o.numberOfHolidays,
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
}