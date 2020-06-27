package com.scott_tigers.oncall;

import java.text.SimpleDateFormat;
import java.util.Date;

public enum Dates {
    TIME_STAMP("yyyy-MM-dd-HH-mm-ss");

    private String format;

    Dates(String format) {
	this.format = format;
    }

    String getFormattedDate() {
	return new SimpleDateFormat(format).format(new Date());
    }
}
