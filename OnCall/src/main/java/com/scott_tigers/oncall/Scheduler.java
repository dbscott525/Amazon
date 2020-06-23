package com.scott_tigers.oncall;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;

public class Scheduler {

    protected Date startDate;
    private Schedule schedule;
    protected ScheduleType scheduleType;
    private Map<Integer, Double> percentileMap = new HashMap<Integer, Double>();
    private double[] sortedLevels;

    public Scheduler(ScheduleType scheduleType, Date startDate, List<Engineer> engineers) {
	this.scheduleType = scheduleType;
	this.startDate = startDate;
	sortedLevels = engineers.stream().map(Engineer::getLevel).sorted().mapToDouble(x -> x).toArray();
	engineers.forEach(engineer -> engineer.setScheduler(this));
	engineers = engineers.parallelStream().filter(scheduleType.getEngineerFilter()).collect(Collectors.toList());
	new CombinationFinder<Engineer>()
		.input(engineers)
		.resultSize(engineers.size() / scheduleType.getRotationSize() * scheduleType.getRotationSize())
		.combinationHandler(this::processCandidateSchedule)
		.generate();
    }

    private void processCandidateSchedule(List<Engineer> candidateSchedule) {
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

}
