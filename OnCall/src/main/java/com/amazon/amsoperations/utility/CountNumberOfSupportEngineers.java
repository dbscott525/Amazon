package com.amazon.amsoperations.utility;

import java.util.Arrays;
import java.util.List;

import com.amazon.amsoperations.bean.Engineer;
import com.amazon.amsoperations.shared.EngineerFiles;

public class CountNumberOfSupportEngineers {

    public static void main(String[] args) {

	List<String> oncalls = Arrays.asList("Secondary", "Primary");

	System.out.println("Number of on-call engineers is " + (EngineerFiles.MASTER_LIST
		.readCSV()
		.stream()
		.map(Engineer::getType)
		.filter(oncalls::contains)
		.count()));

    }

}
