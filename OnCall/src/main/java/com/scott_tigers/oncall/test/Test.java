package com.scott_tigers.oncall.test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.utility.Utility;

@JsonIgnoreProperties
public class Test extends Utility {

    public static void main(String[] args) throws Exception {
	new Test().run();
    }

    private void run() throws Exception {
	EngineerFiles.OFFSHORE_UIDS.readCSV().stream().map(Engineer::getUid).forEach(System.out::println);

    }

}