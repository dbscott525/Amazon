package com.amazon.amsoperations.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KeywordPoints {
    String keyword;
    Integer points;

    public String getKeyword() {
	return keyword;
    }

    public void setKeyword(String keyword) {
	this.keyword = keyword;
    }

    public Integer getPoints() {
	return points;
    }

    public void setPoints(Integer points) {
	this.points = points;
    }
}
