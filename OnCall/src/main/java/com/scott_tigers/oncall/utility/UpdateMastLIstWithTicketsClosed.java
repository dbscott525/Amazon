package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.Optional;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.bean.EngineerMetric;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Json;

public class UpdateMastLIstWithTicketsClosed extends Utility {

    public static void main(String[] args) throws Exception {
	new UpdateMastLIstWithTicketsClosed().run();
    }

    private void run() throws Exception {
	getMetricMap();

	List<Engineer> engineers = EngineerFiles.MASTER_LIST.readCSV();

	engineers.stream().forEach(this::setLevel);
	Json.print(engineers);

	EngineerFiles.MASTER_LIST.replaceEngineerList(engineers);
	successfulFileCreation(EngineerFiles.MASTER_LIST);
    }

    private void setLevel(Engineer eng) {
	eng
		.setLevel(Optional
			.ofNullable(metricMap.get(eng.getUid()))
			.map(EngineerMetric::getTicketsPerWeek)
			.orElse(0.0)
			+ eng.getLevelAdjust());
    }

}
