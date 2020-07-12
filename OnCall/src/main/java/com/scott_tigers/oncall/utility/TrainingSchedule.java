package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.shared.WeekDays;

public class TrainingSchedule {

    private List<TrainingDaySchedule> trainingDays;

    public TrainingSchedule(Entry<String, List<Engineer>> entry) {
	trainingDays = new WeekDays(entry.getKey(), 3)
		.stream()
		.sorted()
		.map(date -> new TrainingDaySchedule(date, entry.getValue()))
		.collect(Collectors.toList());
    }

    public List<TrainingDaySchedule> getTrainingDays() {
	return trainingDays;
    }

}
