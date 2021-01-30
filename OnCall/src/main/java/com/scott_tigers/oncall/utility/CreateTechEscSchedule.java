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

    private static final String START_DATE = "2021-02-07";

    private static final boolean TEST_DATA = false;

    public static void main(String[] args) throws Exception {
	new CreateTechEscSchedule().run();
    }

    private List<String> holidays;

    private Random random;

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
	random = new Random();

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
	    schedule = new OnCallContainer(Oncall.TechEsc
		    .getOnCallScheduleStream()
		    .collect(Collectors.toList()));

	}

	List<OnCallScheduleRow> existingSchedule = schedule
		.getSchedule()
		.stream()
		.filter(x -> x.before(START_DATE))
		.collect(Collectors.toList());

	List<Engineer> techEscs = EngineerFiles.TECH_ESC.readCSVToPojo(Engineer.class);
	String endDate = Dates.SORTABLE.getFormattedDelta(START_DATE, 28);
	DateStream.get(START_DATE, endDate)
		.forEach(date -> {

		    TechEscMetric techEsc = techEscs.stream()
			    .map(te -> new TechEscMetric(te, date, existingSchedule))
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

    private class OnCallContainer {
	List<OnCallScheduleRow> schedule;

	public OnCallContainer(List<OnCallScheduleRow> schedule) {
	    this.schedule = schedule;
	}

	public List<OnCallScheduleRow> getSchedule() {
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

    private class TechEscMetric implements Comparable<TechEscMetric> {

	private String uid;
	private String lastScheduledDate = null;
	private int numberOfPreviousDates = 0;
	private int randomSort;
	private int scheduleGap;
	private int dayOfWeek;
	private boolean holiday;
	private int numberOfHolidays = 0;

	public TechEscMetric(Engineer techEsc, String date, List<OnCallScheduleRow> existingSchedule) {
	    this.randomSort = random.nextInt(100);
	    this.uid = techEsc.getUid();
	    dayOfWeek = getDayOfWeek(date);
	    holiday = isHoliday(date);

	    existingSchedule
		    .stream()
		    .filter(scheduleRow -> scheduleRow.getUid().equals(uid))
		    .map(OnCallScheduleRow::getStartDate)
		    .forEach(this::scheduleDate);

	    scheduleGap = Optional
		    .ofNullable(lastScheduledDate)
		    .map(lastDate -> Dates.SORTABLE.getDifference(lastScheduledDate, date))
		    .orElse(1000);
	}

	private boolean isHoliday(String date) {
	    System.out.println("date=" + (date));
	    System.exit(1);
	    return holidays.contains(date);
	}

	public void scheduleDate(String scheduleDate) {

	    lastScheduledDate = Optional
		    .ofNullable(lastScheduledDate)
		    .filter(lastDate -> lastDate.compareTo(scheduleDate) > 0)
		    .orElse(scheduleDate);

	    if (getDayOfWeek(scheduleDate) == dayOfWeek) {
		numberOfPreviousDates++;
	    }

	    if (holiday && isHoliday(scheduleDate)) {
		numberOfHolidays++;
	    }
	}

	private int getDayOfWeek(String date) {
	    return Dates.SORTABLE.getDayOfWeek(date);
	}

	public String getUid() {
	    return uid;
	}

	@Override
	public int compareTo(TechEscMetric o) {

	    Stream<Supplier<Integer>> comparators = Stream.of(
		    () -> numberOfHolidays - o.numberOfHolidays,
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