package com.scott_tigers.oncall;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public enum EngineerFiles {
    MASTER_LIST("Engineer Master List"),
    ENGINEER_ADDS("Engineers to be Added"),
    FOO("foo"),
    LEVELS_FROM_QUIP("Levels From Quip"),
    NEW_LEVEL_ENGINEERS("New Level Engineers");

    private String fileName;

    public String getFileName() {
	return "J:\\SupportEngineering\\OnCallData\\" + fileName + ".csv";
    }

    EngineerFiles(String fileName) {
	this.fileName = fileName;
    }

    List<Engineer> readCSV() {
	return new CSVReader<Engineer>()
		.inputFile(getFileName())
		.type(Engineer.class)
		.read();
    }

    void replace(List<Engineer> exsitingEngineers) {
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

	String path = file.getPath();
	String extension = FilenameUtils.getExtension(path);
	int dotpos = path.length() - extension.length() - 1;
	String prefix = path.substring(0, dotpos);

	String timeStampPath = prefix + " " + Dates.TIME_STAMP.getFormattedDate() + "." + extension;

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

    List<String> getFirstNames() {
	return Transform.list(readCSV(), x -> x.map(Engineer::getFirstName));
    }

}
