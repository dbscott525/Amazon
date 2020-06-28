package com.scott_tigers.oncall.utility;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scott_tigers.oncall.schedule.KeywordPoints;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.test.TT;
import com.scott_tigers.oncall.test.Top100Company;

public class GenerateCustomerIssueBacklog {

    public static void main(String[] args) throws Exception {
	new GenerateCustomerIssueBacklog().run();
    }

    private List<Integer> assignedTicketIds;
    private List<KeywordPoints> keywordPoints;
    private List<String> top100Companies;

    private void run() throws Exception {
	readAssignedTicets();
	makeCopyofMostRecentTTDownload();
	readPointData();
	readtop100CompanyData();
	createReadyQueue();

	System.out.println("TT Ready Queue Created");

    }

    private void createReadyQueue() {
	EngineerFiles.CUSTOMER_ISSUE_BACKLOG.writeCSV(EngineerFiles.TT_DOWNLOAD
		.readCSVToPojo(TT.class)
		.stream()
		.filter(this::notAssigned)
		.peek(tt -> assignedWeight(tt))
		.sorted(Comparator.comparing(TT::getWeight)
			.reversed())
		.collect(Collectors.toList()), TT.class);
    }

    private void readAssignedTicets() {
	assignedTicketIds = EngineerFiles.ASSIGNED_TICKETS
		.readCSVToPojo(TT.class)
		.stream()
		.map(TT::getUrl)
		.filter(url -> url.matches("https://tt.amazon.com/[0-9]+"))
		.map(url -> url.replaceAll("https://tt.amazon.com/0?([0-9]+)", "$1"))
		.map(Integer::valueOf)
		.collect(Collectors.toList());
    }

    private void readtop100CompanyData() {
	top100Companies = EngineerFiles.TOP_100_COMPANIES
		.readCSVToPojo(Top100Company.class)
		.stream().map(Top100Company::getCompany)
		.collect(Collectors.toList());
    }

    private void readPointData() {
	keywordPoints = EngineerFiles.KEYWORD_POINTS
		.readCSVToPojo(KeywordPoints.class);
    }

    private void assignedWeight(TT tt) {
	String description = tt.getDescription();

	int weight = keywordPoints.stream().filter(kw -> description.contains(kw.getKeyword()))
		.map(kw -> kw.getPoints()).mapToInt(Integer::intValue)
		.sum();

	weight += Integer.valueOf(tt.getAge()) / 7;

	if (top100Companies.stream().anyMatch(x -> description.contains(x))) {
	    weight += 5;
	}

	tt.setWeight(weight);
    }

    private boolean notAssigned(TT tt) {
	return !assignedTicketIds.contains(tt.getCaseId());
    }

    private void makeCopyofMostRecentTTDownload() throws IOException {
	String homePath = System.getenv("HOMEDRIVE") + System.getenv("HOMEPATH");
	Path path = Paths.get(homePath, "Downloads");

	String latestTTFile;

	try (Stream<Path> downloadFileList = Files.list(path)) {
	    latestTTFile = downloadFileList
		    .filter(t -> isTT(t))
		    .sorted()
		    .reduce((first, second) -> second)
		    .orElse(null)
		    .toString();

	}

	Path wiki_path = Paths.get(latestTTFile);

	Charset charset = Charset.forName("ISO-8859-1");
	List<String> lines = Files.readAllLines(wiki_path, charset);
	lines.remove(0);

	EngineerFiles.TT_DOWNLOAD.writeLines(lines);
    }

    private boolean isTT(Path p) {
	return p.getFileName().toString().matches("^ticket_results - .*\\.csv");
    }

}
