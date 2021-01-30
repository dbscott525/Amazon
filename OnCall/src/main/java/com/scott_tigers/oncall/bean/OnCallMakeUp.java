package com.scott_tigers.oncall.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scott_tigers.oncall.shared.Properties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OnCallMakeUp {
    private String name;
    private String primary;
    private String secondary;
    private String cit;

    @JsonProperty(Properties.NAME)
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @JsonProperty(Properties.PRIMARY)
    public String getPrimary() {
	return primary;
    }

    public void setPrimary(String primary) {
	this.primary = primary;
    }

    @JsonProperty(Properties.SECONDARY)
    public String getSecondary() {
	return secondary;
    }

    public void setSecondary(String secondary) {
	this.secondary = secondary;
    }

    @JsonProperty(Properties.CIT)
    public String getCit() {
	return cit;
    }

    public void setCit(String cit) {
	this.cit = cit;
    }

}
