package com.amazon.amsoperations.utility;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.amazon.amsoperations.bean.TT;
import com.amazon.amsoperations.shared.Dates;
import com.amazon.amsoperations.shared.Json;

public class TicketFlowAggregator {
    private static final String RESOLVED_DATE = "ResolvedDate";
    private static final String CREATE_DATE = "CreateDate";
    private HashMap<String, TicketMetric> metricMap = new HashMap<String, TicketMetric>();
    private HashMap<String, TTCMetric> ttcMap = new HashMap<>();

    public HashMap<String, TTCMetric> getTtcMap() {
	return ttcMap;
    }

    public void newTicket(TT tt) {
	Stream.of(CREATE_DATE, RESOLVED_DATE)
		.forEach(dateType -> addMetric(tt, dateType));
	addTimeToCloseMetric(tt);
    }

    private void addTimeToCloseMetric(TT tt) {
	String resolved = tt.getResolvedDate();
	String created = tt.getCreateDate();
	if (resolved.length() != 0 && created.length() != 0) {
	    Date resolvedDate = Dates.TT_DATE.getDateFromString(resolved);
	    long resolvedMillis = resolvedDate.getTime();
	    long createdMillis = Dates.TT_DATE.getDateFromString(created).getTime();
	    long days = TimeUnit.DAYS.convert(resolvedMillis - createdMillis, TimeUnit.MILLISECONDS);
	    String firstDayOfMonth = Dates.SORTABLE.getFirstDayOfMonth(Dates.SORTABLE.getFormattedString(resolvedDate));
	    TTCMetric ttcMetric = ttcMap.get(firstDayOfMonth);
	    ttcMetric = ttcMetric == null ? new TTCMetric() : ttcMetric;
	    ttcMap.put(firstDayOfMonth, ttcMetric);
	    ttcMetric.add(firstDayOfMonth, days);
	}
    }

    private void addMetric(TT tt, String dateType) {
	try {
	    Optional
		    .of((String) TT.class.getMethod("get" + dateType).invoke(tt))
		    .filter(date -> !date.isEmpty())
		    .map(date -> date.substring(0, 10))
		    .map(this::firstDayOfWeek)
		    .ifPresentOrElse(date -> {
			if (date.equals("2018-12-24")) {
			    Json.print(tt);
			}
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
	return Dates.SORTABLE
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

    public List<TTCMetric> getTtcMetrics() {
	return ttcMap
		.values()
		.stream()
		.sorted()
		.collect(Collectors.toList());
    }

}
