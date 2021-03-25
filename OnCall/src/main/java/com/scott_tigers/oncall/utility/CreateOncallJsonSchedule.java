package com.scott_tigers.oncall.utility;

import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
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

    private static final int MINIMUM_WEEK_END_GAP = 9;
    private static final int ONCALL_MINIMUM_SCHEDULE_GAP = 5;
    private static final boolean USE_TEST_DATA = false;
    private static final boolean DEBUG = false;

    private List<String> holidays;
    private Random random;
    private List<OnlineScheduleEvent> newScedule;
    private List<Engineer> engineers;
    private List<UnavailabilityDate> unavailability;
    private OnCallContainer historicalSchedule;
    private Predicate<OnlineScheduleEvent> eventFilter = event -> true;
    private EngineerType engineerType;
    private Supplier<OnCallContainer> historicalScheduleProvider = () -> readOnCallSchedule(engineerType,
	    USE_TEST_DATA);
    private Supplier<String> startDateSupplier = () -> Dates.SORTABLE.getFormattedDelta(getLastHistoricalScheduleDate(),
	    1);
    private Consumer<OnlineScheduleEvent> gapHandler = event -> handleScheduleGap(event);
    private Supplier<String> endDateSupplier = () -> Dates.SORTABLE.getFormattedDelta(Dates.SORTABLE.getFormattedDate(),
	    31);
    private BiPredicate<OncallMetric, OnlineScheduleEvent> sameWeekFilter = (metric, event) -> true;
    private Predicate<OncallMetric> scheduleGapFilter = metric -> metric.scheduleGap >= ONCALL_MINIMUM_SCHEDULE_GAP;
    private Consumer<List<OnlineScheduleEvent>> schedulePostProcessor = schedule -> {
    };

    protected void run() throws Exception {

	engineers = EngineerFiles.MASTER_LIST
		.readCSVToPojo(Engineer.class)
		.stream()
		.filter(engineerType::engineerIsType)
		.collect(Collectors.toList());

	random = new Random();

	holidays = EngineerFiles.AMAZON_HOLIDAYS
		.readCSVToPojo(Holiday.class)
		.stream()
		.map(Holiday::getDate)
		.map(date -> Dates.ONLINE_SCHEDULE.convertFormat(date, Dates.SORTABLE))
		.collect(Collectors.toList());

	createUnavailabilityList();

	historicalSchedule = historicalScheduleProvider.get();

	String startDate = startDateSupplier.get();
	String endDate = endDateSupplier.get();

	newScedule = historicalSchedule
		.getSchedule()
		.stream()
		.filter(x -> x.before(startDate))
		.collect(Collectors.toList());
	System.out.println("Schedule Size: " + (newScedule.size()));

	engineerType
		.getStream(s -> s
			.startDate(startDate)
			.endDate(endDate))
		.filter(eventFilter::test)
		.forEach(event -> {

		    List<OncallMetric> metrics = engineers
			    .stream()
			    .filter(eng -> !engineerType.useTimeZones() || event.inTimeZone(eng))
			    .filter(eng -> event.available(eng, unavailability))
			    .filter(eng -> eng.isOncall(event.getStartDate()))
			    .map(eng -> new OncallMetric(eng, event))
			    .filter(metric -> sameWeekFilter.test(metric, event))
			    .collect(Collectors.toList());

		    if (DEBUG) {
			Json.print(event);
			metrics.stream().sorted().forEach(System.out::println);
		    }

		    Optional<OncallMetric> foo = metrics
			    .stream()
			    .filter(scheduleGapFilter)
			    .filter(x -> x.weekendGap >= MINIMUM_WEEK_END_GAP)
			    .min(Comparator.comparing(x -> x));

		    foo.ifPresentOrElse(metric -> {
			event.setUid(metric.getUid());
			event.setScheduleGap(metric.scheduleGap);
			newScedule.add(event);
		    }, () -> gapHandler.accept(event));

		});

	List<OnlineScheduleEvent> newSchedule = newScedule
		.stream()
		.filter(event -> event.after(startDate))
		.collect(Collectors.toList());

	newSchedule
		.stream()
		.map(OnlineScheduleEvent::getFormattedLine)
		.forEach(System.out::println);

	newSchedule
		.stream()
		.collect(Collectors.groupingBy(OnlineScheduleEvent::getUid))
		.entrySet()
		.stream()
		.forEach(entry -> {
		    System.out.println(getEngineer(entry.getKey()));
		    entry.getValue()
			    .stream()
			    .map(x -> "  " + x.getFormattedLine())
			    .forEach(System.out::println);
		});

	schedulePostProcessor.accept(newSchedule);

	engineerType.getScheduleFile().write(w -> w.json(newSchedule));
	engineerType.getScheduleContainerFiler().write(w -> w.json(new OnCallContainer(newSchedule)));
    }

    private void handleScheduleGap(OnlineScheduleEvent event) {
	System.out.println("Can't find anyone for this event:");
	Json.print(event);
	System.exit(1);
    }

    private String getLastHistoricalScheduleDate() {
	return historicalSchedule
		.getSchedule()
		.stream()
		.map(OnlineScheduleEvent::getStartDate)
		.max(Comparator.comparing(x -> x))
		.get();
    }

    private void createUnavailabilityList() {
	Stream<Stream<UnavailabilityDate>> unavailabilityStream = EngineerFiles.UNAVAILABILITY
		.readCSVToPojo(Unavailability.class)
		.stream()
		.map(Unavailability::getUnvailabilityKeyStream);

	Stream<Stream<UnavailabilityDate>> citStream = getShiftStream()
		.map(shift -> shift
			.getUids()
			.stream()
			.map(uid -> new UnavailabilityDate(uid, shift.getDate())));

	unavailability = Stream
		.concat(unavailabilityStream, citStream)
		.flatMap(x -> x)
		.collect(Collectors.toList());

    }

    private boolean isTimeZoneSensitive() {
	return engineerType.isTimeZoneSensitive();
    }

    private boolean isHoliday(OnlineScheduleEvent event) {
	return isHoliday(event.getStartDate()) || isHoliday(event.getEndDateTime());
    }

    private boolean isWeekend(OnlineScheduleEvent event) {
	switch (event.getStartDayOfWeek()) {

	case Calendar.SUNDAY:
	case Calendar.SATURDAY:
	    return true;

	case Calendar.FRIDAY:
	    return event.getStartHour() >= 17;

	default:
	    return false;

	}
    }

    private boolean isHoliday(String date) {
	return holidays.contains(date);
    }

//    private boolean isWeekend(String date) {
//	return Dates.SORTABLE.isWeekend(date);
//    }
//
    protected boolean isDublinSchedule(OnlineScheduleEvent event) {
	switch (event.getStartDayOfWeek()) {

	case Calendar.MONDAY:
	    return event.getStartHour() == 3;

	default:
	    return false;

	}
    }

    protected void eventFilter(Predicate<OnlineScheduleEvent> eventFilter) {
	this.eventFilter = eventFilter;
    }

    protected void historicalScheduleProvider(Supplier<OnCallContainer> historicalScheduleProvider) {
	this.historicalScheduleProvider = historicalScheduleProvider;
    }

    protected void startDate(String startDate) {
	startDateSupplier = () -> startDate;
    }

    protected void setEngineerType(EngineerType engineerType) {
	this.engineerType = engineerType;
    }

    protected void allowGaps() {
	gapHandler = event -> {
	};
    }

    protected void doNotUseHistory() {
	historicalScheduleProvider = () -> new OnCallContainer();
    }

    protected void setWeeks(int weeks) {
	endDateSupplier = () -> Dates.SORTABLE.getFormattedDelta(startDateSupplier.get(), weeks * 7);
    }

    protected void noSameWeekEvents() {
	sameWeekFilter = (metric, event) -> metric.notSameWeek(event);
    }

    protected void noScheduleGapFilter() {
	scheduleGapFilter = metric -> true;
    }

    protected void endDate(String date) {
	endDateSupplier = () -> date;
    }

    protected void postScheduleProcess(Consumer<List<OnlineScheduleEvent>> schedulePostProcessor) {
	this.schedulePostProcessor = schedulePostProcessor;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Holiday {
	private String date;

	public String getDate() {
	    return date;
	}

    }

    private class OncallMetric implements Comparable<OncallMetric> {

	private static final int VERY_LARGE_GAP = 1000;
	private static final String DATE_LONG_AGO = "2000-01-01";
	private static final int COUNT_FACTOR = 10;

	private String uid;
	private String lastScheduledDate = DATE_LONG_AGO;
	private int randomSort;
	private int scheduleGap;
	private boolean holiday;
	private boolean weekend;
	private int numberOfHolidays = 0;
	private int numberOfWeekends = 0;
	private int numberOfAfterHours = 0;
	private OnlineScheduleEvent nextEvent;
	private int numberOfHours = 0;
	private Engineer engineer;
	private int weekendGap = VERY_LARGE_GAP;
	private String lastWeekendDate = DATE_LONG_AGO;
	private boolean afterHours;;

	public OncallMetric(Engineer engineer, OnlineScheduleEvent nextEvent) {
	    this.engineer = engineer;
	    this.nextEvent = nextEvent;
	    this.randomSort = random.nextInt(100);
	    this.uid = engineer.getUid();
	    holiday = isHoliday(nextEvent);
	    weekend = isWeekend(nextEvent);
	    afterHours = nextEvent.isAfterHours();
	    adjustForNewEntry();

	    newScedule
		    .stream()
		    .filter(scheduleRow -> scheduleRow.getUid().equals(uid))
		    .forEach(this::processEvent);

	    scheduleGap = Optional
		    .ofNullable(lastScheduledDate)
		    .map(lastDate -> Dates.SORTABLE.getDifference(lastDate, nextEvent.getStartDate()))
		    .orElse(VERY_LARGE_GAP);

	    weekendGap = Optional
		    .ofNullable(lastWeekendDate)
		    .filter(lastDate -> isWeekend(nextEvent))
		    .map(lastDate -> Dates.SORTABLE.getDifference(lastDate, nextEvent.getStartDate()))
		    .orElse(VERY_LARGE_GAP);
	}

	public boolean notSameWeek(OnlineScheduleEvent event) {
	    String eventFirstDayOfWeek = Dates.SORTABLE.getFirstDayOfWeek(event.getStartDate());
	    String lastScheduleDateFirstDayOfWeek = Dates.SORTABLE.getFirstDayOfWeek(lastScheduledDate);
	    return !eventFirstDayOfWeek.equals(lastScheduleDateFirstDayOfWeek);
	}

	private void adjustForNewEntry() {

	    if (engineer.getOncallStartDate() == null) {
		return;
	    }

	    List<Engineer> timeZoneEngineers = engineers
		    .stream()
		    .filter(eng -> !isTimeZoneSensitive() || eng.getTimeZone().equals(engineer.getTimeZone()))
		    .filter(eng -> !eng.equals(engineer))
		    .collect(Collectors.toList());

	    if (timeZoneEngineers.size() == 0) {
		return;
	    }

	    List<String> timeZoneUids = timeZoneEngineers
		    .stream()
		    .map(x -> x.getUid())
		    .collect(Collectors.toList());
	    assert timeZoneUids.size() != 0 : "No time zone uids";

	    HistoricalCounter historicalCount = new HistoricalCounter();

	    newScedule
		    .stream()
		    .filter(previousEvent -> previousEvent.before(engineer.getOncallStartDate()))
		    .filter(previousEvent -> timeZoneUids.contains(previousEvent.getUid()))
		    .forEach(previousEvent -> {
			historicalCount.addHours(previousEvent.getCommonHours(nextEvent));
			historicalCount.addWeekend(weekend && isWeekend(previousEvent));
			historicalCount.addHoliday(holiday && isHoliday(previousEvent));
			historicalCount.addAfterHours(afterHours && previousEvent.isAfterHours());
		    });

	    numberOfHours = (int) (historicalCount.getHours() / timeZoneUids.size());
	    numberOfHolidays = (int) (historicalCount.getHolidays() / timeZoneUids.size() * COUNT_FACTOR);
	    numberOfWeekends = (int) (historicalCount.getWeekends() / timeZoneUids.size() * COUNT_FACTOR);
	    numberOfAfterHours = (int) (historicalCount.getAfterHours() / timeZoneUids.size());
	}

	private void processEvent(OnlineScheduleEvent historicalEvent) {

	    if (isWeekend(historicalEvent) && weekend) {
		lastWeekendDate = Stream
			.of(lastWeekendDate, historicalEvent.getStartDate())
			.max(Comparator.comparing(x -> x))
			.get();
	    }

	    lastScheduledDate = Stream
		    .of(lastScheduledDate, historicalEvent.getStartDate())
		    .max(Comparator.comparing(x -> x))
		    .get();

	    numberOfHours += historicalEvent.getCommonHours(nextEvent);
	    numberOfHolidays += isHoliday(historicalEvent) && holiday ? COUNT_FACTOR : 0;
	    numberOfWeekends += isWeekend(historicalEvent) && weekend ? COUNT_FACTOR : 0;
	    numberOfAfterHours += historicalEvent.isAfterHours() && afterHours ? 1 : 0;

	}

	public String getUid() {
	    return uid;
	}

	@Override
	public String toString() {
	    return String.format("uid:%10s holidays:%2d weekends:%2d after hours:%2d hours:%2d gap:%3d random:%2d", uid,
		    numberOfHolidays, numberOfWeekends, numberOfAfterHours, numberOfHours, scheduleGap, randomSort);
	}

	@Override
	public int compareTo(OncallMetric o) {

	    Stream<Supplier<Integer>> comparators = Stream.of(
		    () -> numberOfHolidays - o.numberOfHolidays,
		    () -> numberOfWeekends - o.numberOfWeekends,
		    () -> numberOfAfterHours - o.numberOfAfterHours,
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
	int afterHours = 0;

	public void addHours(int hours) {
	    this.hours += hours;
	}

	public void addAfterHours(boolean isAfterHours) {
	    afterHours += isAfterHours ? 1 : 0;
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

	public int getAfterHours() {
	    return afterHours;
	}

	@Override
	public String toString() {
	    return "HistoricalCounter [hours=" + hours + ", holidays=" + holidays + ", weekends=" + weekends + "]";
	}
    }
}