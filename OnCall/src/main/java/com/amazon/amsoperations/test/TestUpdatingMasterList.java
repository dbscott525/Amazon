package com.amazon.amsoperations.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazon.amsoperations.bean.Engineer;
import com.amazon.amsoperations.shared.EngineerFiles;
import com.amazon.amsoperations.shared.EngineerType;
import com.amazon.amsoperations.shared.Expertise;
import com.amazon.amsoperations.shared.Json;
import com.amazon.amsoperations.utility.Utility;

public class TestUpdatingMasterList extends Utility {

    public static void main(String[] args) throws Exception {
	new TestUpdatingMasterList().run();
    }

    private void run() throws Exception {
	Map<String, String> startDates = new HashMap<>();
	getShiftStream().forEach(shift -> {
	    shift.getUids().stream().forEach(uid -> {
		String startDate = startDates.get(uid);
		if (startDate == null) {
		    startDates.put(uid, shift.getDate());
		}
	    });
	});

////	Json.print(startDates);
	List<Engineer> engineers = EngineerFiles.MASTER_LIST.readCSV();
//	engineers.stream().forEach(eng -> eng.setCit(getCitSetting(eng)));
//	Json.print(engineers);
//	EngineerFiles.MASTER_LIST.write(w -> w.CSV(engineers, Engineer.class));
//	System.exit(1);
	engineers.stream()
		.filter(Engineer::isCurrentCit)
		.forEach(eng -> {
		    String startDate = startDates.get(eng.getUid());
		    System.out.println("eng.getUid()=" + (eng.getUid()));
		    System.out.println("startDate=" + (startDate));
		    if (startDate != null) {
			eng.setFirstCitDate(startDate);
			Json.print(eng);
		    }
		});
//	Json.print(engineers);
//	engineers.stream().map(eng -> eng.getCit()).map(cit -> cit.length()).forEach(System.out::println);
//	engineers.stream().map(eng -> eng.getStartDate()).forEach(System.out::println);
//	engineers.stream().filter(eng -> eng.isCit()).map(eng -> eng.getStartDate()).forEach(System.out::println);
//	engineers.stream().map(Engineer::isCit).forEach(System.out::println);
//	engineers.stream().forEach(
//		eng -> eng.setStartDate(Dates.ONLINE_SCHEDULE.convertFormat(eng.getStartDate(), Dates.SORTABLE)));
	EngineerFiles.MASTER_LIST.write(w -> w.CSV(engineers, Engineer.class));
    }

    private String getCitSetting(Engineer eng) {
	if (

	!eng.isCurrent()
		|| Expertise.Serverless.toString().equals(eng.getExpertise())
		|| EngineerType.DublinPrimary.engineerIsType(eng)

	) {
	    return null;
	}

	return "X";
    }
}