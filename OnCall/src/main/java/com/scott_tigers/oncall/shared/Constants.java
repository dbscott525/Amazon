/*
 * COPYRIGHT (C) 2020 Amazon All Rights Reserved.
 *
 * Created on Jun 19, 2020
 */
package com.scott_tigers.oncall.shared;

import java.util.HashMap;
import java.util.Map;

/**
 * (Put description here)
 * 
 * @author bruscob
 */
public class Constants {

    public static final String AMAZON_EMAIL_POSTFIX = "@amazon.com";
    public static final String AUTOMATICIC_UID = "AR_ESCALATOR";
    public static final String CHROME_USER_DATA_LOCATION = "C:\\Users\\bruscob\\AppData\\Local\\Google\\Chrome\\User Data";
    public static final String CHROMEDRIVER_EXE_LOCATION = "P:\\bin\\chromedriver.exe";
    public static final int CIT_SHIFT_SIZE = 15;
    public static final String CSV_EXTENSION = ".csv";
    public static final double CUSTOMER_ISSUE_AGE_EXPONENT = .9;
    public static final int CUSTOMER_ISSUE_TICKETS_ON_THE_QUEUE = 10;
    public static final String DEFAULT_SERVICE_TEAM_DIRECTORY = "W:\\Shared With Me\\Share";
    public static final String DOCX_EXTENSION = ".docx";
    public static final int ENGINE_TICKET_TRAILING_DAYS = 60;
    public static final int ENGINE_TICKETS_ON_THE_QUEUE = 10;
    public static final String ENGINEER_TYPE_TRAINEE = "Trainee";
    public static final String ITEM_CUSTOMER_ISSUE = "CustomerIssue";
    public static final String ITEM_ENGINE = "Engine";
    public static final String JSON_EXTENSION = ".json";
    public static final int NUMBER_OF_PRIMARY_TRAINING_WEEKS = 3;
    public static final int NUMBER_OF_WEEKDAYS = 5;
    public static final String ON_CALL_DATA_ARCHIVE_SUB_DIRECTORY = "\\OnCallDataArchive\\";
    public static final String ON_CALL_DATA_SUB_DIRECTORY = "\\OnCallData\\";
    public static final int ON_CALL_DAYS_PER_INTERVAL = 1;
    public static final int ON_CALLS_PER_DAY = 4;
    public static final int PENDING_CUSTOMER_RESPONSE_WAIT_TIME = 7;
    public static final String PPTX_EXTENSION = ".pptx";
    public static final int PRIORITY_ENGINEERS_PER_WEEK = 3;
    public static final String REPLACE_ME_EMAIL = "replace@me.com";
    public static final boolean SERVERLESS_ROTATION = false;
    public static final String SERVICE_TEAM_ENVIRONMENT_VARIABLE_NAME = "STDATA";
    public static final String SORTABLE_DATE_FORMAT = "yyyy-MM-dd";
    public static Map<String, String> TEMPLATE_REPLACEMENTS = new HashMap<String, String>() {
	private static final long serialVersionUID = 1L;

	{
	    put("Primary0", "DeoxaEyR");
	    put("Primary1", "DBYsVrjR");
	    put("Secondary", "WSuhsnzg");
	    put("Tech Esc", "dknqZRqk");
	    put("CIT0", "eCPiAKue");
	    put("CIT1", "pFGZFkTC");
	    put("CIT2", "jBwcGwXL");
	    put("CIT3", "EvXAFSrZ");
	    put("CIT4", "EGvknsaS");
	    put("CIT5", "SrSPAEer");
	    put("CITO0", "YRZnQdLb");
	    put("CITO1", "ubXtLtsN");
	    put("CITO2", "oLDGXYPM");
	    put("CITO3", "dnKSFHnF");
	    put("CITO4", "ffyMSStF");
	    put("CITO5", "sAHGeswu");
	    put("Date0", "UasZkAgF");
	    put("Date1", "FqyartTR");
	    put("Date2", "fXLCipMm");
	    put("Date3", "rvZrtLXS");
	    put("Date4", "UnFFhfKk");
	}
    };
    public static final String WINDOWS_PROGRAM_FILES_FOLDER = "C:\\\\Program Files (x86)\\\\Microsoft Office\\\\Office16\\";
    public static final String EXCEL_LOCATION = WINDOWS_PROGRAM_FILES_FOLDER + "EXCEL.EXE";
    public static final String WINWORD_LOCATION = WINDOWS_PROGRAM_FILES_FOLDER + "WINWORD.EXE";
    public static final String XLSX_EXTENSION = ".xlsx";
    public static final String XML_EXTENSION = ".xml";
    public static final int DAYS_PER_WEEK = 7;

}
