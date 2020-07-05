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
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvSchema.Builder;
import com.google.gson.Gson;
import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.bean.TT;

public enum EngineerFiles {
    MASTER_LIST("Engineer Master List"),
    ENGINEER_ADDS("Engineers to be Added"),
    FOO("foo"),
    LEVELS_FROM_QUIP("Levels From Quip"),
    NEW_LEVEL_ENGINEERS("New Level Engineers"),
    CURRENT_SCHEDULE("Current Customer Issue Schedule") {
	@Override
	protected String extension() {
	    return ".json";
	}
    },
    SCHEDULE_CSV("Schedule"),
    CUSTOMER_ISSUE_EMAIL("Customer Issue Emails"),
    TT_DOWNLOAD("TT Download"),
    ASSIGNED_TICKETS("Assigned Tickets"),
    CUSTOMER_ISSUE_BACKLOG("Customer Issue Backlog"),
    KEYWORD_POINTS("Keyword Points"),
    TOP_100_COMPANIES("Top 100 Companies"),
    ROOT_CAUSE_TO_DO("Root Cause To Do"),
    CURRENT_CUSTOMER_ISSUE_SCHEDULE("Current Customer Issue Schedule") {
	@Override
	protected String extension() {
	    return ".json";
	}
    },
    NEW_SCHEDULE("New Schedule"),
    ONLINE_SCHEDULE("Online Schedule") {
	@Override
	protected String extension() {
	    return ".json";
	}
    },
    ENGINE_TICKET_DAILY_REVIEW("Engine Ticket Daily Review"),
    FROM_ONLINE_SCHEDULE("From Online Schedule"),
    CIT_CANDIDATES_FROM_POOYA("CIT Candidates From Pooya"),
    ON_CALL_SCHEDULE("On Call Schedule"),
    DAILY_STAND_UP_EMAILS("Daily Stand Up Emails"),
    RESOLVED_TICKET_SUMMARY("Resolved Ticket Summary");

    private String fileName;

    public String getFileName() {
	return "J:\\SupportEngineering\\OnCallData\\" + fileName + extension();
    }

    protected String extension() {
	return ".csv";
    }

    EngineerFiles(String fileName) {
	this.fileName = fileName;
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

    public void replace(List<Engineer> exsitingEngineers) {
	try {
	    if (renameFileToTimeStampFile()) {
		writeToCSVFile(exsitingEngineers);
	    }

	} catch (Exception e) {
	    System.out.println("e=" + (e));
	    e.printStackTrace();
	}
    }

    private boolean renameFileToTimeStampFile() {
	File file = new File(getFileName());

	if (!file.exists()) {
	    return true;
	}

	String timeStampPath = file
		.getPath()
		.replaceAll(
			"(.+)(\\..+)",
			"$1 " + Dates.TIME_STAMP.getFormattedDate() + "$2");

	boolean renameResult = file.renameTo(new File(timeStampPath));

	if (!renameResult) {
	    System.out.println("Cannot rename file [" + file.getPath() + "] to [" + timeStampPath + "]");
	}
	return renameResult;
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

    public List<String> getFirstNames() {
	return Transform.list(readCSV(), x -> x.map(Engineer::getFirstName));
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

    public void writeText(String text) throws IOException {
	Files.write(Paths.get(getFileName()), text.getBytes());
    }

    public <T> T readJson(Class<T> clazz) {
	try {
	    return new Gson().fromJson(Files.readString(Paths.get(getFileName()), StandardCharsets.US_ASCII), clazz);
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return null;
    }

    public <T> void writeCSV(List<T> list, Class<T> pojoClass) {
	Function<CsvMapper, CsvSchema> t1 = mapper -> mapper.schemaFor(pojoClass);
	writeCSV(list, t1);
    }

    private <T> void writeCSV(List<T> list, Function<CsvMapper, CsvSchema> schemaMaker) {
	try {
	    CsvMapper mapper = new CsvMapper();
//	CsvSchema schema = mapper.schemaFor(pojoClass);
	    CsvSchema schema = schemaMaker.apply(mapper);
	    mapper.configure(Feature.IGNORE_UNKNOWN, true);
//	    CsvSchema schema = CsvSchema.builder().addColumn("Description").build();

	    schema = schema.withColumnSeparator(',').withHeader();

	    // output writer
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

    public void writeLines(List<String> lines) {
	try {
	    FileUtils.writeLines(new File(getFileName()), lines);
	} catch (IOException e) {
	    System.out.println("e=" + (e));
	    e.printStackTrace();
	}
    }

    public void writeCSV(List<TT> list, List<String> columnNames) {
	Function<CsvMapper, CsvSchema> t1 = mapper -> {
	    Builder builder = CsvSchema.builder();
	    columnNames.stream().forEach(builder::addColumn);
	    return builder.build();
	};
	writeCSV(list, t1);
    }

    public void launch() {

	try {
	    Runtime.getRuntime().exec(new String[] {
		    "C:\\Program Files (x86)\\Microsoft Office\\Office16\\excel.exe",
		    getFileName()
	    });
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

}
