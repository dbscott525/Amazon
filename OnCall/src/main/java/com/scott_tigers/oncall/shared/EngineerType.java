package com.scott_tigers.oncall.shared;

import com.scott_tigers.oncall.bean.Engineer;

public enum EngineerType {
    Secondary, Primary;

    public boolean is(Engineer engineer) {
	return engineer.isType(this);
    }

}
