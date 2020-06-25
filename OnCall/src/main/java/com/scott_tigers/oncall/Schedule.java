package com.scott_tigers.oncall;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

public class Schedule {

    private List<Engineer> engineers;
    private List<List<Engineer>> daySchedules;
    private Date startDate = new Date();
    private ScheduleType scheduleType;

    public Schedule(List<Engineer> candidateSchedule, Date startDate, ScheduleType scheduleType) {
	this.startDate = startDate;
	this.scheduleType = scheduleType;
	this.engineers = candidateSchedule;
	daySchedules = IntStream
		.range(0, candidateSchedule.size())
		.mapToObj(Integer::valueOf)
		.collect(scheduleCollector());
    }

    public Schedule getBestSchedule(Schedule bestSchedule) {

	boolean hasDateConflct = hasDateConflict();

	if (hasDateConflct) {
	    return bestSchedule;
	}

	if (bestSchedule == null) {
	    return this;
	}

	double standardDeviation = getStandardDeviation();
	boolean betterStandardDeviation = standardDeviation < bestSchedule.getStandardDeviation();
	if (betterStandardDeviation) {
	    System.out.println(new Date() + ": standardDeviation=" + (standardDeviation));
	}
	return betterStandardDeviation ? this : bestSchedule;

    }

    private boolean hasDateConflict() {
	return dayRange().anyMatch(day -> {
	    String date = getDateString(day);
	    return daySchedules.get(day).stream().anyMatch(eng -> {
		return eng.hasDateConflict(date);
	    });
	});
    }

    public double getStandardDeviation() {

	double[] levelSums = daySchedules.stream()
		.map(engineers -> engineers
			.stream()
			.map(Engineer::getLevel)
			.mapToDouble(Double::doubleValue)
			.sum())
		.mapToDouble(x -> x)
		.toArray();

	return new StandardDeviation(false).evaluate(levelSums);
    }

    private Collector<Integer, List<List<Engineer>>, List<List<Engineer>>> scheduleCollector() {
	Supplier<List<List<Engineer>>> supplier = () -> new ArrayList<List<Engineer>>();

	BiConsumer<List<List<Engineer>>, Integer> accumlator = (result, index) -> {
	    int i = (int) index / scheduleType.getRotationSize();
	    while (result.size() < i + 1) {
		result.add(new ArrayList<Engineer>());
	    }
	    result.get(i).add(engineers.get((int) index));
	};

	BinaryOperator<List<List<Engineer>>> combiner = (result1, result2) -> Stream
		.concat(result1.stream(), result1.stream())
		.collect(Collectors.toList());

	return Collector.of(supplier, accumlator, combiner);

    }

    @Override
    public String toString() {

	try {
	    ByteArrayOutputStream os = new ByteArrayOutputStream();
	    PrintStream ps = new PrintStream(os);
	    getScheduleString(ps);
	    return os.toString("UTF8");
	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	    return "";
	}
    }

    private void getScheduleString(PrintStream ps) {
	ps.println();
	ps.println("getStandardDeviation()=" + (getStandardDeviation()));
	dayRange()
		.forEach(day -> {
		    String engineers = daySchedules
			    .get(day)
			    .stream()
			    .map(Engineer::getName)
			    .collect(Collectors.joining(","));
		    ps.println(getAdjustedDate(day) + ": " + engineers);
		});
    }

    private String getAdjustedDate(int day) {
	return getDateString(day * scheduleType.getDaysPerInterval());
    }

    private IntStream dayRange() {
	return IntStream
		.range(0, daySchedules.size());
    }

    private String getDateString(int daySchedule) {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	Calendar c = Calendar.getInstance();
	c.setTime(startDate);
	c.add(Calendar.DATE, daySchedule);
	return sdf.format(c.getTime());
    }

    public List<ScheduleRow> getScheduleRows() {
	return dayRange().mapToObj(day -> {
	    ScheduleRow scheduleRow = new ScheduleRow(getAdjustedDate(day));

	    List<Engineer> sortedEngineesRow = daySchedules.get(day)
		    .stream()
		    .sorted(Comparator.comparingDouble(Engineer::getLevel).reversed())
		    .collect(Collectors.toList());

	    IntStream.range(0, sortedEngineesRow.size())
		    .forEach(index -> scheduleType.getEngineerMaps()
			    .get(index)
			    .accept(scheduleRow, sortedEngineesRow.get(index).getName()));

	    return scheduleRow;
	}).collect(Collectors.toList());
    }

    public Date getNextDate() {
	Calendar c = Calendar.getInstance();
	c.setTime(startDate);
	c.add(Calendar.DATE, daySchedules.size());
	return c.getTime();
    }

}