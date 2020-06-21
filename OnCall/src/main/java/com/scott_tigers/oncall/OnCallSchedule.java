package com.scott_tigers.oncall;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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

public class OnCallSchedule {

    private List<Engineer> engineers;
    private int days;
    private List<List<Engineer>> daySchedules;
    private Date startDate = new Date();

    public OnCallSchedule(Engineer[] engineers, Date startDate) {
	this.startDate = startDate;
	this.engineers = Arrays.asList(engineers);
	days = engineers.length / Constants.ON_CALLS_PER_DAY;
	daySchedules = IntStream
		.range(0, engineers.length)
		.mapToObj(Integer::valueOf)
		.collect(scheduleCollector());
    }

    public OnCallSchedule getBestSchedule(OnCallSchedule bestSchedule) {

	boolean hasDateConflct = hasDateConflict();

	if (hasDateConflct) {
	    return bestSchedule;
	}

	if (bestSchedule == null) {
	    return this;
	}

	return getStandardDeviation() > bestSchedule.getStandardDeviation() ? bestSchedule : this;

    }

    private boolean hasDateConflict() {
	return dayRange().anyMatch(day -> {
	    String date = getDateString(day);
	    return daySchedules.get(day).stream().anyMatch(eng -> eng.hasDayConflig(date));
	});
    }

    private double getStandardDeviation() {

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
	Supplier<List<List<Engineer>>> supplier = () -> IntStream
		.range(0, days)
		.mapToObj(ArrayList<Engineer>::new)
		.collect(Collectors.toList());

	BiConsumer<List<List<Engineer>>, Integer> accumlator = (result, index) -> result
		.get((int) index / Constants.ON_CALLS_PER_DAY)
		.add(engineers.get((int) index));

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
		    ps.println(getDateString(day) + ": " + engineers);
		});
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

}
