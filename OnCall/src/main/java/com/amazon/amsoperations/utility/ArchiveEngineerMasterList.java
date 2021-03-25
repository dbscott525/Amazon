package com.amazon.amsoperations.utility;

import java.io.IOException;

import com.amazon.amsoperations.shared.EngineerFiles;
import com.amazon.amsoperations.shared.Util;

public class ArchiveEngineerMasterList extends Util {
    public static void main(String[] args) throws Exception {
	new ArchiveEngineerMasterList().run();
    }

    private void run() throws IOException {
	EngineerFiles.MASTER_LIST.archive();
    }
}