package com.scott_tigers.oncall;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class CSVReader<T> {

    private String inputFile;
    private Class<T> type;

    public CSVReader<T> inputFile(String inputFile) {
	this.inputFile = inputFile;
	return this;

    }

    public CSVReader<T> type(Class<T> type) {
	this.type = type;
	return this;
    }

    public List<T> read() {
	try {
	    return new CsvMapper()
		    .reader()
		    .forType(type)
		    .with(CsvSchema.emptySchema().withHeader())
		    .<T>readValues(new File(inputFile))
		    .readAll();
	} catch (IOException e) {
	    e.printStackTrace();
	    System.exit(-1);
	    return new ArrayList<T>();
	}
    }

}
