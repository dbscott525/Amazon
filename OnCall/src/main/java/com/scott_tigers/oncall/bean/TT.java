package beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.GsonBuilder;

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

    @JsonProperty("Root Cause Details")
    public String getRootCauseDetails() {
	return rootCauseDetails;
    }

    public void setRootCauseDetails(String rootCauseDetails) {
	this.rootCauseDetails = rootCauseDetails;
    }

    public String getOwner() {
	return owner;
    }

    public void setOwner(String owner) {
	this.owner = owner;
    }

    @Override
    public String toString() {
	return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}
