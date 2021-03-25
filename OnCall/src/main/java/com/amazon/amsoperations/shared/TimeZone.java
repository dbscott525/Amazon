package com.amazon.amsoperations.shared;

import com.amazon.amsoperations.bean.Engineer;

public enum TimeZone {
    IST, EST, PST, DUB, SKIP;

    public boolean isIn(Engineer eng) {
	return eng.getTimeZone().equals(toString());
    }

}
