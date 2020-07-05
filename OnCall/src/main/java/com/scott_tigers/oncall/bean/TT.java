package com.scott_tigers.oncall.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.GsonBuilder;
import com.scott_tigers.oncall.shared.Properties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TT {
    private Integer caseId;
    private String description;
    private String createDate;
    private String age;
    private String owner;
    private String url;
    private String rootCauseDetails;
    private Integer weight;
    private String comments;
    private String review;
    private String status;
    @SuppressWarnings("unused")
    private String link;

    @JsonProperty(Properties.CASE_ID)
    public Integer getCaseId() {
	return caseId;
    }

    public void setCaseId(Integer caseId) {
	url = "https://tt.amazon.com/" + caseId;
	this.caseId = caseId;
    }

    @JsonProperty(Properties.DESCRIPTION)
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    @JsonProperty(Properties.CREATE_DATE)
    public String getCreateDate() {
	return createDate;
    }

    public void setCreateDate(String createDate) {
	this.createDate = createDate;
    }

    @JsonProperty("Age")
    public String getAge() {
	return age;
    }

    public void setAge(String age) {
	this.age = age;
    }

    @JsonProperty(Properties.URL)
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

    @JsonProperty(Properties.ROOT_CAUSE_DETAILS)
    public String getRootCauseDetails() {
	return rootCauseDetails;
    }

    public void setRootCauseDetails(String rootCauseDetails) {
	this.rootCauseDetails = rootCauseDetails;
    }

    @JsonProperty(Properties.OWNER)
    public String getOwner() {
	return owner;
    }

    public void setOwner(String owner) {
	this.owner = owner;
    }

    @JsonProperty(Properties.COMMENTS)
    public String getComments() {
	return comments;
    }

    public void setComments(String comments) {
	this.comments = comments;
    }

    @JsonProperty(Properties.REVIEW)
    public String getReview() {
	return review;
    }

    public void setReview(String review) {
	this.review = review;
    }

    @JsonProperty(Properties.STATUS)
    public String getStatus() {
	return status;
    }

    public void setStatus(String status) {
	this.status = status;
    }

    @JsonProperty(Properties.LINK)
    public String getLink() {
	// =HYPERLINK("https://tt.amazon.com/0503132068", "FOO")
	return "=HYPERLINK(\"https://tt.amazon.com/"
		+ caseId
		+ "\", \""
		+ caseId
		+ "\")";
    }

    public void setLink(String link) {
	this.link = link;
    }

    @Override
    public String toString() {
	return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}
