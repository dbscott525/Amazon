package com.scott_tigers.oncall.utility;

import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.EmailsByDate;
import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.bean.OnCallScheduleRow;
import com.scott_tigers.oncall.bean.ScheduleContainer;
import com.scott_tigers.oncall.bean.ScheduleRow;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Util;

public class Utility {

    protected List<Engineer> masterList;
    private Map<String, Engineer> uidToEngMap;

    protected void successfulFileCreation(EngineerFiles fileType) {
	System.out.println(fileType.getFileName() + " was successfully created.");
	System.out.println("File is now be launched");
	fileType.launch();
    }

    protected void copyMostRecentDownloadedTTs() throws IOException {
	Util.makeCopyofMostRecentTTDownload();
    }

    protected Function<List<Engineer>, List<Engineer>> getEngineerListTransformer() {
	return list -> {

	    if (masterList == null) {
		masterList = EngineerFiles.MASTER_LIST.readCSV();
	    }

	    return list
		    .stream()
		    .map(eng -> {
			int index = masterList.indexOf(eng);
			if (index < 0) {
			    System.out.println("engineer missing from master list: " + (eng));
			    index = 0;
			}
			return masterList.get(index);
		    })
		    .collect(Collectors.toList());
	};
    }

    protected void writeEmailsByDate(List<OnCallScheduleRow> emailList, EngineerFiles fileType) {
	List<EmailsByDate> dailyEmailAddress = emailList
		.stream()
		.collect(Collectors.groupingBy(OnCallScheduleRow::getDate))
		.entrySet()
		.stream()
		.map(EmailsByDate::new)
		.sorted(Comparator.comparing(EmailsByDate::getDate))
		.collect(Collectors.toList());

	fileType.writeCSV(dailyEmailAddress, EmailsByDate.class);
	successfulFileCreation(fileType);
    }

    protected List<OnCallScheduleRow> getOnCallSchedule() {
	return EngineerFiles.ON_CALL_SCHEDULE
		.readCSVToPojo(OnCallScheduleRow.class)
		.stream()
		.map(OnCallScheduleRow::canonicalDate)
		.collect(Collectors.toList());
    }

    protected void getFullName(String uid) {
    }

    protected Engineer getEngineer(String uid) {
	if (uidToEngMap == null) {
	    uidToEngMap = EngineerFiles.MASTER_LIST
		    .readCSVToPojo(Engineer.class)
		    .stream()
		    .collect(Collectors.toMap(Engineer::getUid, x -> x));
	}

	return uidToEngMap.get(uid);
    }

    protected Optional<ScheduleRow> getScheduleForThisWeek() {
	Optional<ScheduleRow> foundSchedule = Stream
		.of(EngineerFiles.CURRENT_CUSTOMER_ISSUE_SCHEDULE, EngineerFiles.EXCECUTED_CUSTOMER_ISSUE_SCHEDULES)
		.flatMap(x -> x.readJson(ScheduleContainer.class)
			.getScheduleRows()
			.stream())
		.collect(Collectors.toList())
		.stream()
		.filter(this::forToday)
		.findFirst();
	return foundSchedule;
    }

    protected boolean forToday(ScheduleRow scheduleRow) {
	Date scheduleStartDate = Dates.SORTABLE.getDateFromString(scheduleRow.getDate());

	Date startDate = Dates.getDateDelta(scheduleStartDate, -2);
	Date endDate = Dates.getDateDelta(scheduleStartDate, 5);

	Date currentDate = new Date();
	return startDate.compareTo(currentDate) <= 0 && currentDate.compareTo(endDate) <= 0;
    }

    protected Map<String, List<Engineer>> getTraineesByDate() {
	return EngineerFiles.TRAINEES
		.readCSV()
		.stream()
		.collect(Collectors.groupingBy(Engineer::getTrainingDate));
    }

}
