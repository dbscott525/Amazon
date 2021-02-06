package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.stream.Collectors;

import com.scott_tigers.oncall.shared.EngineerFiles;

public class CreateORRTable extends Utility {

    public static void main(String[] args) {
	new CreateORRTable().run();
    }

    private void run() {
	List<ORRTable> orrTable = EngineerFiles.ORR_QUESTION_SOURCE
		.readCSVToPojo(ORRRawData.class)
		.stream()
		.map(x -> x.getLine())
		.filter(l -> !"No Response".equals(l))
		.filter(l -> !l.contains("Questions Answered"))
		.filter(l -> !l.contains("Risk Score"))
		.map(l -> new ORRTable(l))
		.collect(Collectors.toList());

	EngineerFiles.ORR_QUESTIONS.write(x -> x.CSV(orrTable, ORRTable.class));
    }

    private static class ORRRawData {
	private String line;

	public String getLine() {
	    return line;
	}

	@SuppressWarnings("unused")
	public void setLine(String line) {
	    this.line = line;
	}
    }

    private static class ORRTable {
	private String category;
	private String question;

	public ORRTable(String line) {
	    category = "foo";
	    question = "bar";

	    if (line.matches("[a-zA-Z]{2} - .*")) {
		category = "matches";
//		question = line;
		category = line.replaceAll("([a-zA-Z]{2}) - .*", "$1");
		question = line.replaceAll("[a-zA-Z]{2} - (.*)", "$1");
	    } else if (line.matches("[A-Z]{2}-\\d{1,2}\\. .*")) {
		category = "question found";
		category = line.replaceAll("([A-Z]{2}-\\d{1,2})\\. .*", "$1");
		question = line.replaceAll("[A-Z]{2}-\\d{1,2}\\. (.*)", "$1");
	    }
	}

	public String getCategory() {
	    return category;
	}

	public void setCategory(String category) {
	    this.category = category;
	}

	public String getQuestion() {
	    return question;
	}

	public void setQuestion(String question) {
	    this.question = question;
	}

    }
}
