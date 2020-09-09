package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.Optional;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.bean.EngineerMetric;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class UpdateMasterListWithTicketsClosed extends Utility implements Command {

    public static void main(String[] args) throws Exception {
	new UpdateMasterListWithTicketsClosed().run();
    }

    @Override
    public void run() throws Exception {
	getMetricMap();

	List<Engineer> engineers = EngineerFiles.MASTER_LIST.readCSV();

	engineers.stream().forEach(this::setLevel);

	EngineerFiles.MASTER_LIST
		.write(w -> w
			.CSV(engineers, Engineer.class)
			.archive());
    }

    private void setLevel(Engineer eng) {
	eng.setLevel(Optional
		.ofNullable(metricMap.get(eng.getUid()))
		.map(EngineerMetric::getTicketsPerWeek)
		.orElse(0.0)
		+ eng.getLevelAdjust());
    }

}
