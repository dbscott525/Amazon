package com.scott_tigers.oncall.shared;

import java.util.function.Predicate;

public enum SpecialLabel {
    Escalation("ESCALATION]"), Availability("[AVAILABILITY]");

    private String searchString;

    SpecialLabel(String searchString) {
	this.searchString = searchString;
    }

    public static Predicate<SpecialLabel> match(String description) {
	return specialLabel -> description.contains(specialLabel.searchString);
    }

}
