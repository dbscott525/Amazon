package com.amazon.amsoperations.utility;

import java.util.List;

import com.amazon.amsoperations.bean.KeywordPoints;
import com.amazon.amsoperations.bean.TT;

public class WeightContext {

    private List<KeywordPoints> keywordPoints;
    private double engineTypeExponent;
    private TT tt;

    public List<KeywordPoints> getKeywordPoints() {
	return keywordPoints;
    }

    public void setKeywordPoints(List<KeywordPoints> keywordPoints) {
	this.keywordPoints = keywordPoints;
    }

    public double getEngineTypeExponent() {
	return engineTypeExponent;
    }

    public void setEngineTypeExponent(double engineTypeExponent) {
	this.engineTypeExponent = engineTypeExponent;
    }

    public TT getTt() {
	return tt;
    }

    public void setTt(TT tt) {
	this.tt = tt;
    }

    public Integer getAge() {
	return Integer.valueOf(tt.getAge());
    }
}
