package com.scott_tigers.oncall.schedule;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.scott_tigers.oncall.shared.Constants;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Executer;

public class Scheduler {

    private Schedule schedule;
    private Map<Integer, Double> percentileMap = new HashMap<Integer, Double>();
    private Executer search = () -> searchByCombination();
    private double[] sortedLevels;
    private List<Engineer> engineers;
    private int passes;
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

    private synchronized void processCandidateSchedule(List<Engineer> candidateSchedule) {
	schedule = new Schedule(this, candidateSchedule, startDate)
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
	return new Schedule(this, prefix, startDate).getBestSchedule(null) != null;
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

    public Scheduler engineers(List<Engineer> engineers) {
	this.engineers = engineers;
	originalEngineers = engineers;
	return this;
    }

    public Scheduler passes(int passes) {
	this.passes = passes;
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

	IntStream.range(0, passes).forEach(pass -> {
	    prepareForSearch();
	    int engByTeamSize = engineers.size() / teamSize;
	    resultSize = engByTeamSize * teamSize;
	    search.run();
	    scheduleRows.addAll(schedule.getScheduleRows());
	    printScheduled();
	    startDate = schedule.getNextDate();
	});
	return this;
    }

    private void prepareForSearch() {
	engineers = originalEngineers
		.parallelStream()
		.filter(engineerFilter::test)
		.sorted((eng1, eng2) -> eng2.getOoo().length() - eng1.getOoo().length())
		.collect(Collectors.toList());

	int shifts = engineers.size() / teamSize;
	List<Engineer> excludedEngineers = getOOOConflictedEngineers(shifts);
	System.out.println("EXCLUDED ENGINEERS");
	excludedEngineers.stream().forEach(eng -> System.out.println(eng.getFirstName()
		+ ": "
		+ eng.getOoo()));

	engineers = engineers.stream().filter(eng -> {
	    return !excludedEngineers.stream().map(Engineer::getFirstName)
		    .anyMatch(name -> name.equals(eng.getFirstName()));
	}).collect(Collectors.toList());
    }

    private List<Engineer> getOOOConflictedEngineers(int shifts) {
	Stream<Engineer> t1 = engineers.stream();

	Stream<Engineer> t2 = t1.filter(eng -> {
	    IntStream t3 = IntStream.range(0, shifts);

	    boolean t4 = t3.allMatch(shift -> {
		IntStream t6 = IntStream.range(0, getShiftSize());
		boolean t7 = t6.anyMatch(shiftDay -> {
		    int dayOffset = (shift * getShiftFrequency()) + shiftDay;
		    String dateOfShift = getDateString(dayOffset);
		    return eng.hasDateConflict(dateOfShift);
		});

		return t7;
	    });
	    return t4;
	});

	List<Engineer> t5 = t2.collect(Collectors.toList());

	return t5;
    }

    private void prepareEngineers() {
	sortedLevels = originalEngineers.stream().map(Engineer::getLevel).sorted().mapToDouble(x -> x).toArray();
	originalEngineers.forEach(engineer -> engineer.setScheduler(this));
	engineers = engineers.parallelStream().filter(engineerFilter::test).collect(Collectors.toList());
    }

    private void printScheduled() {
	scheduleRows.stream()
		.forEach(day -> System.out.println("Day Schedule=" + day));
    }

    private void searchByRamdom() {
	long startTimeInMillis = System.currentTimeMillis();
	long maximumElapsedMillis = TimeUnit.MINUTES.toMillis(timeLimit);

	Random random = new Random(525);
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
//	System.out.println("Number of Engineers That don't match" + (engineersWithNoMatch.count()));
	new CombinationFinder<Engineer>()
		.timeLimit(timeLimit)
		.input(sortedEngineers)
		.resultSize(resultSize)
		.combinationHandler(this::processCandidateSchedule)
		.prefixChecker(this::checkPrefix)
		.generate();
    }

    public Scheduler writeToCSV(String engineersScedhuleFile) {
	try {
	    CsvMapper mapper = new CsvMapper();
	    CsvSchema schema = mapper.schemaFor(ScheduleRow.class);
	    schema = schema.withColumnSeparator(',').withHeader();

	    // output writer
	    OutputStreamWriter writerOutputStream = new OutputStreamWriter(
		    new BufferedOutputStream(
			    new FileOutputStream(
				    new File(engineersScedhuleFile)),
			    1024),
		    "UTF-8");

	    mapper
		    .writer(schema)
		    .writeValue(writerOutputStream, scheduleRows);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return this;
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

    public void save(EngineerFiles scheduleJson) {
	ScheduleContainer scheduleContainer = new ScheduleContainer(scheduleRows);
	scheduleJson.writeJson(scheduleContainer);
    }

}
