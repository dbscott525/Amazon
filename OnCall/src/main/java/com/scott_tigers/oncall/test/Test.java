package com.scott_tigers.oncall.test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.scott_tigers.oncall.bean.OnCallScheduleRow;
import com.scott_tigers.oncall.shared.Json;
import com.scott_tigers.oncall.shared.Oncall;
import com.scott_tigers.oncall.utility.Utility;

@JsonIgnoreProperties
public class Test extends Utility {

    public static void main(String[] args) throws Exception {
	new Test().run();
    }

    private void run() throws Exception {
	List<OnCallScheduleRow> schedule = Oncall.TechEsc
		.getOnCallScheduleStream()
		.collect(Collectors.toList());
	Json.print(schedule);
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