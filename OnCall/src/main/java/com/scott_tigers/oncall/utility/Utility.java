package com.scott_tigers.oncall.utility;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.EmailsByDate;
import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.bean.OnCallScheduleRow;
import com.scott_tigers.oncall.bean.ScheduleRow;
import com.scott_tigers.oncall.bean.TT;
import com.scott_tigers.oncall.shared.Constants;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class Utility {

    protected List<Engineer> masterList;
    private Map<String, Engineer> uidToEngMap;
    private List<Integer> assignedTicketIds;

    protected void successfulFileCreation(EngineerFiles fileType) {
	System.out.println(fileType.getFileName() + " was successfully created.");
	System.out.println("File is now be launched");
	fileType.launch();
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
	return EngineerFiles
		.getScheduleRowsStream()
		.filter(this::forToday)
		.findFirst();
    }

    protected boolean forToday(ScheduleRow scheduleRow) {
	Date scheduleStartDate = Dates.SORTABLE.getDateFromString(scheduleRow.getDate());

	Date startDate = Dates.getDateDelta(scheduleStartDate, -2);
	Date endDate = Dates.getDateDelta(scheduleStartDate, 5);

	Date currentDate = new Date();
	return startDate.compareTo(currentDate) <= 0 && currentDate.compareTo(endDate) <= 0;
    }

    protected Map<String, List<Engineer>> getTraineesByDate() {
	return EngineerFiles.MASTER_LIST
		.readCSV()
		.stream()
		.filter(x -> x.getType().equals(Constants.ENGINEER_TYPE_TRAINEE))
		.collect(Collectors.groupingBy(Engineer::getTrainingDate));
    }

    protected Stream<TT> getTicketStreamFromUrl(String url) throws Exception {
	return getTicketStream(launchUrlAndWaitForDownload(url));
    }

    private String launchUrlAndWaitForDownload(String url)
	    throws Exception {
	String previousTTFileName = getLatestTTFileName();
	String ttFileName = previousTTFileName;

	launchUrl(url);

	while (previousTTFileName.equals(ttFileName)) {
	    TimeUnit.SECONDS.sleep(3);
	    ttFileName = getLatestTTFileName();
	}
	return ttFileName;
    }

    protected void launchUrl(String url) throws IOException, URISyntaxException {
	System.out.println("url=" + (url));
	java.awt.Desktop
		.getDesktop()
		.browse(new URI(url));
    }

    private Stream<TT> getTicketStream(String ttFileName) throws IOException {
	List<String> lines = Files.readAllLines(Paths.get(ttFileName), Charset.forName("ISO-8859-1"));
	lines.remove(0);

	EngineerFiles.TT_DOWNLOAD.writeLines(lines);

	Stream<TT> ticketStream = EngineerFiles.TT_DOWNLOAD
		.readCSVToPojo(TT.class)
		.stream();
	return ticketStream;
    }

    private String getLatestTTFileName() throws IOException {
	String homePath = System.getenv("HOMEDRIVE") + System.getenv("HOMEPATH");
	Path path = Paths.get(homePath, "Downloads");

	String latestTTFile;

	try (Stream<Path> downloadFileList = Files.list(path)) {
	    latestTTFile = downloadFileList
		    .filter(this::isTT)
		    .sorted()
		    .reduce((first, second) -> second)
		    .orElse(null)
		    .toString();

	}
	return latestTTFile;
    }

    private boolean isTT(Path path) {
	return path.getFileName().toString().matches("^ticket_results - .*\\.csv");
    }

    protected boolean notAssigned(TT tt) {
	assignedTicketIds = Optional
		.ofNullable(assignedTicketIds)
		.orElse(EngineerFiles.ASSIGNED_TICKETS
			.readCSVToPojo(TT.class)
			.stream()
			.map(TT::getUrl)
			.filter(url -> url.matches("https://tt.amazon.com/[0-9]+"))
			.map(url -> url.replaceAll("https://tt.amazon.com/0?([0-9]+)", "$1"))
			.map(Integer::valueOf)
			.collect(Collectors.toList()));

	return !assignedTicketIds.contains(tt.getCaseId());
    }

//    protected Stream<ScheduleRow> getScheduledRowsStream() {
//        return EngineerFiles.CUSTOMER_ISSUE_TEAM_SCHEDULE
//        	.readJson(ScheduleContainer.class)
//        	.getScheduleRows()
//        	.stream();
//    }
}
