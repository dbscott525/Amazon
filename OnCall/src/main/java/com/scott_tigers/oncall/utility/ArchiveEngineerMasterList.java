package com.scott_tigers.oncall.utility;

import java.io.IOException;

import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Util;

public class ArchiveEngineerMasterList extends Util {
    public static void main(String[] args) throws Exception {
	new ArchiveEngineerMasterList().run();
    }

    private void run() throws IOException {
	EngineerFiles.MASTER_LIST.archive();
    }
}