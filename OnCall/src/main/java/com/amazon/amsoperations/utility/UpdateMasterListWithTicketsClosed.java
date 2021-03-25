package com.amazon.amsoperations.utility;

import java.util.List;
import java.util.Optional;

import com.amazon.amsoperations.bean.Engineer;
import com.amazon.amsoperations.bean.EngineerMetric;
import com.amazon.amsoperations.shared.EngineerFiles;

public class UpdateMasterListWithTicketsClosed extends Utility implements Command {

    public static void main(String[] args) throws Exception {
	new UpdateMasterListWithTicketsClosed().run();
    }

    @Override
    public void run() throws Exception {

	System.out.println("Updating Master Engineer List with Tickets Closed");

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
		.orElse(0.0));
    }

}
