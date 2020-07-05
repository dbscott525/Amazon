package com.scott_tigers.oncall.utility;

import java.util.stream.Collectors;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class CreateCompleteEmailList {

    public static void main(String[] args) {
	new CreateCompleteEmailList().run();
    }

    private void run() {
	System.out.println(
		EngineerFiles.MASTER_LIST.readCSV().stream().map(Engineer::getEmail).collect(Collectors.joining(";")));

    }
}
