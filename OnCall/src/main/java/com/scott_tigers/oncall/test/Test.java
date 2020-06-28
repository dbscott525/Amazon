package com.scott_tigers.oncall.test;

import java.util.List;

import com.scott_tigers.oncall.shared.EngineerFiles;

public class Test {

    public static void main(String[] args) {
	List<Top100Company> t1 = EngineerFiles.TOP_100_COMPANIES.readCSVToPojo(Top100Company.class);
	System.out.println("t1=" + (t1));
    }

}
