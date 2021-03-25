package com.amazon.amsoperations.utility;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.amazon.amsoperations.bean.OnlineScheduleEvent;
import com.amazon.amsoperations.shared.EngineerFiles;
import com.amazon.amsoperations.shared.EngineerType;

public class CreateOncallCsvSchedule extends Utility implements Command {

    public static void main(String[] args) throws Exception {
	new CreateOncallCsvSchedule().run();
    }

    public void run() throws Exception {

	EngineerFiles.ON_CALL_SCHEDULE.write(w -> w.CSV(
		getOnCallStream()
//			.peek(x -> Json.print(x))
			.distinct()
			.collect(Collectors.toList()),
		"startDate", "uid"));
    }

    private Stream<OnlineScheduleEvent> getOnCallStream() {
	return Stream
		.of(EngineerType.values())
		.filter(EngineerType::useForDailyBulletin)
		.flatMap(EngineerType::getOnCallScheduleStream);
    }
}