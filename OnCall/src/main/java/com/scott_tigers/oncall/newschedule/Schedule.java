package com.scott_tigers.oncall.newschedule;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.bean.ScheduleContainer;
import com.scott_tigers.oncall.shared.Dates;

public class Schedule {

    private List<Shift> shifts;
    private Double levelStandardDeviation;
    private transient ScheduleCreator scheduleCreator;
    private long tryCount;
//    private transient Random random = new Random();

    public Schedule(ScheduleContainer oldSchedule) {
	shifts = oldSchedule
		.getScheduleRows()
		.stream()
		.map(Shift::new)
		.collect(Collectors.toList());
    }

    public Schedule(ScheduleCreator scheduleCreator) {
	this.scheduleCreator = scheduleCreator;
	shifts = new ArrayList<>(scheduleCreator.getExistingSchedule().shifts);
	tryCount = scheduleCreator.getIterations();
    }

    public double getLevelStandardDeviation() {
	return Optional
		.ofNullable(levelStandardDeviation)
		.orElseGet(() -> {
		    levelStandardDeviation = new StandardDeviation(false)
			    .evaluate(shifts
			        .stream()
			        .filter(s -> !s.isBefore(scheduleCreator.getStartDate()))
			        .mapToDouble(Shift::getSumOfLevels)
			        .toArray());
		    return levelStandardDeviation;
		});
    }

    public void truncate() {
	shifts = shifts
		.stream()
		.filter(s -> s.isBefore(scheduleCreator.getStartDate()))
		.collect(Collectors.toList());

    }

    public void addShift(Shift shift) {
	shifts.add(shift);
    }

    public void setScheduleCreator(ScheduleCreator scheduleCreator) {
	this.scheduleCreator = scheduleCreator;
	shifts.stream().forEach(shift -> shift.setScheduleCreator(scheduleCreator));
    }

    public List<Engineer> getCandidates(String date, List<Engineer> candidateEngineers)
	    throws ImpossibleScheduleCombinationException {
	Integer index = Optional
		.ofNullable(shifts.size() - scheduleCreator.getMaximumShiftFrequency())
		.filter(i -> i > 0)
		.orElse(0);

	Map<Engineer, Engineer> excludedEngineers = shifts
		.subList(index, shifts.size())
		.stream()
		.map(Shift::getEngineers)
		.flatMap(List<Engineer>::stream)
		.distinct()
		.collect(Collectors.toMap(Function.identity(), Function.identity()));

	Random random = new Random();

	Map<Engineer, Long> shiftCounts = getShiftCounts();

	DuplicateSmeEliminator duplicateSmeEliminator = new DuplicateSmeEliminator();

	List<Engineer> finalList = candidateEngineers
		.stream()
		.filter(Predicate.not(excludedEngineers::containsKey))
		.map(engineer -> new RandomUid(engineer, shiftCounts.get(engineer), random.nextDouble()))
		.sorted()
		.map(RandomUid::getEngineer)
		.filter(engineer -> duplicateSmeEliminator.notDuplicate(engineer))
		.collect(Collectors.toList());

	if (finalList.size() < scheduleCreator.getShiftSize()) {
	    throw new ImpossibleScheduleCombinationException();
	}

	return finalList
		.subList(0, scheduleCreator.getShiftSize());
    }

    private Map<Engineer, Long> getShiftCounts() {
	String startDate = Dates.SORTABLE.getFormattedDelta(scheduleCreator.getStartDate(),
		scheduleCreator.getDaysInShifts());
	Map<Engineer, Long> scheduleCount = shifts
		.stream()
		.map(Shift::getEngineers)
		.flatMap(List<Engineer>::stream)
		.filter(eng -> !eng.afterEndDate(startDate))
		.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
	return scheduleCount;
    }

    public Schedule getBestSchedule(Schedule currentBestSchedule) {
	if (currentBestSchedule == null || isBetter(currentBestSchedule)) {
	    new DecimalFormat("#.000").format(getLevelStandardDeviation());
	    System.out.println(new Date() + " Try="
		    + String.format("%,12.0f", (double) tryCount)
		    + " STD=" + new DecimalFormat("0.000000000").format(getLevelStandardDeviation()));
	    return this;
	} else {
	    return currentBestSchedule;
	}
    }

    private boolean isBetter(Schedule schedule) {
	return schedule.getCompositeStandarDeviation() > getCompositeStandarDeviation();
    }

    private double getCompositeStandarDeviation() {
	return getLevelStandardDeviation();
    }

    public List<Shift> getShifts() {
	return shifts;
    }

    private class RandomUid implements Comparable<RandomUid> {

	private Engineer engineer;
	private int shifts;
	private double random;

	public RandomUid(Engineer engineer, Long shifts, double random) {
	    this.engineer = engineer;
	    this.shifts = Optional
		    .ofNullable(shifts)
		    .map(Long::intValue)
		    .orElse(0);
	    this.random = random;
	}

	@Override
	public int compareTo(RandomUid o) {

	    if (engineer.getRequiredOrder() != o.engineer.getRequiredOrder()) {
		return engineer.getRequiredOrder() - o.engineer.getRequiredOrder();
	    }

	    if (shifts != o.shifts) {
		return shifts - o.shifts;
	    }

//	    return 0;

	    return (int) ((random - o.random) * 10000);
	}

	public Engineer getEngineer() {
	    return engineer;
	}

    }

    private class DuplicateSmeEliminator {
	private List<String> foundSMEs = new ArrayList<>();

	public boolean notDuplicate(Engineer engineer) {
	    String expertise = engineer.getExpertise();
	    if (expertise.length() == 0) {
		return true;
	    }

	    if (foundSMEs.contains(expertise)) {
		return false;
	    }

	    foundSMEs.add(expertise);

	    return true;
	}

    }
}
