package com.scott_tigers.oncall.utility;

import java.io.IOException;

import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Util;

public class Utility {

    protected void successfulFileCreation(EngineerFiles fileType) {
	System.out.println(fileType.getFileName() + " was successfully created.");
	System.out.println("File is now be launched");
	fileType.launch();
    }

    protected void copyMostRecentDownloadedTTs() throws IOException {
	Util.makeCopyofMostRecentTTDownload();
    }

}
