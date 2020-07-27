package com.scott_tigers.oncall.utility;

import com.scott_tigers.oncall.bean.TT;

public class TTStat {
    private Integer caseId;
    private String createDate;
    private String resolvedDate;

    public TTStat(TT tt) {
	caseId = tt.getIntCaseId();
	createDate = dateOnly(tt.getCreateDate());
	resolvedDate = dateOnly(tt.getResolvedDate());
    }

    private String dateOnly(String dateString) {
	return dateString.length() >= 10 ? dateString.substring(0, 10) : dateString;
    }

    public Integer getCaseId() {
	return caseId;
    }

    public void setCaseId(Integer caseId) {
	this.caseId = caseId;
    }

    public String getCreateDate() {
	return createDate;
    }

    public void setCreateDate(String createDate) {
	this.createDate = createDate;
    }

    public String getResolvedDate() {
	return resolvedDate;
    }

    public void setResolvedDate(String resolvedDate) {
	this.resolvedDate = resolvedDate;
    }

}
