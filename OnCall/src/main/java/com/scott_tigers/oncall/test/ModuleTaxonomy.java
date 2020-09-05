package com.scott_tigers.oncall.test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModuleTaxonomy {

    private String sdm;
    private String area;

    public ModuleTaxonomy() {
    }

    public ModuleTaxonomy(String name, String area) {
	sdm = name;
	this.area = area;
    }

    public String getSdm() {
	return sdm;
    }

    public void setSdm(String sdm) {
	this.sdm = sdm;
    }

    public String getArea() {
	return area;
    }

    public void setArea(String area) {
	this.area = area;
    }

    @Override
    public String toString() {
	return "ModuleTaxonomy [sdm=" + sdm + ", area=" + area + "]";
    }

}
