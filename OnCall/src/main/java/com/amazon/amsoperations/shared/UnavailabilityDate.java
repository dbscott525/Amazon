package com.amazon.amsoperations.shared;

import java.util.Arrays;
import java.util.List;

public class UnavailabilityDate {

    private List<String> keys;

    public UnavailabilityDate(String uid, String date) {
	keys = Arrays.asList(date, uid);
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((keys == null) ? 0 : keys.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	UnavailabilityDate other = (UnavailabilityDate) obj;
	if (keys == null) {
	    if (other.keys != null)
		return false;
	} else if (!keys.equals(other.keys))
	    return false;
	return true;
    }

}
