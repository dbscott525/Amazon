package com.scott_tigers.oncall.schedule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.bean.ScheduleRow;
import com.scott_tigers.oncall.bean.Unavailability;
import com.scott_tigers.oncall.shared.Constants;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Executor;

public class Scheduler {

    private Schedule schedule;
    private Map<Integer, Double> percentileMap = new HashMap<Integer, Double>();
    private Executor search = () -> searchByCombination();
    private double[] sortedLevels;
    private List<Engineer> engineers;
    private int timeLimit;
    private int resultSize;
    private ArrayList<ScheduleRow> scheduleRows;
    private Date startDate;
    private List<Engineer> originalEngineers;
    private int teamSize = 3;
    private Predicate<Engineer> engineerFilter = engineer -> true;
    private Supplier<Boolean> standardDeviationCheck = () -> false;
    private int shiftSize = 1;
    private int shiftFrequency = 1;
    private List<ScheduleRow> existingSchedule;
    private List<Engineer> engineersFromCsvFile;
    private Predicate<Integer> shiftFilter;
    private Map<String, Engineer> levelEngineers;
    private int shifts;
    private Map<String, SmeRange> smeRanges;
    private String newScheduleStart = Dates.SORTABLE.getFormattedString();
    private int weeksBetweenShift = 3;
    private Map<String, Engineer> uidToEngineer;
    private long schedulesTried = 0;

    private void processCandidateSchedule(List<Engineer> candidateSchedule) {
	schedulesTried++;
	schedule = new Schedule(this, candidateSchedule)
		.getBestSchedule(schedule);
    }

    public Schedule getSchedule() {
	return schedule;
    }

    public boolean isGreaterThanPercnetile(int percentile, double level) {
	return level >= getPercentileValue(percentile);
    }

    private Double getPercentileValue(int percentile) {
	Double percentileValue = percentileMap.get(percentile);
	if (percentileValue == null) {
	    percentileValue = new Percentile(percentile)
		    .withEstimationType(Percentile.EstimationType.R_5)
		    .evaluate(sortedLevels);
	    percentileMap.put(percentile, percentileValue);
	}
	return percentileValue;
    }

    public List<ScheduleRow> getScheduleRows() {
	return schedule.getScheduleRows();
    }

    private boolean checkPrefix(List<Engineer> prefix) {
	return new Schedule(this, prefix).getBestSchedule(null) != null;
    }

    private String getDateString(int daySchedule) {
	SimpleDateFormat sdf = new SimpleDateFormat(Constants.SORTABLE_DATE_FORMAT);
	Calendar c = Calendar.getInstance();
	c.setTime(startDate);
	c.add(Calendar.DATE, daySchedule);
	return sdf.format(c.getTime());
    }

    public Scheduler startDate(Date startDate) {
	this.startDate = startDate;
	return this;
    }

    public Scheduler timeLimit(int timeLimit) {
	this.timeLimit = timeLimit;
	return this;
    }

    public Scheduler searchByRandom() {
	search = () -> searchByRamdom();
	return this;
    }

    public Scheduler run() {
	prepareEngineers();

	scheduleRows = new ArrayList<ScheduleRow>();

	prepareForSearch();
	int engByTeamSize = engineers.size() / teamSize;
	resultSize = engByTeamSize * teamSize;
	search.run();
	scheduleRows.addAll(schedule.getScheduleRows());
	return this;
    }

    private void prepareForSearch() {
	shiftFilter.test(1);
	engineers = originalEngineers
		.parallelStream()
		.filter(engineerFilter::test)
		.filter(eng -> shiftFilter.test(eng.getShiftsCompleted()))
		.sorted((eng1, eng2) -> eng2.getOoo().length() - eng1.getOoo().length())
		.collect(Collectors.toList());

	updateStartDateBasedOnShiftGap();

//	shifts = 2;
	List<Engineer> excludedEngineers = getOOOConflictedEngineers(shifts);
	System.out.println("EXCLUDED ENGINEERS:");
	System.out.println();
	excludedEngineers.stream().map(eng -> "  " + eng.getFullName()).sorted().forEach(System.out::println);
	System.out.println();

	engineers = engineers
		.stream()
		.filter(eng -> !excludedEngineers
			.stream()
			.map(Engineer::getUid)
			.anyMatch(uid -> uid.equals(eng.getUid())))
		.collect(Collectors.toList());
//	shifts = (engineers.size() / teamSize) - 1;
	System.out.println("shifts=" + (shifts));
//	shifts = 2;
	System.out.println("shifts=" + (shifts));

	Map<String, Long> smeCounts = engineers.stream()
		.filter(e -> !e.getExpertise().isEmpty())
		.collect(Collectors.groupingBy(Engineer::getExpertise, Collectors.counting()));

	String smeString = smeCounts.entrySet().stream().map(entry -> entry.getKey() + ": " + entry.getValue())
		.collect(Collectors.joining(", "));
	smeRanges = smeCounts
		.entrySet()
		.stream()
		.collect(Collectors.toMap(Entry<String, Long>::getKey, SmeRange::new));

	System.out.println("Shifts: " + shifts + ", " + smeString);
//	Json.print(smeRanges);
//	System.exit(1);

    }

    private void updateStartDateBasedOnShiftGap() {
	existingSchedule
		.stream()
		.forEach(schedule -> {
		    String newStartDate = Dates.SORTABLE
			    .getFormattedDelta(schedule.getDate(), weeksBetweenShift * 7);
		    System.out.println("newStartDate=" + (newStartDate));
		    schedule
			    .getEngineers()
			    .stream()
			    .map(eng -> uidToEngineer.get(eng.getUid()))
			    .forEach(eng -> eng.candidateStartDate(newStartDate));
		});
//	Json.print(engineers);
//	System.exit(1);
    }

    private List<Engineer> getOOOConflictedEngineers(int shifts) {
	return engineers.stream()
		.filter(eng -> IntStream
			.range(0, shifts)
			.allMatch(shift -> IntStream
				.range(0, getShiftSize())
				.anyMatch(shiftDay -> eng
					.hasDateConflict(getDateString((shift * getShiftFrequency()) + shiftDay)))))
		.collect(Collectors.toList());
    }

    private void prepareEngineers() {
	readEngineers();
	createShiftsFilter();
	sortedLevels = originalEngineers
		.stream()
		.map(Engineer::getLevel)
		.sorted()
		.mapToDouble(x -> x)
		.toArray();
	originalEngineers.forEach(engineer -> engineer.setScheduler(this));
	engineers = engineers.stream()
		.filter(engineerFilter::test)
		.collect(Collectors.toList());
    }

    private void createShiftsFilter() {
	Integer min = relationalCompare(s -> s.min(Comparator.comparing(x1 -> x1)));
	shiftFilter = shifts -> shifts <= min + 1;

    }

    private Integer relationalCompare(Function<Stream<Integer>, Optional<Integer>> compareType) {
	return compareType
		.apply(engineersFromCsvFile
			.stream()
			.map(Engineer::getShiftsCompleted))
		.orElse(0);
    }

    private void readEngineers() {

	existingSchedule = EngineerFiles
		.getScheduleRowStream()
		.filter(row -> row.isBefore(newScheduleStart))
		.collect(Collectors.toList());

	engineersFromCsvFile = EngineerFiles.MASTER_LIST.readCSV();

	uidToEngineer = engineersFromCsvFile
		.stream()
		.collect(Collectors.toMap(Engineer::getUid, Function.identity()));

	populateLevels();

	engineersFromCsvFile.forEach(eng -> {
	    eng.setOoo("");
	    eng.setShiftsCompleted(0);
	});

	Map<String, Engineer> uidToEngineer = engineersFromCsvFile
		.stream()
		.collect(Collectors.toMap(Engineer::getUid, e -> e));

	EngineerFiles.UNAVAILABILITY
		.readCSVToPojo(Unavailability.class)
		.stream()
		.forEach(ua -> ua.setOoo(uidToEngineer.get(ua.getUid())));

	existingSchedule
		.stream()
		.forEach(engineerInSchedule -> engineerInSchedule
			.getEngineers()
			.stream()
			.map(eng -> uidToEngineer.get(eng.getUid()))
			.filter(Objects::nonNull)
			.forEach(Engineer::incrementShiftsCompleted));

	originalEngineers = engineersFromCsvFile;
	engineers = originalEngineers;
	computeStartDate();
    }

    private void populateLevels() {
	levelEngineers = EngineerFiles.LEVELS_FROM_QUIP
		.readCSV()
		.stream()
		.collect(Collectors.toMap(Engineer::getUid, Function.identity()));
	engineersFromCsvFile
		.stream()
		.forEach(this::updateLevel);
    }

    private void updateLevel(Engineer eng) {
	Engineer levelEngineer = levelEngineers.get(eng.getUid());
	if (levelEngineer == null) {
	    System.out.println("Engineer "
		    + eng.getUid()
		    + " does not have level data");
	} else {
	    eng.setLevel(levelEngineer.getLevel());
	}
    }

    private void computeStartDate() {
	startDate = Dates.SORTABLE
		.getDateFromString(existingSchedule
			.stream()
			.map(ScheduleRow::getDate)
			.map(x -> Dates.SORTABLE.getFormattedDelta(x, 7))
			.max(Comparator.comparing(x -> x))
			.orElse(Dates.SORTABLE.getNextMondayDate()));
	System.out.println("startDate=" + (startDate));
    }

    private void printScheduled() {
	scheduleRows.stream()
		.forEach(day -> System.out.println("Day Schedule=" + day));
    }

    private void searchByRamdom() {
	long startTimeInMillis = System.currentTimeMillis();
	long maximumElapsedMillis = TimeUnit.MINUTES.toMillis(timeLimit);

	Random random = new Random(525);
//	Random random = new Random();
	while (true) {
	    long elapsed = System.currentTimeMillis() - startTimeInMillis;
	    if (elapsed > maximumElapsedMillis) {
		System.out.println("Time limit of " + timeLimit + " minutes reached");
		break;
	    }
	    ArrayList<Engineer> candidate = new ArrayList<Engineer>(engineers.subList(0, resultSize));
	    Collections.shuffle(candidate, random);
	    processCandidateSchedule(candidate);
	    if (standardDeviationCheck.get()) {
		break;
	    }
	}
    }

    private void searchByCombination() {
	System.out.println("Writing to CSV File");
	printScheduled();
	List<Engineer> sortedEngineers = engineers.stream()
		.sorted((eng1, eng2) -> eng2.getOoo().length() - eng1.getOoo().length())
		.collect(Collectors.toList());
	int days = engineers.size() / teamSize;
	sortedEngineers.stream().filter(eng -> IntStream.range(0, days)
		.mapToObj(day -> getDateString(day)).allMatch(date -> eng.hasDateConflict(date)));
	new CombinationFinder<Engineer>()
		.timeLimit(timeLimit)
		.input(sortedEngineers)
		.resultSize(resultSize)
		.combinationHandler(this::processCandidateSchedule)
		.prefixChecker(this::checkPrefix)
		.generate();
    }

    public Scheduler teamSize(int teamSize) {
	this.teamSize = teamSize;
	return this;
    }

    public int getTeamSize() {
	return teamSize;
    }

    public Scheduler miniumPercentile(int percentile) {
	engineerFilter = engineer -> engineer.isGreaterThanPercentile(percentile);
	return this;
    }

    public Scheduler minimumStandardDeviation(double minimumStandardDeviation) {
	standardDeviationCheck = () -> {
	    boolean standardDeviationReached = schedule != null
		    && schedule.getStandardDeviation() < minimumStandardDeviation;
	    if (standardDeviationReached) {
		System.out.println("STANDARD DEVIATION ACHIEVED");
	    }
	    return standardDeviationReached;
	};
	return this;
    }

    public Scheduler shiftSize(int shiftSize) {
	this.shiftSize = shiftSize;
	return this;
    }

    public Date getStartDate() {
	return startDate;
    }

    public int getShiftSize() {
	return shiftSize;
    }

    public Scheduler shiftFrequency(int shiftFrequency) {
	this.shiftFrequency = shiftFrequency;
	return this;
    }

    public int getShiftFrequency() {
	return shiftFrequency;
    }

    public void save() {
	existingSchedule.addAll(scheduleRows);
	EngineerFiles.writeScheduleRows(existingSchedule);
    }

    private class SmeRange {

	private int min;
	private int max;

	public SmeRange(Entry<String, Long> entry) {
	    max = 1;
	    min = entry.getValue() > shifts ? 1 : 0;
//	    double averageSmePerShift = (double) entry.getValue() / shifts;
//	    max = (int) Math.ceil(averageSmePerShift);
//	    min = (int) Math.floor(averageSmePerShift);
	}

	@Override
	public String toString() {
	    return "SmeRange [min=" + min + ", max=" + max + "]";
	}

	public boolean outOfRange(Long value) {
//	    return false;
//	    return value > 1;
	    return value > max || value < min;
	}

    }

    public boolean smeOutOfRang(Entry<String, Long> entry) {
//	return false;
	return Optional
		.ofNullable(smeRanges.get(entry.getKey()))
		.filter(range -> range.outOfRange(entry.getValue()))
		.isPresent();
    }

    public Scheduler newScheduleStart(String newScheduleStart) {
	this.newScheduleStart = newScheduleStart;
	return this;
    }

    public Scheduler weeksBetweenShift(int weeksBetweenShift) {
	this.weeksBetweenShift = weeksBetweenShift;
	return this;
    }

    public int getShifts() {
	return shifts;
    }

    public long getSchedulesTried() {
	return schedulesTried;
    }

    public Scheduler shifts(int shifts) {
	this.shifts = shifts;
	return this;
    }
}
