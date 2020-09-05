package com.scott_tigers.oncall.utility;

import java.util.stream.Stream;

import com.scott_tigers.oncall.shared.EngineerFiles;

public class GenerateServiceTeamEmailList extends Utility {

    public static void main(String[] args) {
	new GenerateServiceTeamEmailList().run();
    }

    private void run() {
	Stream.of(EngineerFiles.MASTER_LIST, EngineerFiles.TECH_ESC)
		.flatMap(t -> readCSVByType(t).stream())
		.forEach(t -> System.out.println(t.getEmail()));
    }

}
