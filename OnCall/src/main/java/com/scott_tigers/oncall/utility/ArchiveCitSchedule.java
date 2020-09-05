package com.scott_tigers.oncall.utility;

import java.io.IOException;

import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Util;

public class ArchiveCitSchedule extends Util {
    public static void main(String[] args) throws Exception {
	new ArchiveCitSchedule().run();
    }

    private void run() throws IOException {
	EngineerFiles.CIT_SCHEDULE.archive();
    }
}