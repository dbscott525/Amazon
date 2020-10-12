package com.scott_tigers.oncall.utility;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scott_tigers.oncall.bean.TT;
import com.scott_tigers.oncall.shared.Dates;

public class TicketFlowAggregator {
    private static final String RESOLVED_DATE = "ResolvedDate";
    private static final String CREATE_DATE = "CreateDate";
    private HashMap<String, TicketMetric> metricMap = new HashMap<String, TicketMetric>();

    public void newTicket(TT tt) {
	Stream.of(CREATE_DATE, RESOLVED_DATE)
		.forEach(dateType -> addMetric(tt, dateType));
    }

    private void addMetric(TT tt, String dateType) {
	try {
	    Optional
		    .of((String) TT.class.getMethod("get" + dateType).invoke(tt))
		    .filter(date -> !date.isEmpty())
		    .map(date -> date.substring(0, 10))
		    .map(this::firstDayOfWeek)
		    .ifPresentOrElse(date -> {
			TicketMetric metric = metricMap.get(date);

			if (metric == null) {
			    metric = new TicketMetric(date);
			    metricMap.put(date, metric);
			}

			metric.addDataPoint(dateType);

		    }, () -> {
			if (CREATE_DATE.equals(dateType))
			    System.out.println("No " + dateType + " Date");
		    });
	} catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    private String firstDayOfWeek(String date) {
	Dates dateType = Dates.SORTABLE;
	return getFirstDayOfWeek(dateType, date);
    }

    private String getFirstDayOfWeek(Dates dateType, String date) {
	return dateType
		.getDateFromString(date)
		.toInstant()
		.atZone(ZoneId.systemDefault())
		.toLocalDate()
		.with(DayOfWeek.MONDAY)
		.toString();
    }

    public List<TicketMetric> getMetrics() {
	return metricMap
		.values()
		.stream()
		.sorted()
		.collect(Collectors.toList());
    }

}
