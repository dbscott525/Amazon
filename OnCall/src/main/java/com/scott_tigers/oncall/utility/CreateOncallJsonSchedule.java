package com.scott_tigers.oncall.utility;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.bean.OnlineScheduleEvent;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.EngineerType;

@JsonIgnoreProperties
public abstract class CreateOncallJsonSchedule extends Utility {

    private List<String> holidays;

    private Random random;

    private List<OnlineScheduleEvent> newScedule;

    private List<Engineer> engineers;

    protected void run() throws Exception {
	random = new Random();

	holidays = EngineerFiles.AMAZON_HOLIDAYS
		.readCSVToPojo(Holiday.class)
		.stream()
		.map(Holiday::getDate)
		.map(date -> Dates.ONLINE_SCHEDULE.convertFormat(date, Dates.SORTABLE))
		.collect(Collectors.toList());

	OnCallContainer historicalSchedule = new OnCallContainer(getType()
		.getHistoricalOnCallScheduleStream()
		.collect(Collectors.toList()));

	newScedule = historicalSchedule
		.getSchedule()
		.stream()
		.filter(x -> x.before(startDate()))
		.collect(Collectors.toList());

	engineers = getRosterFile()
		.readCSVToPojo(Engineer.class)
		.stream()
		.filter(Engineer::isCurrent)
		.filter(getType()::engineerIsType)
		.collect(Collectors.toList());

	Dates.SORTABLE.getFormattedDelta(startDate(), getNumberOfDays());
	getType().getStream(s -> s.startDate(startDate()).days(getNumberOfDays()))
		.forEach(event -> {

		    List<OncallMetric> metrics = engineers.stream()
			    .map(te -> new OncallMetric(te, event)).collect(Collectors.toList());

		    OncallMetric metric = metrics.stream()
			    .min(Comparator.comparing(x -> x))
			    .get();

		    event.setUid(metric.getUid());

		    newScedule.add(event);
		});

	List<OnlineScheduleEvent> newSchedule = newScedule
		.stream()
		.filter(x -> x.after(startDate()))
		.collect(Collectors.toList());

	EngineerFiles.ONLINE_SCHEDULE.write(w -> w.json(newSchedule));
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
	private String lastScheduledDate = null;
	private int randomSort;
	private int scheduleGap;
	private boolean holiday;
	private int numberOfHolidays = 0;
	private int shiftType;
	private int shifts = 0;

	public OncallMetric(Engineer engineer, OnlineScheduleEvent event) {
	    this.randomSort = random.nextInt(100);
	    this.uid = engineer.getUid();
	    String date = event.getStartDate();
	    shiftType = event.getShiftType();
	    holiday = isHoliday(event);
	    if (engineer.getOncallStartDate() != null) {

		double engineerCount = engineers
			.stream()
			.map(Engineer::getUid)
			.filter(uid -> !engineer.getUid().equals(uid))
			.distinct()
			.count();

		double shiftTypeCount = newScedule
			.stream()
			.filter(e -> e.getShiftType() == shiftType)
			.count();

		shifts = (int) (shiftTypeCount / engineerCount * COUNT_FACTOR);

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

	    newScedule
		    .stream()
		    .filter(scheduleRow -> scheduleRow.getUid().equals(uid))
		    .forEach(this::processEvent);

	    scheduleGap = Optional
		    .ofNullable(lastScheduledDate)
		    .map(lastDate -> Dates.SORTABLE.getDifference(lastScheduledDate, date))
		    .orElse(1000);
	}

	private void processEvent(OnlineScheduleEvent event) {
	    String scheduleDate = event.getStartDate();
	    lastScheduledDate = Optional
		    .ofNullable(lastScheduledDate)
		    .filter(lastDate -> lastDate.compareTo(scheduleDate) > 0)
		    .orElse(scheduleDate);

	    if (shiftType == event.getShiftType()) {
		shifts += COUNT_FACTOR;
	    }

	    if (isHoliday(event) && holiday) {
		numberOfHolidays += COUNT_FACTOR;
	    }
	}

	public String getUid() {
	    return uid;
	}

	@Override
	public int compareTo(OncallMetric o) {

	    Stream<Supplier<Integer>> comparators = Stream.of(
		    () -> numberOfHolidays - o.numberOfHolidays,
		    () -> shifts - o.shifts,
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