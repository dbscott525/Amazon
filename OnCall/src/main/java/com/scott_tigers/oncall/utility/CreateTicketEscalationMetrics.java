package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scott_tigers.oncall.bean.TicketEscalation;
import com.scott_tigers.oncall.shared.Dates;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Properties;
import com.scott_tigers.oncall.shared.URL;

public class CreateTicketEscalationMetrics extends Utility {

    public static void main(String[] args) {
	new CreateTicketEscalationMetrics().run();
    }

    private void run() {
	List<TicketEscalation> escalations = readFromUrl(URL.TICKET_ESCALATIONS, TicketEscalation.class)
		.peek(TicketEscalation::normalizeDate)
		.collect(Collectors.toList());

	List<WeekEscalation> escalationsByWeek = escalations.stream()
		.map(TicketEscalation::getDate)
		.map(Dates.SORTABLE::getFirstDayOfWeek)
		.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
		.entrySet()
		.stream()
		.map(WeekEscalation::new)
		.sorted()
		.collect(Collectors.toList());

	EngineerFiles.TICKET_ESCLATIONS_PER_WEEK
		.write(w -> w.CSV(escalationsByWeek, Properties.DATE, Properties.COUNT));
	EngineerFiles.TICKET_ESCALATIONS_BAR_CHART.launch();

	List<TypeEscalation> esclationtypes = escalations
		.stream()
		.map(TicketEscalation::getType)
		.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
		.entrySet()
		.stream()
		.map(TypeEscalation::new)
		.collect(Collectors.toList());

	EngineerFiles.ESCALATIONS_BY_TYPE
		.write(w -> w.CSV(esclationtypes, Properties.TYPE, Properties.COUNT));
	EngineerFiles.ESCALATIONS_TYPE_PIE_CHART.launch();
    }

    private class WeekEscalation implements Comparable<WeekEscalation> {
	private String date;
	private Long count;

	public WeekEscalation(Entry<String, Long> entry) {
	    date = entry.getKey();
	    count = entry.getValue();
	}

	@JsonProperty(Properties.DATE)
	public String getDate() {
	    return date;
	}

	@JsonProperty(Properties.COUNT)
	public Long getCount() {
	    return count;
	}

	@Override
	public int compareTo(WeekEscalation o) {
	    return date.compareTo(o.date);
	}

    }

    private class TypeEscalation {
	private String type;
	private Long count;

	public TypeEscalation(Entry<String, Long> entry) {
	    type = entry.getKey();
	    count = entry.getValue();
	}

	@JsonProperty(Properties.TYPE)
	public String getType() {
	    return type;
	}

	@JsonProperty(Properties.COUNT)
	public Long getCount() {
	    return count;
	}

    }

}
