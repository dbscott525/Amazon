package com.scott_tigers.oncall.schedule;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.bean.ScheduleContainer;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.Expertise;

public class Schedule {

    private static final int MAXIMUM_SHIFT_FIND_RETRIES = 10;
    private List<Shift> shifts;
    private transient Double levelStandardDeviation;
    private transient ScheduleCreator scheduleCreator;
    private transient long tryCount;

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

    public Schedule(List<Shift> shifts) {
	this.shifts = shifts;
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

	return IntStream.rangeClosed(0, scheduleCreator.getMaximumShiftFrequency())
		.map(n -> scheduleCreator.getMaximumShiftFrequency() - n)
		.mapToObj(maximumShiftFrequency -> IntStream
			.range(0, MAXIMUM_SHIFT_FIND_RETRIES)
			.mapToObj(n -> getRandomShift(candidateEngineers, maximumShiftFrequency))
			.filter(list -> list.size() >= scheduleCreator.getShiftSize())
			.findFirst()
			.orElse(null))
		.filter(Objects::nonNull)
		.findFirst()
		.orElseThrow(ImpossibleScheduleCombinationException::new)
		.subList(0, scheduleCreator.getShiftSize());

    }

    @SuppressWarnings("unused")
    private List<Engineer> extractedExperimental(List<Engineer> candidateEngineers, int maximumShiftFrequency) {
	Random random = new Random();
	Map<Engineer, Long> shiftCounts = getShiftCounts();

	int index = Optional
		.ofNullable(shifts.size() - maximumShiftFrequency)
		.filter(i -> i > 0)
		.orElse(0);

	System.out.println("before candidateEngineers.size()=" + (candidateEngineers.size()));
	Stream<Engineer> e1 = shifts
		.subList(index, shifts.size())
		.stream()
		.map(Shift::getEngineers)
		.flatMap(List<Engineer>::stream)
		.distinct();
	Stream<RandomUid> e2 = e1
		.map(engineer -> new RandomUid(engineer, shiftCounts.get(engineer), random.nextDouble()));
	Stream<RandomUid> e3 = e2.sorted(Comparator.reverseOrder());
	Stream<RandomUid> e4 = e3.filter(r -> candidateEngineers.size() >= scheduleCreator.getShiftSize());
	e4.forEach(r -> {
	    candidateEngineers.remove(r.getEngineer());
	});
	System.out.println("aftercandidateEngineers.size()=" + (candidateEngineers.size()));

	DuplicateSmeEliminator duplicateSmeEliminator = new DuplicateSmeEliminator();

	List<Engineer> finalList = candidateEngineers
		.stream()
		.map(engineer -> new RandomUid(engineer, shiftCounts.get(engineer), random.nextDouble()))
		.sorted()
		.map(RandomUid::getEngineer)
		.filter(engineer -> duplicateSmeEliminator.notDuplicate(engineer))
		.collect(Collectors.toList());

	return finalList;
    }

    private List<Engineer> getRandomShift(List<Engineer> candidateEngineers, int maximumShiftFrequency) {
	int index = Optional
		.ofNullable(shifts.size() - maximumShiftFrequency)
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

	List<RandomUid> selected = candidateEngineers
		.stream()
		.filter(Predicate.not(excludedEngineers::containsKey))
		.map(engineer -> new RandomUid(engineer, shiftCounts.get(engineer), random.nextDouble()))
		.sorted()
		.collect(Collectors.toList());

	List<Engineer> finalList = selected
		.stream()
		.map(RandomUid::getEngineer)
		.filter(engineer -> duplicateSmeEliminator.notDuplicate(engineer))
		.collect(Collectors.toList());

	return finalList;
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
		    + " STD=" + new DecimalFormat("0.00").format(getLevelStandardDeviation()));
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

    public void setShifts(List<Shift> shifts) {
	this.shifts = shifts;
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

	    return (int) ((random - o.random) * 10000);
	}

	public Engineer getEngineer() {
	    return engineer;
	}

    }

    private class DuplicateSmeEliminator {
	private static final boolean EXPERTISE_LIMITATION = true;
	private Map<Expertise, Integer> smeMap = new HashMap<>();

	public boolean notDuplicate(Engineer engineer) {
	    if (EXPERTISE_LIMITATION) {
		Expertise expert = Expertise.get(engineer.getExpertise());

		Integer expertCount = smeMap.get(expert);
		expertCount = expertCount == null ? 1 : expertCount + 1;
		smeMap.put(expert, expertCount);

		return expert.allowedNumber(expertCount);
	    } else {
		return true;
	    }
	}

    }
}
