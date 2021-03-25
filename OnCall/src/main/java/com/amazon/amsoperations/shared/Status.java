package com.amazon.amsoperations.shared;

import java.util.stream.Stream;

import com.amazon.amsoperations.bean.TT;

public enum Status {
    ASSIGNED("Assigned"),
    CLOSED("Closed") {
	@Override
	public boolean isUnresolved() {
	    return false;
	}

	@Override
	public boolean needsWork() {
	    return false;
	}
    },
    ERROR("Error"),
    PENDING_ANY_INFO_7_DAY_AUTO_RESOLVE("Pending Any Info - 7 Day Auto Resolve") {
	@Override
	public boolean needsWork() {
	    return false;
	}
    },
    PENDING_DAYTIME_SEV2_RE_ASSIGN("Pending Daytime Sev2 - Re-assign") {
	@Override
	public boolean isAlwaysInQueue() {
	    return true;
	}
    },
    PENDING_PENDING_CUSTOMER_RESPONSE("Pending Pending Customer Response") {
	@Override
	public boolean agedLongEnough(int age) {
	    return age > Constants.PENDING_CUSTOMER_RESPONSE_WAIT_TIME;
	}

	@Override
	public int getWeight() {
	    return 30;
	}

    },

    PENDING_PENDING_INFO_7_DAY_AUTO_RE_ASSIGN("Pending Pending Info - 7 Day Auto Re-assign") {
	@Override
	public boolean needsWork() {
	    return false;
	}
    },
    PENDING_PENDING_ROOT_CAUSE("Pending Pending Root Cause") {
	@Override
	public boolean includeInSummary() {
	    return false;
	}
    },
    PENDING_PENDING_SOFTWARE_UPDATE("Pending Pending Software Update") {

	@Override
	public int getWeight() {
	    return 50;
	}

	@Override
	public boolean isAlwaysInQueue() {
	    return true;
	}
    },
    PENDING_REQUESTER_INFO_3_DAY_AUTO_RESOLVE("Pending Requester Info - 3 Day Auto Resolve") {
	@Override
	public boolean needsWork() {
	    return false;
	}
    },
    PENDING_REQUESTER_INFO_7_DAY_AUTO_RESOLVE("Pending Requester Info - 7 Day Auto Resolve") {
	@Override
	public boolean needsWork() {
	    return false;
	}
    },
    PENDING_VERIFICATION_OF_FIX("Pending Verification of fix") {

	@Override
	public int getWeight() {
	    return 40;
	}

	@Override
	public boolean isAlwaysInQueue() {
	    return true;
	}
    },
    RESOLVED("Resolved") {
	@Override
	public boolean isUnresolved() {
	    return false;
	}

	@Override
	public boolean needsWork() {
	    return false;
	}
    },
    WORK_IN_PROGRESS("Work In Progress"),;

    public static Status get(String input) {
	return Stream
		.of(values())
		.filter(s -> s.getValue().compareTo(input) == 0)
		.findFirst()
		.orElse(ERROR);
    }

    public static Status getStatus(TT tt) {
	return get(tt.getStatus());
    }

    private String value;

    Status(String value) {
	this.value = value;
    }

    public boolean agedLongEnough(int age) {
	return true;
    }

    public String getValue() {
	return value;
    }

    public int getWeight() {
	return 0;
    }

    public boolean includeInSummary() {
	return true;
    }

    public boolean isAlwaysInQueue() {
	return false;
    }

    public boolean isNeverInQueue() {
	return toString().endsWith("_DAY_AUTO_RESOLVE");
    }

    public boolean isUnresolved() {
	return true;
    }

    public boolean needsWork() {
	return true;
    }
}
