package com.scott_tigers.oncall.shared;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvSchema.Builder;
import com.google.gson.Gson;
import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.bean.ScheduleContainer;
import com.scott_tigers.oncall.bean.ScheduleRow;

public enum EngineerFiles {
    // @formatter:off
    
    ASSIGNED_TICKETS                   ("Assigned Tickets"),
    CIT_CANDIDATES_FROM_POOYA          ("CIT Candidates From Pooya"),
    CUSTOMER_ISSUE_BACKLOG             ("Customer Issue Backlog"),
    CUSTOMER_ISSUE_EMAIL               ("Customer Issue Emails"),
    CUSTOMER_ISSUE_TEAM_SCHEDULE       ("Customer Issue Team Schedule", Constants.JSON_EXTENSION),
    DAILY_ON_CALL_REMINDER_EMAILS      ("Daily On Call Reminder Emails"),
    DAILY_STAND_UP_ATTENDEE_EMAILS     ("Daily Stand Up Attendee Emails"), 
    DSU                                ("DSU",  Constants.XML_EXTENSION),
    DSU_TEMPLATE                       ("DSU Template", Constants.XML_EXTENSION),
    ENGINE_TICKET_DAILY_REVIEW         ("Engine Ticket Daily Review"),
    ENGINEER_ADDS                      ("Engineers to be Added"),
    ESCALATED_COMPANIES                ("Escalated Companies"),    
    ESCALATION_CHOOSER                 ("Escalation Chooser"),
    ESCALATION_OWNERSHIP               ("Escalation Ownership"),
    FOO                                ("foo"),
    FROM_ONLINE_SCHEDULE               ("From Online Schedule"),
    KEYWORD_POINTS                     ("Keyword Points"),
    LEVELS_FROM_QUIP                   ("Levels From Quip"),
    MASTER_LIST                        ("Engineer Master List"),
    NEW_LEVEL_ENGINEERS                ("New Level Engineers"),
    ON_CALL_SCHEDULE                   ("On Call Schedule"),
    ONLINE_SCHEDULE                    ("Online Schedule", Constants.JSON_EXTENSION),
    RESOLVED_TICKET_SUMMARY            ("Resolved Ticket Summary"),
    ROOT_CAUSE_TO_DO                   ("Root Cause To Do"),
    SCHEDULE_CSV                       ("Schedule"),
    SDMS                               ("SDMs"),
    SKIPPED_TICKETS                    ("Skipped Tickets"),
    TECH_ESC                           ("Tech Esc"),
    TEST                               ("Test", ".ics"),
    TICKET_FLOW_REPORT                 ("Ticket Flow Report"),
    TICKET_STATS                       ("Ticket Stats"),
    TOP_100_COMPANIES                  ("Top 100 Companies"),
    TRAINEE_EMAILS                     ("Trainee Emails"),
    TRAINEES                           ("Trainees"),
    TRAINING_DAILY_SCHEDULE	       ("Training Daily Schedule"),
    TT_DOWNLOAD                        ("TT Download"),
    UNAVAILABILITY                     ("Unavailability");
    
    // @formatter:on

    static Map<String, String> programMap = new HashMap<>() {
	private static final long serialVersionUID = 1L;
	{
	    put(Constants.XML_EXTENSION, "C:\\\\Program Files (x86)\\\\Microsoft Office\\\\Office16\\WINWORD.EXE");
	    put(Constants.CSV_EXTENSION, "C:\\Program Files (x86)\\Microsoft Office\\Office16\\EXCEL.EXE");
	}
    };

    public static ScheduleContainer getScheduleContainer() {
	return CUSTOMER_ISSUE_TEAM_SCHEDULE.readJson(ScheduleContainer.class);
    }

    public static List<ScheduleRow> getScheduledRows() {
	return getScheduleContainer().getScheduleRows();
    }

    public static Stream<ScheduleRow> getScheduleRowStream() {
	return getScheduledRows().stream();
    }

    public static void writeScheduleRows(List<ScheduleRow> scheduleRows) {
	CUSTOMER_ISSUE_TEAM_SCHEDULE.replaceJsonFile(new ScheduleContainer(scheduleRows));
//	CUSTOMER_ISSUE_TEAM_SCHEDULE.writeJsonFile(new ScheduleContainer(scheduleRows));
    }

    private String extension = Constants.CSV_EXTENSION;

    private String fileName;

    EngineerFiles(String fileName) {
	this.fileName = fileName;
    }

    EngineerFiles(String fileName, String extension) {
	this.fileName = fileName;
	this.extension = extension;
    }

    protected String extension() {
	return extension;
    }

    public String getFileName() {
	return "J:\\SupportEngineering\\OnCallData\\" + fileName + extension();
    }

    public List<String> getFirstNames() {
	return Transform.list(readCSV(), x -> x.map(Engineer::getFirstName));
    }

    public void launch() {

	try {
	    Runtime.getRuntime().exec(new String[] {
		    programMap.get(extension),
//		    "C:\\Program Files (x86)\\Microsoft Office\\Office16\\EXCEL.EXE",
		    getFileName()
	    });
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public List<Engineer> readCSV() {
	return new CSVReader<Engineer>()
		.inputFile(getFileName())
		.type(Engineer.class)
		.read();
    }

    public <T> List<T> readCSVToPojo(Class<T> pojoClass) {

	return new CSVReader<T>()
		.inputFile(getFileName())
		.type(pojoClass)
		.read();
    }

    public <T> T readJson(Class<T> clazz) {
	try {
	    return new Gson().fromJson(Files.readString(getPath(), StandardCharsets.US_ASCII), clazz);
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return null;
    }

    private Path getPath() {
	return Paths.get(getFileName());
    }

    public String readText() throws IOException {
	return Files.readString(getPath());
    }

    private boolean renameFileToTimeStampFile() {
	File file = new File(getFileName());

	if (!file.exists()) {
	    return true;
	}

	String archivePath = getArchivePath();

	boolean renameResult = file.renameTo(new File(archivePath));

	if (!renameResult) {
	    System.out.println("Cannot rename file [" + file.getPath() + "] to [" + archivePath + "]");
	}
	return renameResult;
    }

    private String getArchivePath() {
	File file = new File(getFileName());
	var regex = "(.+\\\\)(.+)(\\.)";
	String replacement = "$1Revisions\\\\$2 " + Dates.TIME_STAMP.getFormattedDate() + "$3";

	String archivePath = file
		.getPath()
		.replaceAll(regex, replacement);
	return archivePath;
    }

    public void replaceEngineerList(List<Engineer> exsitingEngineers) {
	replaceFile(() -> writeToCSVFile(exsitingEngineers));
    }

    private void replaceFile(ExceptionExecutor writer) {
	try {
	    if (renameFileToTimeStampFile()) {
		writer.run();
//		writeToCSVFile(exsitingEngineers);
	    }

	} catch (Exception e) {
	    System.out.println("e=" + (e));
	    e.printStackTrace();
	}
    }

    void replaceJsonFile(Object object) {
	replaceFile(() -> writeJsonFile(object));
    }

    public <T> void writeCSV(List<T> list, Class<T> pojoClass) {
	Function<CsvMapper, CsvSchema> t1 = mapper -> mapper.schemaFor(pojoClass);
	writeCSV(list, t1);
    }

    private <T> void writeCSV(List<T> list, Function<CsvMapper, CsvSchema> schemaMaker) {
	try {
	    CsvMapper mapper = new CsvMapper();
	    CsvSchema schema = schemaMaker.apply(mapper);
	    mapper.configure(Feature.IGNORE_UNKNOWN, true);

	    schema = schema.withColumnSeparator(',').withHeader();

	    OutputStreamWriter writerOutputStream = new OutputStreamWriter(
		    new BufferedOutputStream(
			    new FileOutputStream(
				    new File(getFileName())),
			    1024),
		    "UTF-8");

	    mapper
		    .writer(schema)
		    .writeValue(writerOutputStream, list);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public <T> void writeCSV(List<T> list, List<String> columnNames) {
	writeCSV(list, mapper -> {
	    Builder builder = CsvSchema.builder();
	    columnNames
		    .stream()
		    .forEach(builder::addColumn);
	    return builder.build();
	});
    }

    public <T> void writeJson(T object) {
	try {
	    if (renameFileToTimeStampFile()) {
		writeJsonFile(object);
	    }

	} catch (Exception e) {
	    System.out.println("e=" + (e));
	    e.printStackTrace();
	}
    }

    <T> void writeJsonFile(T object) {
	try {
	    writeText(Json.getJsonString(object));
	} catch (IOException e) {
	    System.out.println("Cannot write to file " + getFileName());
	    e.printStackTrace();
	}
    }

    public void writeLines(List<String> lines) {
	try {
	    FileUtils.writeLines(new File(getFileName()), lines);
	} catch (IOException e) {
	    System.out.println("e=" + (e));
	    e.printStackTrace();
	}
    }

    public void writeText(String text) throws IOException {
	Files.write(getPath(), text.getBytes());
    }

    private void writeToCSVFile(List<Engineer> exsitingEngineers) throws UnsupportedEncodingException,
	    FileNotFoundException, IOException, JsonGenerationException, JsonMappingException {
	CsvMapper mapper = new CsvMapper();
	CsvSchema schema = mapper.schemaFor(Engineer.class);
	schema = schema.withColumnSeparator(',').withHeader();

	OutputStreamWriter writerOutputStream = new OutputStreamWriter(
		new BufferedOutputStream(
			new FileOutputStream(
				new File(getFileName())),
			1024),
		"UTF-8");

	mapper
		.writer(schema)
		.writeValue(writerOutputStream, exsitingEngineers);
    }

    public void archive() throws IOException {
	System.out.println("getArchivePath()=" + (getArchivePath()));
	System.out.println("fileName=" + (fileName));
	FileUtils.copyFile(new File(getFileName()), new File(getArchivePath()));
    }

    public List<String> readLines() {
	Path path = getPath();
	return readLines(path);
    }

    private static List<String> readLines(Path path) {
	try {
	    return Files.readAllLines(path);
	} catch (IOException e) {
	    return new ArrayList<String>();
	}
    }

    public static List<String> readLines(String fileName) {
	return readLines(Paths.get(fileName));
    }

}
