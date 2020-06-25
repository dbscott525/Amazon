package com.scott_tigers.oncall;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EngineerDetail {
    private String firstName;
    private String uid;

    @JsonProperty("UID")
    public String getUid() {
	return uid;
    }

    public void setUid(String uid) {
	this.uid = uid;
    }

    @JsonProperty("First Name")
    public String getFirstName() {
	return firstName;
    }

    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    @Override
    public String toString() {
	return new Gson().toJson(this);
    }
}
