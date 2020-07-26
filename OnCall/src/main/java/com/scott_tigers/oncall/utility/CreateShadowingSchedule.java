package com.scott_tigers.oncall.utility;

import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.TrainingDaySchedule;
import com.scott_tigers.oncall.bean.TrainingSchedule;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class CreateShadowingSchedule extends Utility {

    public static void main(String[] args) {
	new CreateShadowingSchedule().run();
    }

    private void run() {
	EngineerFiles.TRAINING_DAILY_SCHEDULE.writeLines(getTraineesByDate()
		.entrySet()
		.stream()
		.map(TrainingSchedule::new)
		.flatMap(x -> x.getTrainingDays().stream())
		.map(TrainingDaySchedule::getSchedule)
		.sorted()
		.collect(Collectors.toList()));

	successfulFileCreation(EngineerFiles.TRAINING_DAILY_SCHEDULE);
    }

}
