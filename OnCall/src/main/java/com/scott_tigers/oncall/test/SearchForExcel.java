package com.scott_tigers.oncall.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

public class SearchForExcel {

//    private static final String PROGRAM_SEARCH_PATH = "C:\\Program Files (x86)\\Microsoft Office\\Office16";
    private static final String PROGRAM_SEARCH_PATH = "C:\\Program Files (x86)\\Microsoft Office";

    public static void main(String[] args) throws AddressException, MessagingException {
	try {
	    Optional<Path> excelExecutabel = Files
		    .walk(Paths.get(PROGRAM_SEARCH_PATH))
		    .filter(Files::isRegularFile)
		    .filter(f -> f.toString().toLowerCase().endsWith("excel.exe"))
		    .findFirst();

	    if (excelExecutabel.isPresent()) {
		System.out.println("f4.get().toAbsolutePath()=" + (excelExecutabel.get().toAbsolutePath()));
	    }
//		    .forEach((f) -> {
//			String file = f.toString().toLowerCase();
//			if (file.endsWith("excel.exe")) {
//			    System.out.println("f.toAbsolutePath()=" + (f.toAbsolutePath()));
//			    System.out.println(file + " found!");
//			}
//		    });
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

}
