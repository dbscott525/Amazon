package deprecate;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OnCallSchedule {
    private String onCallLead;
    private String OnCall2;
    private String OnCall3;
    private Date date;

    @JsonFormat(pattern = "M/d/yyyy")
    public Date getDate() {
	return date;
    }

    @JsonProperty("On Call Lead")
    public String getOnCallLead() {
	return onCallLead;
    }

    @JsonProperty("Primary 1")
    public String getOnCall2() {
	return OnCall2;
    }

    @JsonProperty("Primary 2")
    public String getOnCall3() {
	return OnCall3;
    }

    public void setDate(Date date) {
	this.date = date;
    }

    public void setOnCallLead(String onCallLead) {
	this.onCallLead = onCallLead;
    }

    public void setOnCall2(String onCall2) {
	OnCall2 = onCall2;
    }

    public void setOnCall3(String onCall3) {
	OnCall3 = onCall3;
    }

    @Override
    public String toString() {
	return new Gson().toJson(this);
    }
}
