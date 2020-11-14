package com.scott_tigers.oncall.shared;

public enum Expertise {
    Binlog(2),
    BLR(1) {
//	@Override
//	public String getNotation() {
//	    return "";
//	}
    },
    MySQL8(2) {
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
    QP(1),
    Replication(1),
    Serverless(1) {
	@Override
	public String getNotation() {
	    return "";
	}

	@Override
	public int getRequiredOrder() {
	    return 0;
	}
    };

    public static Expertise get(String expertise) {
	try {
	    return Expertise.valueOf(expertise);
	} catch (Exception e) {
	    return NONE;
	}
    }

    private int maximum;

    Expertise(int maximum) {
	this.maximum = maximum;
    }

    public boolean allowedNumber(Integer expertCount) {
	return expertCount <= maximum;
    }

    public String getNotation() {
	return " (" + toString() + ")";

    }

    public int getRequiredOrder() {
	return 1;
    }

}
