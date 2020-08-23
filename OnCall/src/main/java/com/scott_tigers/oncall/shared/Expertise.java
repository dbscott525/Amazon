package com.scott_tigers.oncall.shared;

public enum Expertise {
    Binlog,
    MySQL8 {
	@Override
	public String getNotation() {
	    return "";
	}
    },
    NONE {
	@Override
	public String getNotation() {
	    return "";
	}
    },
    Serverless {
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

    public String getNotation() {
	return " (" + toString() + ")";

    }

    public int getRequiredOrder() {
	return 1;
    }

}
