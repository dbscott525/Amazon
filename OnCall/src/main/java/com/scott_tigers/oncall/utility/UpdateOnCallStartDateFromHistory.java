package com.scott_tigers.oncall.utility;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.bean.OnlineScheduleEvent;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.EngineerType;
import com.scott_tigers.oncall.shared.Json;

public class UpdateOnCallStartDateFromHistory extends Utility {

    public static void main(String[] args) {
	new UpdateOnCallStartDateFromHistory().run();
    }

    private void run() {
	Map<String, Optional<OnlineScheduleEvent>> map = EngineerType.Primary
		.getHistoricalOnCallScheduleStream()
		.collect(Collectors.groupingBy(x -> x.getUid(),
			Collectors.minBy(Comparator.comparing(x -> x.getStartDate()))));
	map.entrySet().stream()
		.forEach(x -> System.out.printf("%15s %s\n", x.getKey(), x.getValue().get().getStartDate()));

	List<Engineer> engineers = EngineerFiles.MASTER_LIST.readCSV();
	engineers.stream().forEach(eng -> {
	    Optional<OnlineScheduleEvent> optionalStartDate = map.get(eng.getUid());
	    if (optionalStartDate != null) {
		eng.setOncallStartDate(
			Dates.SORTABLE.convertFormat(optionalStartDate.get().getStartDate(), Dates.ONLINE_SCHEDULE));
		Json.print(eng);
	    }
	});

	EngineerFiles.MASTER_LIST.write(w -> w.CSV(engineers, Engineer.class));

//	Json.print(map);
    }
}
