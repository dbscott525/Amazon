package com.scott_tigers.oncall.shared;

import java.util.Arrays;
import java.util.List;

import com.scott_tigers.oncall.bean.Engineer;

public enum ShadowSlot {
    MORNING("PST", "EST", "IST"),
    AFTERNOON("PST", "EST")
//    EVENING("PST", "CT")
    ;

    private List<String> timeZones;

    ShadowSlot(String... timeZones) {
	this.timeZones = Arrays.asList(timeZones);
    }

    public boolean isCompatible(Engineer engineer) {
	return timeZones.contains(engineer.getTimeZone());
    }

}
