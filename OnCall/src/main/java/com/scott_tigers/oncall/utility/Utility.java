package com.scott_tigers.oncall.utility;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.Email;
import com.scott_tigers.oncall.bean.EmailsByDate;
import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.bean.EngineerMetric;
import com.scott_tigers.oncall.bean.LTTRTicket;
import com.scott_tigers.oncall.bean.OnCallScheduleRow;
import com.scott_tigers.oncall.bean.ScheduleRow;
import com.scott_tigers.oncall.bean.TT;
import com.scott_tigers.oncall.bean.WIP;
import com.scott_tigers.oncall.schedule.Schedule;
import com.scott_tigers.oncall.schedule.Shift;
import com.scott_tigers.oncall.shared.Constants;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.EngineerType;
import com.scott_tigers.oncall.shared.Executor;
import com.scott_tigers.oncall.shared.Oncall;
import com.scott_tigers.oncall.shared.URL;
import com.scott_tigers.oncall.test.Company;

public class Utility {

    protected List<Engineer> masterList;
    private Map<String, Engineer> uidToEngMap;
    private List<Integer> assignedTicketIds;
    private Map<EngineerFiles, List<String>> companyListMap = new HashMap<>();
    private Map<EngineerFiles, List<Engineer>> fileTypeToListMap = new HashMap<>();
    private Map<String, Double> uidToLevelMap = null;
    protected String templateDoc;
    protected Map<String, EngineerMetric> metricMap = new HashMap<>();
    protected int wip;

    protected void successfulFileCreation(EngineerFiles fileType) {
	successfulFileCreation(fileType, fileType.getFileName());
    }

    protected void successfulFileCreation(EngineerFiles fileType, String fileName) {
	System.out.println(fileName + " was successfully created.");
	System.out.println("File is now being launched");
	fileType.launch(fileName);
    }

    protected Function<List<Engineer>, List<Engineer>> getEngineerListTransformerDeprecated() {
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
	List<EmailsByDate> emailLines = emailList
		.stream()
		.collect(Collectors.groupingBy(OnCallScheduleRow::getStartDate))
		.entrySet()
		.stream()
		.map(EmailsByDate::new)
		.sorted(Comparator.comparing(EmailsByDate::getDate))
		.collect(Collectors.toList());

	fileType.write(w -> w.CSV(emailLines, EmailsByDate.class));
    }

    protected List<OnCallScheduleRow> getOnCallSchedule() {
	return EngineerFiles.ON_CALL_SCHEDULE
		.readCSVToPojo(OnCallScheduleRow.class)
		.stream()
		.map(OnCallScheduleRow::canonicalDate)
		.collect(Collectors.toList());
    }

    protected Engineer getEngineer(String uid) {
	return Optional
		.ofNullable(uidToEngMap)
		.orElseGet(() -> {
		    uidToEngMap = readCSVByType(EngineerFiles.MASTER_LIST)
			    .stream()
			    .collect(Collectors.toMap(Engineer::getUid, Function.identity()));
		    return uidToEngMap;
		}).get(uid);
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

    protected Optional<Shift> getShiftForThisWeek() {
	return getShiftStream()
		.filter(this::forToday)
		.findFirst();
    }

    protected boolean forTodayDeprecated(ScheduleRow scheduleRow) {
	Date scheduleStartDate = Dates.SORTABLE.getDateFromString(scheduleRow.getDate());

	Date startDate = Dates.getDateDelta(scheduleStartDate, -2);
	Date endDate = Dates.getDateDelta(scheduleStartDate, 5);

	Date currentDate = new Date();
	return startDate.compareTo(currentDate) <= 0 && currentDate.compareTo(endDate) <= 0;
    }

    protected Optional<Shift> getScheduleForThisWeek() {
	return getShiftStream()
		.filter(this::forToday)
		.findFirst();
    }

    protected boolean forToday(Shift shift) {
	return shift.getDate().compareTo(Dates.SORTABLE.getClosestMonday()) == 0;
    }

    protected Map<String, List<Engineer>> getTraineesByDate() {
	return EngineerFiles.MASTER_LIST
		.readCSV()
		.stream()
		.filter(Engineer::isValidTrainingDate)
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
//		System.out.println("downloadedFile=" + (downloadedFile));
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

    protected void launchUrl(String url) {
	try {
	    System.out.println("url=" + (url));
	    java.awt.Desktop
		    .getDesktop()
		    .browse(new URI(url));
	} catch (Exception e) {
	    System.out.println("Cannot lauch " + url + " because of " + e);
	}
    }

    private Stream<TT> getTicketStream(String ttFileName) throws IOException {
	System.out.println("ttFileName=" + (ttFileName));
	Class<TT> pojoClass = TT.class;

	List<String> lines = Files.readAllLines(Paths.get(ttFileName), Charset.forName("ISO-8859-1"));
	lines.remove(0);

	EngineerFiles.TT_DOWNLOAD.writeLines(lines);

	return EngineerFiles.TT_DOWNLOAD
		.readCSVToPojo(pojoClass)
		.stream();
    }

    protected boolean notAssigned(TT tt) {
	assignedTicketIds = Optional
		.ofNullable(assignedTicketIds)
		.orElseGet(() -> getAssingedTicketIds());

	boolean notAssigned = !assignedTicketIds.contains(tt.getIntCaseId());
	if (!notAssigned) {
	    wip++;
	}
	return notAssigned;
    }

    private List<Integer> getAssingedTicketIds() {
	try {
	    return readFromUrl(URL.CIT_TICKET_TRACKER, WIP.class)
		    .map(WIP::getTicketURL)
		    .map(url -> url.replaceAll(".*?(\\d).*?", "$1"))
		    .filter(this::isDigits)
		    .map(Integer::parseInt)
		    .collect(Collectors.toList());
	} catch (Exception e) {
	    System.out.println("e=" + (e));
	    System.exit(1);
	    return new ArrayList<Integer>();
	}
    }

    private boolean isDigits(String line) {
	return line.matches("[0-9]+");
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

    protected List<Engineer> getEngineeringDetails(List<Engineer> engineers) {
	return engineers
		.stream()
		.map(mapToEngineerDetails())
		.collect(Collectors.toList());
    }

    protected Function<Engineer, Engineer> mapToEngineerDetails() {
//	return eng -> getEngineer(eng.getUid());
	return eng -> getEngineer(getEngineer(eng));
    }

    private Engineer getEngineer(Engineer eng) {
	return getEngineer(eng.getUid());
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

    protected Stream<ScheduleRow> getScheduleRowStreamDeprecated() {
	return EngineerFiles
		.getScheduleRowStream()
		.map(this::getOrderedByLevel);
    }

    protected Stream<Shift> getShiftStream() {
	return EngineerFiles.CIT_SCHEDULE
		.readJson(Schedule.class)
		.getShifts()
		.stream()
		.peek(shift -> shift.setEngineers(shift
			.getUids()
			.stream()
			.map(this::getEngineer)
			.sorted(Comparator.comparing(Engineer::getLevel).reversed())
			.collect(Collectors.toList())));
    }

    private ScheduleRow getOrderedByLevel(ScheduleRow scheduleRow) {
	scheduleRow.setEngineers(scheduleRow
		.getEngineers()
		.stream()
//		.map(this::getLevel)
		.map(this::getEngineer)
		.sorted(Comparator.comparing(Engineer::getLevel).reversed())
		.collect(Collectors.toList()));
	return scheduleRow;
    }

    protected void makeReplacement(String searchString, String replacement) {
	String search = Optional.ofNullable(Constants.TEMPLATE_REPLACEMENTS.get(searchString)).orElse(searchString);
	templateDoc = templateDoc.replace(search, replacement);
    }

    protected void replaceEngineers() {
	replaceEngineers(getShiftForThisWeek());
    }

    protected void replaceEngineers(Optional<Shift> shiftOfWeek) {
	shiftOfWeek.ifPresent(shift -> {
//	    List<Engineer> engineers = getEngineeringDetails(shift.getEngineers());
	    List<Engineer> engineers = shift.getEngineers();
	    List<Engineer> orderedList = new ArrayList<Engineer>(engineers);
	    Collections.shuffle(engineers);
	    IntStream.range(0, engineers.size())
		    .forEach(index -> {
			makeReplacement(engineers, index, "CIT");
			makeReplacement(orderedList, index, "CITO");
		    });
	});
    }

    void makeReplacement(List<Engineer> engineers, int index, String prefix) {
	makeReplacement(prefix + index, engineers.get(index).getFullName());
    }

    protected List<EngineerMetric> getTicketClosedMetrics() {
	try {
	    getMetricMap();
	    double average = metricMap
		    .values()
		    .stream()
		    .map(EngineerMetric::getTicketsPerWeek)
		    .mapToDouble(x -> x)
		    .average()
		    .orElse(Double.NaN);
	    System.out.println("average=" + (average));
	    metricMap.put("AVERAGE", new EngineerMetric("AVERAGE", average));
	    List<EngineerMetric> metrics = metricMap
		    .values()
		    .stream()
		    .sorted(Comparator.comparing(EngineerMetric::getTicketsPerWeek).reversed())
		    .collect(Collectors.toList());
	    return metrics;
	} catch (Exception e) {
	    return new ArrayList<EngineerMetric>();
	}

    }

    protected void getMetricMap() throws Exception {
	String lastDate = Dates.SORTABLE.getCompletedShiftMonday();
	getShiftStream()
		.filter(row -> row.isBefore(lastDate))
		.map(Shift::getEngineers)
		.flatMap(List<Engineer>::stream)
		.map(this::getEngineer)
		.filter(Engineer::isBeforeEndDate)
		.filter(Engineer::isNotServerless)
		.map(this::getEngMetric)
		.forEach(EngineerMetric::addWeek);

	getTicketStreamFromUrl(URL.CIT_RESOLVED_TICKETS)
		.filter(tt -> tt.include())
		.map(this::getMetric)
		.filter(Objects::nonNull)
		.forEach(EngineerMetric::addTicket);
    }

    private EngineerMetric getMetric(TT tt) {
	return metricMap.get(Optional
		.ofNullable(tt.getResolvedBy())
		.filter(Predicate.not(Constants.AUTOMATICIC_UID::equals))
		.orElse(tt.getLastModifiedBy()));
    }

    private EngineerMetric getEngMetric(Engineer eng) {
	return Optional
		.ofNullable(metricMap.get(eng.getUid()))
		.orElseGet(() -> {
		    EngineerMetric metric = new EngineerMetric(eng);
		    metricMap.put(eng.getUid(), metric);
		    return metric;
		});
    }

    protected List<String> getCSVSchedule() {
	return getShiftStream()
		.map(shift -> Stream
			.concat(Stream.of(
				shift.getDate()),
				shift
					.getEngineers(Constants.CIT_SHIFT_SIZE)
					.stream()
					.map(Engineer::getFullNameWithExertise))
			.collect(Collectors.joining(",")))
		.collect(Collectors.toList());
    }

    protected void createCSVCITSchedule() {
	EngineerFiles.SCHEDULE_CSV.write(w -> w.lines(getCSVSchedule()));
    }

    protected void createFileFromTemplate(EngineerFiles inputFile, EngineerFiles outputFile, Executor replacer)
	    throws IOException {
	templateDoc = inputFile.readText();
	replacer.run();
	outputFile.writeText(templateDoc);
	successfulFileCreation(outputFile);
    }

    protected <T> T constuct(Class<T> c) {
	try {
	    return c.getConstructor().newInstance();
	} catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	    return null;
	}
    }

    protected void runCommands(@SuppressWarnings("unchecked") Class<? extends Command>... commands) {
	Arrays.asList(commands)
		.stream().map(c -> constuct(c))
		.forEach(command -> {
		    try {
			command.run();
		    } catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		    }
		});
    }

    protected <T> Stream<T> readFromUrl(String url, Class<T> pojoClass) {
	return EngineerFiles
		.readCSVToPojoByFileName(launchUrlAndWaitForDownload(url), pojoClass)
		.stream();
    }

    protected void reconcileMasterListToOncallList(Oncall oncallType, EngineerType engineerType) {

	List<String> offshoreUids = EngineerFiles.OFFSHORE_UIDS.readCSV().stream().map(Engineer::getUid)
		.collect(Collectors.toList());

	List<String> emails = EngineerFiles.EMAILS
		.readCSVToPojo(Email.class)
		.stream()
		.map(Email::getEmail)
		.map(email -> email.replaceAll("(.*)?@amazon.com", "$1"))
		.filter(Predicate.not("bruscob"::equals))

		.collect(Collectors.toList());

	List<String> onCallUids = oncallType
		.getOnCallScheduleStream()
		.map(OnCallScheduleRow::getUid)
		.filter(Predicate.not(offshoreUids::contains))
		.distinct()
		.collect(Collectors.toList());

	List<String> masterListUids = EngineerFiles.MASTER_LIST
		.readCSV()
		.stream()
		.filter(eng -> eng.isType(engineerType))
		.map(Engineer::getUid)
		.filter(Predicate.not(offshoreUids::contains))
		.collect(Collectors.toList());

	compareLists(onCallUids, "On Call", masterListUids, "Master List");
	compareLists(masterListUids, "Master List", emails, "Emails");
    }

    private void compareLists(List<String> list1, String list1Title, List<String> list2, String list2Title) {
	printMissing(list1Title + " not in " + list2Title + ":", list1, list2);
	printMissing(list2Title + " UIDs not in " + list1Title + ":", list2, list1);
    }

    private void printMissing(String title, List<String> list1, List<String> list2) {
	System.out.println(title);
	list1.stream()
		.filter(Predicate.not(list2::contains))
		.forEach(System.out::println);
    }

    @SuppressWarnings("unchecked")
    protected void launchCITUpdater() {
	runCommands(
		CreateCSVSchedule.class,
		CreateCITOnlineSchedule.class,
		CreateCITEmails.class,
		OpenNextWeekCITDocuments.class);
    }

    protected Stream<LTTRTicket> getLttrQuipPlan() {
	return readFromUrl(URL.LTTR_PLAN, LTTRTicket.class);
    }

    protected Stream<LTTRTicket> getLTTRCandidates() {
	return readFromUrl(URL.LTTR_CANDIDATES, LTTRTicket.class);
    }

    protected void openLTTRDocuments() {
	launchUrl(URL.LTTR_NON_ACTIONABLE_SIMS);
	launchUrl(URL.LTTR_CANDIDATES);
	launchUrl(URL.LTTR_PLAN);
	launchUrl(URL.LTTR_TICKETS_LAST_WEEK_DELTA_REPORT);
    }

    protected List<String> getOnCallUIDs(Oncall onCallType) {
        return onCallType
        	.getOnCallScheduleStream()
        	.map(OnCallScheduleRow::getUid)
        	.distinct()
        	.sorted()
        	.collect(Collectors.toList());
    }

}
