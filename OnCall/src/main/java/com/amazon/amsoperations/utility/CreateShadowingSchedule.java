package com.amazon.amsoperations.utility;

import java.util.List;
import java.util.stream.Collectors;

import com.amazon.amsoperations.bean.TrainingDaySchedule;
import com.amazon.amsoperations.bean.TrainingSchedule;
import com.amazon.amsoperations.shared.EngineerFiles;
import com.amazon.amsoperations.shared.URL;

public class CreateShadowingSchedule extends Utility {

    public static void main(String[] args) {
	new CreateShadowingSchedule().run();
    }

    private void run() {
	EngineerFiles.TRAINING_DAILY_SCHEDULE.writeLines(getTraineesByDate()
		.entrySet()
		.stream()
		.map(TrainingSchedule::new)
		.map(TrainingSchedule::getTrainingDays)
		.flatMap(List<TrainingDaySchedule>::stream)
		.map(TrainingDaySchedule::getSchedule)
		.sorted()
		.collect(Collectors.toList()));

	successfulFileCreation(EngineerFiles.TRAINING_DAILY_SCHEDULE);
	launchUrl(URL.PRIMARY_SHADOWING_SCHEDULE);
    }

}
