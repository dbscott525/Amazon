package com.scott_tigers.oncall.utility;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.Gson;
import com.scott_tigers.oncall.bean.CitScheduleRow;
import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.bean.OnCallScheduleRow;
import com.scott_tigers.oncall.shared.DateStream;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Oncall;

@JsonIgnoreProperties
public class CreateTechEscSchedule extends Utility {

    private static final String START_DATE = "2021-01-17";

    private static final boolean TEST_DATA = false;

    public static void main(String[] args) throws Exception {
	new CreateTechEscSchedule().run();
    }

    private List<String> holidays;

    private static String TEST_SCHEDULE = "{\r\n"
	    + "  \"schedule\": [\r\n"
	    + "    {\r\n"
	    + "      \"date\": \"2021-01-09\",\r\n"
	    + "      \"uid\": \"setrakya\",\r\n"
	    + "      \"type\": \"TechEsc\"\r\n"
	    + "    },\r\n"
	    + "    {\r\n"
	    + "      \"date\": \"2021-01-10\",\r\n"
	    + "      \"uid\": \"attaluri\",\r\n"
	    + "      \"type\": \"TechEsc\"\r\n"
	    + "    },\r\n"
	    + "    {\r\n"
	    + "      \"date\": \"2021-01-11\",\r\n"
	    + "      \"uid\": \"stvla\",\r\n"
	    + "      \"type\": \"TechEsc\"\r\n"
	    + "    },\r\n"
	    + "    {\r\n"
	    + "      \"date\": \"2021-01-12\",\r\n"
	    + "      \"uid\": \"attaluri\",\r\n"
	    + "      \"type\": \"TechEsc\"\r\n"
	    + "    },\r\n"
	    + "    {\r\n"
	    + "      \"date\": \"2021-01-13\",\r\n"
	    + "      \"uid\": \"setrakya\",\r\n"
	    + "      \"type\": \"TechEsc\"\r\n"
	    + "    },\r\n"
	    + "    {\r\n"
	    + "      \"date\": \"2021-01-14\",\r\n"
	    + "      \"uid\": \"kaunanda\",\r\n"
	    + "      \"type\": \"TechEsc\"\r\n"
	    + "    },\r\n"
	    + "    {\r\n"
	    + "      \"date\": \"2021-01-15\",\r\n"
	    + "      \"uid\": \"prtisha\",\r\n"
	    + "      \"type\": \"TechEsc\"\r\n"
	    + "    },\r\n"
	    + "    {\r\n"
	    + "      \"date\": \"2021-01-16\",\r\n"
	    + "      \"uid\": \"kaunanda\",\r\n"
	    + "      \"type\": \"TechEsc\"\r\n"
	    + "    }\r\n"
	    + "  ]\r\n"
	    + "}\r\n"
	    + "";

    private void run() throws Exception {
	Random random = new Random();
//	List<Holiday> holidays = DateStream.get(START_DATE, 3).map(date -> new Holiday(date))
//		.collect(Collectors.toList());
//	Json.print(holidays);
//	EngineerFiles.AMAZON_HOLIDAYS.write(w -> w.CSV(holidays, Holiday.class).noOpen());

	holidays = EngineerFiles.AMAZON_HOLIDAYS
		.readCSVToPojo(Holiday.class)
		.stream()
		.map(Holiday::getDate)
		.map(date -> Dates.ONLINE_SCHEDULE.convertFormat(date, Dates.SORTABLE))
		.collect(Collectors.toList());

	OnCallContainer schedule;
	if (TEST_DATA) {
	    schedule = new Gson().fromJson(TEST_SCHEDULE, OnCallContainer.class);

	} else {
	    List<OnCallScheduleRow> foo1 = Oncall.TechEsc
		    .getOnCallScheduleStream().collect(Collectors.toList());
	    schedule = new OnCallContainer(foo1);

	}

	List<OnCallScheduleRow> existingSchedule = schedule
		.getSchedule()
		.stream()
		.filter(x -> x.before(START_DATE))
		.collect(Collectors.toList());

	List<Engineer> techEscs = EngineerFiles.TECH_ESC.readCSVToPojo(Engineer.class);
	String endDate = Dates.SORTABLE.getFormattedDelta(START_DATE, 28);
	DateStream.get(START_DATE, endDate).forEach(date -> {
	    int dateType = getDateType(date);

	    TechEscMetric techEsc = techEscs.stream()
		    .map(te -> new TechEscMetric(te, date, dateType, existingSchedule, random.nextInt(100)))
		    .min(Comparator.comparing(x -> x))
		    .get();

	    existingSchedule.add(new OnCallScheduleRow(date, techEsc.getUid()));
	});
	List<CitScheduleRow> newSchedule = existingSchedule
		.stream()
		.map(CitScheduleRow::new)
		.filter(x -> x.after(START_DATE))
		.collect(Collectors.toList());
	EngineerFiles.TECH_ESC_ONLINE_SCHEDULE.write(w -> w.json(newSchedule));
    }

    private int getDateType(String date) {

	if (holidays.contains(date)) {
	    return 8;
	}

	if (holidays.contains(Dates.SORTABLE.getFormattedDelta(date, -1))) {
	    return 9;
	}

	return Dates.SORTABLE.getDayOfWeek(date);
    }

    private class OnCallContainer {
	List<OnCallScheduleRow> schedule;

	public OnCallContainer(List<OnCallScheduleRow> schedule) {
	    this.schedule = schedule;
	}

	public List<OnCallScheduleRow> getSchedule() {
	    return schedule;
	}

    }

    private static class Holiday {
	private String date;

	public String getDate() {
	    return date;
	}

    }

    private class TechEscMetric implements Comparable<TechEscMetric> {

	private int dateType;
	private String uid;
	private String lastScheduledDate = null;
	private int numberOfPreviousDates = 0;
	private int randomSort;
	private int scheduleGap;

	public TechEscMetric(Engineer techEsc, String date, int dateType, List<OnCallScheduleRow> existingSchedule,
		int randomSort) {
	    this.randomSort = randomSort;
	    this.uid = techEsc.getUid();
	    this.dateType = dateType;

	    existingSchedule
		    .stream()
		    .forEach(this::scheduleDate);

	    scheduleGap = Optional
		    .ofNullable(lastScheduledDate)
		    .map(lastDate -> Dates.SORTABLE.getDifference(lastScheduledDate, date))
		    .orElse(1000);
	}

	public void scheduleDate(OnCallScheduleRow scheduleRow) {
	    if (!scheduleRow.getUid().equals(uid)) {
		return;
	    }

	    String scheduleDate = scheduleRow.getDate();

	    lastScheduledDate = Optional
		    .ofNullable(lastScheduledDate)
		    .filter(lastDate -> lastDate.compareTo(scheduleDate) > 0)
		    .orElse(scheduleDate);

	    if (getDateType(scheduleDate) == dateType) {
		numberOfPreviousDates++;
	    }
	}

	public String getUid() {
	    return uid;
	}

	@Override
	public int compareTo(TechEscMetric o) {

	    Stream<Supplier<Integer>> comparators = Stream.of(
		    () -> numberOfPreviousDates - o.numberOfPreviousDates,
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