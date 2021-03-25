package com.scott_tigers.oncall.shared;

import com.scott_tigers.oncall.bean.Engineer;

public enum TimeZone {
    IST, EST, PST, DUB, SKIP;

    public boolean isIn(Engineer eng) {
	return eng.getTimeZone().equals(toString());
    }

}
