package com.amazon.amsoperations.utility;

import java.io.IOException;

import com.amazon.amsoperations.shared.EngineerFiles;
import com.amazon.amsoperations.shared.Util;

public class ArchiveCitSchedule extends Util {
    public static void main(String[] args) throws Exception {
	new ArchiveCitSchedule().run();
    }

    private void run() throws IOException {
	EngineerFiles.CIT_SCHEDULE.archive();
    }
}