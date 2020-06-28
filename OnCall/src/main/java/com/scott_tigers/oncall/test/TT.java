package com.scott_tigers.oncall.test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.GsonBuilder;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TT {
    private Integer caseId;
    private String description;
    private String createDate;
    private String age;
    private String url;
    private Integer weight;

    @JsonProperty("Case ID")
    public Integer getCaseId() {
	return caseId;
    }

    public void setCaseId(Integer caseId) {
	url = "https://tt.amazon.com/" + caseId;
	this.caseId = caseId;
    }

    @JsonProperty("Description")
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    @JsonProperty("Create Date")
    public String getCreateDate() {
	return createDate;
    }

    public void setCreateDate(String createDate) {
	this.createDate = createDate;
    }

    @Override
    public String toString() {
	return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }

    @JsonProperty("Age")
    public String getAge() {
	return age;
    }

    public void setAge(String age) {
	this.age = age;
    }

    public String getUrl() {
	return caseId == null ? url
		: "https://tt.amazon.com/"
			+ caseId;
    }

    public void setUrl(String url) {
	this.url = url;
    }

    public Integer getWeight() {
	return weight;
    }

    public void setWeight(Integer weight) {
	this.weight = weight;
    }

}
