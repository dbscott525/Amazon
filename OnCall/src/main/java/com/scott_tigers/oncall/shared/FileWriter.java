package com.scott_tigers.oncall.shared;

import java.util.Arrays;
import java.util.List;

public class FileWriter {
    private EngineerFiles fileType;
    private Executor writer = () -> {
    };
    private Executor archiver = () -> {
    };
    private Executor opener = () -> {
	System.out.println("Launching " + getFileName());
	fileType.launch();
    };

    public void write() {
	archiver.run();
	writer.run();
	opener.run();
    }

    public FileWriter fileType(EngineerFiles fileType) {
	this.fileType = fileType;
	return this;
    }

    public FileWriter lines(List<String> lines) {
	createWriter(() -> fileType.writeLines(lines));
	return this;
    }

    private void createWriter(Executor writeExecutor) {
	writer = () -> {
	    writeExecutor.run();
	    successfulCreation();
	};
    }

    private void successfulCreation() {
	System.out.println(getFileName() + " was successfully created.");
    }

    private String getFileName() {
	return fileType.getFileName();
    }

    public <T> FileWriter CSV(List<T> list, Class<T> pojoClass) {
	createWriter(() -> fileType.writeCSV(list, pojoClass));
	return this;
    }

    public <T> FileWriter CSV(List<T> list, String... columnNames) {
	createWriter(() -> fileType.writeCSV(list, Arrays.asList(columnNames)));
	return this;
    }

    public FileWriter json(Object jsonObject) {
	createWriter(() -> fileType.writeJson(jsonObject));
	return this;
    }

    public FileWriter noOpen() {
	opener = () -> {
	};
	return this;
    }

    public FileWriter archive() {
	archiver = () -> fileType.renameFileToTimeStampFile();
	return this;
    }

}
