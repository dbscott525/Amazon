package com.scott_tigers.oncall.test;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.scott_tigers.oncall.bean.EngineerMetric;
import com.scott_tigers.oncall.shared.Json;
import com.scott_tigers.oncall.utility.Utility;

@JsonIgnoreProperties
public class Test extends Utility {

    public static void main(String[] args) throws Exception {
	new Test().run();
    }

    private void run() throws Exception {
	List<EngineerMetric> list = getTicketClosedMetrics();

	Json.print(list);
    }

}