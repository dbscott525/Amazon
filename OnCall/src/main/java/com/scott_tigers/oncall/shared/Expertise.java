package com.scott_tigers.oncall.shared;

public enum Expertise {
    QP(1),
    Binlog(1),
    MySQL8(1) {
	@Override
	public String getNotation() {
	    return "";
	}
    },
    NONE(1000) {
	@Override
	public String getNotation() {
	    return "";
	}
    },
    Serverless(1) {
	@Override
	public int getRequiredOrder() {
	    return 0;
	}

	@Override
	public String getNotation() {
	    return "";
	}
    },
    Replication(1);

    private int maximum;

    Expertise(int maximum) {
	this.maximum = maximum;
    }

    public static Expertise get(String expertise) {
	try {
	    return Expertise.valueOf(expertise);
	} catch (Exception e) {
	    return NONE;
	}
    }

    public String getNotation() {
	return " (" + toString() + ")";

    }

    public int getRequiredOrder() {
	return 1;
    }

    public boolean allowedNumber(Integer expertCount) {
	return expertCount <= maximum;
    }

}
