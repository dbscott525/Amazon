package com.scott_tigers.oncall.newschedule;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.bean.Unavailability;
import com.scott_tigers.oncall.shared.DateStream;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class ScheduleCreator {

    interface Condition {
	boolean isTrue();
    }

    private String startDate;
    private int shiftSize;
    private List<Engineer> engineers;
    private int daysBetweenShifts = 7;
    private int daysInShift = 5;
    double maxStandardDeviation = 100.0;
    private String endDate;
    private Map<String, List<Engineer>> dateMap = new HashMap<>();
    private Schedule existingSchedule;
    private int maximumShiftFrequency;
    private Map<String, Engineer> uidMap;
    private Schedule candidateSchedule;
    private Schedule bestSchedule;
    private long iterations = 0;
    private long startTime;

    private Condition moreIterations = () -> true;
    private Condition moreTime = () -> true;

    public ScheduleCreator startDate(String startDate) {
	this.startDate = startDate;
	return this;
    }

    public void create() {

	getAndPrepareEngineers();
	search();
	writeSchedule();
    }

    private void writeSchedule() {
	EngineerFiles.CIT_SCHEDULE.writeJson(bestSchedule);
    }

    private void search() {
	startTime = System.currentTimeMillis();
	System.out.println("" + new Date() + " Start");
	getCandidateScheduleStream().forEach(schedule -> {
	    try {
		candidateSchedule = schedule;
		addShift(startDate);
		bestSchedule = candidateSchedule.getBestSchedule(bestSchedule);
	    } catch (ImpossibleScheduleCombinationException e) {
		// Nothing to do because and impossible schedule combination was found.
	    }
	});
    }

    private Stream<Schedule> getCandidateScheduleStream() {
	return StreamSupport.stream(
		Spliterators.spliteratorUnknownSize(
			new Iterator<Schedule>() {

			    @Override
			    public boolean hasNext() {
				return moreIterations.isTrue() && moreTime.isTrue();
			    }

			    @Override
			    public Schedule next() {
				iterations++;
				return new Schedule(ScheduleCreator.this);
			    }
			},
			Spliterator.ORDERED),
		false);
    }

    private void addShift(String date) throws ImpossibleScheduleCombinationException {
	if (date.compareTo(endDate) > 0) {
	    return;
	}

	List<Engineer> candidates = candidateSchedule.getCandidates(date, dateMap.get(date));
	Collections.shuffle(candidates);

	candidateSchedule.addShift(new Shift(this, date, candidates));
	addShift(Dates.SORTABLE.getFormattedDelta(date, daysBetweenShifts));
    }

    private void getAndPrepareEngineers() {
	engineers = EngineerFiles.MASTER_LIST.readCSV();
	updateEngineersOOO();
	createEngineerDateMap();
	createUidMap();
    }

    private void updateEngineersOOO() {

	Map<String, Engineer> uidToEngineer = engineers
		.stream()
		.collect(Collectors.toMap(Engineer::getUid, Function.identity()));

	engineers.forEach(eng -> {
	    eng.setOoo("");
	    eng.setShiftsCompleted(0);
	});

	EngineerFiles.UNAVAILABILITY
		.readCSVToPojo(Unavailability.class)
		.stream()
		.forEach(ua -> ua.setOoo(uidToEngineer.get(ua.getUid())));
    }

    private void createEngineerDateMap() {
	dateMap = getShiftDateStream()
		.collect(Collectors.toMap(Function.identity(), this::availableEngs));
    }

    private void createUidMap() {
	uidMap = engineers.stream().collect(Collectors.toMap(Engineer::getUid, Function.identity()));
    }

    private List<Engineer> availableEngs(String startDate) {
	return engineers.stream()
		.filter(eng -> DateStream
			.getStream(startDate, Dates.SORTABLE.getFormattedDelta(startDate, daysInShift), 1)
			.noneMatch(eng::hasDateConflict))
		.collect(Collectors.toList());
    }

    private Stream<String> getShiftDateStream() {
	return DateStream.getStream(startDate, endDate, daysBetweenShifts);
    }

    public ScheduleCreator shifts(int shifts) {
	endDate = Dates.SORTABLE.getFormattedDelta(startDate, shifts * daysBetweenShifts - 1);
	return this;
    }

    public ScheduleCreator shiftSize(int shfitSize) {
	this.shiftSize = shfitSize;
	return this;
    }

    public List<Engineer> getEngineers() {
	return engineers;
    }

    public int getShiftSize() {
	return shiftSize;
    }

    public int getDaysBetweenShifts() {
	return daysBetweenShifts;
    }

    public ScheduleCreator daysBetweenShifts(int daysBetweenShifts) {
	this.daysBetweenShifts = daysBetweenShifts;
	return this;
    }

    public String getStartDate() {
	return startDate;
    }

    public ScheduleCreator endDate(String endDate) {
	this.endDate = endDate;
	return this;
    }

    public ScheduleCreator maximumShiftFrequency(int maximumShiftFrequency) {
	this.maximumShiftFrequency = maximumShiftFrequency;
	return this;
    }

    public int getMaximumShiftFrequency() {
	return maximumShiftFrequency;
    }

    public Engineer getEngineer(String uid) {
	return uidMap.get(uid);
    }

    public Schedule getExistingSchedule() {
	return Optional
		.ofNullable(existingSchedule)
		.orElseGet(() -> {
		    existingSchedule = EngineerFiles.CIT_SCHEDULE.readJson(Schedule.class);
		    existingSchedule.setScheduleCreator(this);
		    existingSchedule.truncate();
		    existingSchedule.setScheduleCreator(ScheduleCreator.this);
		    return existingSchedule;
		});
    }

    public long getIterations() {
	return iterations;
    }

    public int getDaysInShifts() {
	return daysInShift;
    }

    public ScheduleCreator iterations(long maximumIterations) {
	moreIterations = () -> {
	    if (iterations >= maximumIterations) {
		System.out.println(String.format("%,.0f", (double) maximumIterations)
			+ " iterations complete");
		return false;

	    }
	    return true;
	};
	return this;
    }

    public ScheduleCreator timeLimit(int timeLimit) {
	long maximumElapsedMillis = TimeUnit.MINUTES.toMillis(timeLimit);
	moreTime = () -> {
	    long elapsed = System.currentTimeMillis() - startTime;
	    if (elapsed > maximumElapsedMillis) {
		System.out.println("Time limit of " + timeLimit + " minutes reached");
		return false;
	    }
	    return true;
	};
	return this;
    }

    public ScheduleCreator endAfterMonths(int months) {
	endDate = Dates.SORTABLE.addMonths(Dates.SORTABLE.getFormattedString(), months);
	System.out.println("endDate=" + (endDate));
	return this;
    }
}
