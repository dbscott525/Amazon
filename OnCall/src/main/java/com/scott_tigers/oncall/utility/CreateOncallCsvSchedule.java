package com.scott_tigers.oncall.utility;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.OnlineScheduleEvent;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.EngineerType;

public class CreateOncallCsvSchedule extends Utility implements Command {

    public static void main(String[] args) throws Exception {
	new CreateOncallCsvSchedule().run();
    }

    public void run() throws Exception {

	EngineerFiles.ON_CALL_SCHEDULE.write(w -> w.CSV(
		getOnCallStream()
			.distinct()
			.collect(Collectors.toList()),
		"startDate", "uid"));
    }

    private Stream<OnlineScheduleEvent> getOnCallStream() {
	return Stream
		.of(EngineerType.values())
		.flatMap(EngineerType::getOnCallScheduleStream);
    }
}