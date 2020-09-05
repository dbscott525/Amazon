package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.Map;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.shared.Json;

public class CreateShadowingSchedule extends Utility {

    public static void main(String[] args) {
	new CreateShadowingSchedule().run();
    }

    private void run() {
	Map<String, List<Engineer>> x1 = getTraineesByDate();
	Json.print(x1);
//	EngineerFiles.TRAINING_DAILY_SCHEDULE.writeLines(getTraineesByDate()
//		.entrySet()
//		.stream()
//		.map(TrainingSchedule::new)
//		.map(TrainingSchedule::getTrainingDays)
//		.flatMap(List<TrainingDaySchedule>::stream)
//		.map(TrainingDaySchedule::getSchedule)
//		.sorted()
//		.collect(Collectors.toList()));
//
//	successfulFileCreation(EngineerFiles.TRAINING_DAILY_SCHEDULE);
    }

}
