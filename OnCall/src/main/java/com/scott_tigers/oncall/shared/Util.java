package com.scott_tigers.oncall.shared;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class Util {

    public static String getDateIncrementString(Date date, int dayIncrement, String dateFormat) {
	SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
	Calendar c = Calendar.getInstance();
	c.setTime(date);
	c.add(Calendar.DATE, dayIncrement);
	String dateString = sdf.format(c.getTime());
	return dateString;
    }

    public static void makeCopyofMostRecentTTDownload() throws IOException {
	String homePath = System.getenv("HOMEDRIVE") + System.getenv("HOMEPATH");
	Path path = Paths.get(homePath, "Downloads");

	String latestTTFile;

	try (Stream<Path> downloadFileList = Files.list(path)) {
	    latestTTFile = downloadFileList
		    .filter(t -> Util.isTT(t))
		    .sorted()
		    .reduce((first, second) -> second)
		    .orElse(null)
		    .toString();

	}

	List<String> lines = Files.readAllLines(Paths.get(latestTTFile), Charset.forName("ISO-8859-1"));
	lines.remove(0);

	EngineerFiles.TT_DOWNLOAD.writeLines(lines);
    }

    private static boolean isTT(Path path) {
	return path.getFileName().toString().matches("^ticket_results - .*\\.csv");
    }

}
