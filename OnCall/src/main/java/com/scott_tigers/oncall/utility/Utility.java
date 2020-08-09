package com.scott_tigers.oncall.utility;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
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
import com.scott_tigers.oncall.test.Company;

public class Utility {

    protected List<Engineer> masterList;
    private Map<String, Engineer> uidToEngMap;
    private List<Integer> assignedTicketIds;
    private Map<EngineerFiles, List<String>> companyListMap = new HashMap<>();
    private Map<EngineerFiles, List<Engineer>> fileTypeToListMap = new HashMap<>();
    private Map<String, Double> uidToLevelMap = null;

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
	fileType.writeCSV(emailList
		.stream()
		.collect(Collectors.groupingBy(OnCallScheduleRow::getDate))
		.entrySet()
		.stream()
		.map(EmailsByDate::new)
		.sorted(Comparator.comparing(EmailsByDate::getDate))
		.collect(Collectors.toList()), EmailsByDate.class);

	successfulFileCreation(fileType);
    }

    protected List<OnCallScheduleRow> getOnCallSchedule() {
	return EngineerFiles.ON_CALL_SCHEDULE
		.readCSVToPojo(OnCallScheduleRow.class)
		.stream()
		.map(OnCallScheduleRow::canonicalDate)
		.collect(Collectors.toList());
    }

    protected Engineer getEngineer(String uid) {
	if (uidToEngMap == null) {
	    EngineerFiles fileType = EngineerFiles.MASTER_LIST;
	    List<Engineer> readCSVToPojo = readCSVByType(fileType);
	    uidToEngMap = readCSVToPojo
		    .stream()
		    .collect(Collectors.toMap(Engineer::getUid, x -> x));
	}

	return uidToEngMap.get(uid);
    }

    protected List<Engineer> readCSVByType(EngineerFiles fileType) {
	List<Engineer> engineers = fileTypeToListMap.get(fileType);

	if (engineers == null) {
	    engineers = fileType
		    .readCSVToPojo(Engineer.class);
	    fileTypeToListMap.put(fileType, engineers);
	}
	return engineers;
    }

    protected Optional<ScheduleRow> getScheduleForThisWeek() {
	return getScheduleRowStream()
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

    protected String launchUrlAndWaitForDownload(String url) {

	try {
	    File newestFile = getNewestFile();
	    File downloadedFile = newestFile;

	    launchUrl(url);
	    while (downloadedFile.compareTo(newestFile) == 0) {
		downloadedFile = getNewestFile();
		System.out.println("downloadedFile=" + (downloadedFile));
		TimeUnit.SECONDS.sleep(3);
	    }

	    return downloadedFile.toString();
	} catch (Exception e) {
	    return null;
	}
    }

    private File getNewestFile() {
	return Stream
		.of(Paths.get(System.getenv("HOMEDRIVE") + System.getenv("HOMEPATH"), "Downloads")
			.toFile()
			.listFiles())
		.sorted(Comparator.comparing(File::lastModified))
		.reduce((first, second) -> second).orElse(null);
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

    protected boolean notAssigned(TT tt) {
	assignedTicketIds = Optional
		.ofNullable(assignedTicketIds)
		.orElseGet(() -> getAssingedTicketIds());
//	assignedTicketIds = Optional
//		.ofNullable(assignedTicketIds)
//		.orElse(getAssingedTicketIds());

	return !assignedTicketIds.contains(tt.getIntCaseId());
    }

    private List<Integer> getAssingedTicketIds() {
	try {
	    String fileName = launchUrlAndWaitForDownload(
		    "https://quip-amazon.com/nhftAqjImkQT/Customer-Issues-Ticket-Tracker");
	    System.out.println("fileName=" + (fileName));
	    return Files.readAllLines(Paths.get(fileName))
		    .stream()
		    .map(this::getCaseId)
		    .filter(this::isDigits)
		    .map(Integer::valueOf)
		    .collect(Collectors.toList());
	} catch (Exception e) {
	    return new ArrayList<Integer>();
	}
    }

    private boolean isDigits(String line) {
	return line.matches("[0-9]+");
    }

    private String getCaseId(String line) {
	return line.replaceAll(".*,https:.*com/([0-9]+).*", "$1");
    }

    protected List<String> getCompanyList(EngineerFiles companyFile) {
	List<String> list = companyListMap.get(companyFile);

	if (list != null) {
	    return list;
	}

	list = companyFile
		.readCSVToPojo(Company.class)
		.stream()
		.map(Company::getCompany)
		.collect(Collectors.toList());

	companyListMap.put(companyFile, list);

	return list;

    }

    protected boolean foundIn(String target, String searchString) {
	return target
		.toLowerCase()
		.contains(searchString
			.toLowerCase()
			.trim());
    }

    protected List<Engineer> getEngineeringDetails(List<Engineer> engineers) {
	return engineers
		.stream()
		.map(mapToEngineerDetails())
		.collect(Collectors.toList());
    }

    protected Function<Engineer, Engineer> mapToEngineerDetails() {
	return eng -> getEngineer(eng.getUid());
    }

    protected Engineer getLevel(Engineer eng) {
	uidToLevelMap = Optional
		.ofNullable(uidToLevelMap)
		.orElseGet(() -> readCSVByType(EngineerFiles.LEVELS_FROM_QUIP)
			.stream()
			.collect(Collectors.toMap(Engineer::getUid, Engineer::getLevel)));

	eng.setLevel(uidToLevelMap.get(eng.getUid()));
	return eng;
    }

    protected Stream<ScheduleRow> getScheduleRowStream() {
	return EngineerFiles
		.getScheduleRowStream()
		.map(this::getOrderedByLeve);
    }

    private ScheduleRow getOrderedByLeve(ScheduleRow scheduleRow) {
	scheduleRow.setEngineers(scheduleRow
		.getEngineers()
		.stream()
		.map(this::getLevel)
		.sorted(Comparator.comparing(Engineer::getLevel).reversed())
		.collect(Collectors.toList()));
	return scheduleRow;
    }

    protected void writeTickets(EngineerFiles fileType, List<TT> ticketList, List<String> columns) {
	fileType.writeCSV(ticketList, columns);
	successfulFileCreation(fileType);
    }
}
