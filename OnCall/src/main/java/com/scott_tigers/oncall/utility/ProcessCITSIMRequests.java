package com.scott_tigers.oncall.utility;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.shared.EngineerFiles;
import com.scott_tigers.oncall.shared.Json;
import com.scott_tigers.oncall.shared.URL;

public class ProcessCITSIMRequests extends Utility {

    private Map<String, Engineer> engineerMap;

    public static void main(String[] args) {
	new ProcessCITSIMRequests().run();
    }

    private void run() {
	List<Engineer> engineers = EngineerFiles.MASTER_LIST.readCSV();
	engineerMap = engineers
		.stream()
		.collect(Collectors.toMap(x -> x.getFullName(), x -> x));
	List<SimBugRequest> emails = readFromUrl(URL.CIT_BUG_TABLE, CITBug.class)
		.filter(x -> x.getEmailed().equals("0"))
		.map(SimBugRequest::new)
		.collect(Collectors.toList());
	Json.print(emails);
	EngineerFiles.SIM_BUG_INQUIRY_DATA.write(w -> w.CSV(emails, SimBugRequest.class));
	EngineerFiles.SIM_BUG_INQUIRY_EMAIL.launch();
    }

    private class SimBugRequest {
	private String email;
	private String name;
	private String sim;
	private String tt;

	public SimBugRequest(CITBug citBug) {
	    Engineer engineer = engineerMap.get(citBug.getSdm());
	    if (engineer == null) {
		System.out.println("Can't find email for " + citBug.getSdm());
		System.exit(1);
	    }
	    email = engineer.getEmail();
	    name = engineer.getFirstName();
	    sim = citBug.getSim();
	    tt = citBug.getTt();
	}

	@SuppressWarnings("unused")
	public String getEmail() {
	    return email;
	}

	@SuppressWarnings("unused")
	public String getName() {
	    return name;
	}

	@SuppressWarnings("unused")
	public String getSim() {
	    return sim;
	}

	@SuppressWarnings("unused")
	public String getTt() {
	    return tt;
	}

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class CITBug {
	private String emailed;
	private String sim;
	private String tt;
	private String sdm;

	@JsonProperty("Emailed")
	public String getEmailed() {
	    return emailed;
	}

	@JsonProperty("SIM")
	public String getSim() {
	    return sim;
	}

	@JsonProperty("TTs")
	public String getTt() {
	    return tt;
	}

	@JsonProperty("SDM")
	public String getSdm() {
	    return sdm;
	}
    }
}
