package com.scott_tigers.oncall.utility;

import com.scott_tigers.oncall.shared.URL;

public enum LTTRList {
    Plan(URL.LTTR_PLAN), Candidate(URL.LTTR_CANDIDATES), Nonactionable(URL.LTTR_NON_ACTIONABLE_SIMS);

    private String url;

    public String getUrl() {
	return url;
    }

    LTTRList(String url) {
	this.url = url;
    }

    String getDisplayName() {
	return toString();
    }
}
