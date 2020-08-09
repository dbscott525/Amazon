package com.scott_tigers.oncall.utility;

import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.shared.EngineerFiles;

public class GenerateServiceTeamEmailList extends Utility {

    public static void main(String[] args) {
	new GenerateServiceTeamEmailList().run();
    }

    private void run() {
	// readCSVByType(EngineerFiles fileType)
	Stream<EngineerFiles> s1 = Stream.of(EngineerFiles.MASTER_LIST, EngineerFiles.TECH_ESC);
	Stream<Engineer> s2 = s1.flatMap(t -> readCSVByType(t).stream());
	s2.forEach(t -> System.out.println(t.getEmail()));

    }

}
