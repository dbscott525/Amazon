package com.scott_tigers.oncall.newschedule;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.bean.Unavailability;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class ScheduleCreator {

    private static final int MAXIMUM_ITERATIONS = 100000;
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
	getCandidateScheduleStream().forEach(schedule -> {
	    candidateSchedule = schedule;
	    addShift(startDate);
	    bestSchedule = candidateSchedule.getBestSchedule(bestSchedule);
	});
    }

    private Stream<Schedule> getCandidateScheduleStream() {
	return StreamSupport.stream(
		Spliterators.spliteratorUnknownSize(
			new Iterator<Schedule>() {

			    @Override
			    public boolean hasNext() {
				return iterations < MAXIMUM_ITERATIONS;
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

    private void addShift(String date) {
	if (date.compareTo(endDate) > 0) {
	    return;
	}

	List<Engineer> candidates = candidateSchedule.getCandidates(date, dateMap.get(date));

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
		.filter(eng -> getDateStream(startDate, Dates.SORTABLE.getFormattedDelta(startDate, daysInShift), 1)
			.noneMatch(eng::hasDateConflict))
		.collect(Collectors.toList());
    }

    private Stream<String> getShiftDateStream() {
	return getDateStream(startDate, endDate, daysBetweenShifts);
    }

    private Stream<String> getDateStream(String startDate, String endDate, int delta) {
	return StreamSupport.stream(
		Spliterators.spliteratorUnknownSize(
			new Iterator<String>() {
			    String currentDate = startDate;

			    @Override
			    public boolean hasNext() {
				return currentDate.compareTo(endDate) <= 0;
			    }

			    @Override
			    public String next() {
				String nextDate = currentDate;
				currentDate = Dates.SORTABLE.getFormattedDelta(currentDate, delta);
				return nextDate;
			    }
			},
			Spliterator.ORDERED),
		false);
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
}
