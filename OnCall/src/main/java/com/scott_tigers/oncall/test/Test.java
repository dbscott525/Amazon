package com.scott_tigers.oncall.test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.scott_tigers.oncall.bean.LTTRTicket;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Properties;
import com.scott_tigers.oncall.utility.Utility;

@JsonIgnoreProperties
public class Test extends Utility {

    public static void main(String[] args) throws Exception {
	new Test().run();
    }

    private void run() throws Exception {
	List<LTTRTicket> list = EngineerFiles.LTTR_CANDIDATE_EMAIL_DATA_COPY
		.readCSVToPojo(LTTRTicket.class)
		.stream()
		.collect(Collectors.toList());

//	Json.print(list);

	System.out.println("before write");
	EngineerFiles.LTTR_CANDIDATE_EMAIL_DATA.write(w -> w.CSV(list, LTTRTicket.class));
	EngineerFiles.LTTR_CANDIDATE_EMAIL_DATA.write(w -> w.CSV(list, Properties.TICKET_ID));
//	EngineerFiles.LTTR_CANDIDATE_EMAIL_DATA.write(w -> w.CSV(list, Properties.TICKET_ID,
//		Properties.EMAIL, Properties.TO, Properties.TICKETS,
//		Properties.DESCRIPTION, Properties.TICKET, Properties.SEARCH_URL, Properties.TOTAL_TICKETS));

    }

    @SuppressWarnings("unused")
    private void run21() throws Exception {
	Calendar cld = Calendar.getInstance();
	cld.set(Calendar.YEAR, 2020);
	cld.set(Calendar.WEEK_OF_YEAR, 35);
	Date result = cld.getTime();
	System.out.println("result=" + (result));
    }

}