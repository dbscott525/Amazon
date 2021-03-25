package com.amazon.amsoperations.utility;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.amazon.amsoperations.bean.Engineer;
import com.amazon.amsoperations.bean.OnlineScheduleEvent;
import com.amazon.amsoperations.shared.Dates;
import com.amazon.amsoperations.shared.EngineerFiles;
import com.amazon.amsoperations.shared.EngineerType;
import com.amazon.amsoperations.shared.Json;

public class UpdateOnCallStartDateFromHistory extends Utility {

//    private static final EngineerType ENGINEER_TYPE = EngineerType.Primary;
    private static final EngineerType ENGINEER_TYPE = EngineerType.TechEsc;

    public static void main(String[] args) {
	new UpdateOnCallStartDateFromHistory().run();
    }

    private void run() {
	Map<String, Optional<OnlineScheduleEvent>> map = ENGINEER_TYPE
		.getHistoricalOnCallScheduleStream()
		.collect(Collectors.groupingBy(x -> x.getUid(),
			Collectors.minBy(Comparator.comparing(x -> x.getStartDate()))));
	map.entrySet().stream()
		.forEach(x -> System.out.printf("%15s %s\n", x.getKey(), x.getValue().get().getStartDate()));

	List<Engineer> engineers = EngineerFiles.MASTER_LIST.readCSV();
	engineers.stream().forEach(eng -> {
	    Optional<OnlineScheduleEvent> optionalStartDate = map.get(eng.getUid());
	    if (optionalStartDate != null) {
		System.out
			.println("optionalStartDate.get().getStartDate()=" + (optionalStartDate.get().getStartDate()));
		eng.setOncallStartDate(
			Dates.SORTABLE.convertFormat(optionalStartDate.get().getStartDate(), Dates.ONLINE_SCHEDULE));
		Json.print(eng);
	    }
	});

	EngineerFiles.MASTER_LIST.write(w -> w.CSV(engineers, Engineer.class));

//	Json.print(map);
    }
}
