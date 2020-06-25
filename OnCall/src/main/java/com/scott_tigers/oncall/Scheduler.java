package com.scott_tigers.oncall;

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
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class Scheduler {

    private Schedule schedule;
    protected ScheduleType scheduleType;
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

    public Scheduler(ScheduleType scheduleType) {
	this.scheduleType = scheduleType;
    }

    private synchronized void processCandidateSchedule(List<Engineer> candidateSchedule) {
	schedule = new Schedule(candidateSchedule, startDate, scheduleType)
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
	return new Schedule(prefix, startDate, scheduleType).getBestSchedule(null) != null;
    }

    private String getDateString(int daySchedule) {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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
	prepareEngines();

	resultSize = engineers.size() / scheduleType.getRotationSize() * scheduleType.getRotationSize();

	scheduleRows = new ArrayList<ScheduleRow>();

	IntStream.range(0, passes).forEach(pass -> {
	    prepareForSearch();
	    System.out.println("pass=" + (pass));
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
		.filter(scheduleType.getEngineerFilter())
		.sorted((eng1, eng2) -> eng2.getExclusionDates().length() - eng1.getExclusionDates().length())
		.collect(Collectors.toList());

	int days = engineers.size() / scheduleType.getRotationSize();

	List<Engineer> excludedEngineers = engineers
		.stream()
		.filter(eng -> IntStream.range(0, days)
			.mapToObj(this::getDateString)
			.allMatch(eng::hasDateConflict))
		.collect(Collectors.toList());

	System.out.println("excludedEngineers=" + (excludedEngineers));

	engineers = engineers.stream().filter(eng -> {
	    return !excludedEngineers.stream().map(Engineer::getName)
		    .anyMatch(name -> name.equals(eng.getName()));
	}).collect(Collectors.toList());
    }

    private void prepareEngines() {
	sortedLevels = originalEngineers.stream().map(Engineer::getLevel).sorted().mapToDouble(x -> x).toArray();
	originalEngineers.forEach(engineer -> engineer.setScheduler(this));
	engineers = engineers.parallelStream().filter(scheduleType.getEngineerFilter()).collect(Collectors.toList());
    }

    private void printScheduled() {
	scheduleRows.stream().forEach(day -> System.out.println("Day Schedule=" + day));
    }

    private void searchByRamdom() {
	long startTimeInMillis = System.currentTimeMillis();
	long maximumElapsedMillis = TimeUnit.MINUTES.toMillis(timeLimit);

	Random random = new Random(525);
	while (true) {
	    long elapsed = System.currentTimeMillis() - startTimeInMillis;
	    if (elapsed > maximumElapsedMillis) {
		break;
	    }
	    ArrayList<Engineer> candidate = new ArrayList<Engineer>(engineers.subList(0, resultSize));
	    Collections.shuffle(candidate, random);
	    processCandidateSchedule(candidate);
//	    if (schedule != null) {
//		double std = schedule.getStandardDeviation();
//		System.out.println("Standard Deviation: " + (std));
//	    }

	}
    }

    private void searchByCombination() {
	System.out.println("Writing to CSV File");
	printScheduled();
	List<Engineer> sortedEngineers = engineers.stream()
		.sorted((eng1, eng2) -> eng2.getExclusionDates().length() - eng1.getExclusionDates().length())
		.collect(Collectors.toList());
	int days = engineers.size() / scheduleType.getRotationSize();
	Stream<Engineer> engineersWithNoMatch = sortedEngineers.stream().filter(eng -> IntStream.range(0, days)
		.mapToObj(day -> getDateString(day)).allMatch(date -> eng.hasDateConflict(date)));
	System.out.println("Number of Engineers That don't match" + (engineersWithNoMatch.count()));
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
}
