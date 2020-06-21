/*
 * COPYRIGHT (C) 2017 Aktana, Inc. All Rights Reserved.
 *
 * Created on Jun 19, 2020
 */
package com.scott_tigers.oncall;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

/**
 * (Put description here)
 * 
 * @author bruscob
 */
public class Engineers {

    private List<Engineer> engineers;

    Engineers() {
	try {
	    engineers = new CsvMapper().reader().forType(Engineer.class).with(CsvSchema.emptySchema().withHeader())
		    .<Engineer>readValues(new File(Constants.ENGINEERS_CSV_FILE)).readAll();
	} catch (IOException e) {
	    e.printStackTrace();
	    System.exit(-1);
	}
    }

    public List<Engineer> getEngineers() {
	return engineers;
    }

    public void setEngineers(List<Engineer> engineers) {
	this.engineers = engineers;
    }

    @Override
    public String toString() {
	return "Engineers [" + (engineers != null ? "engineers=" + engineers : "") + "]";
    }

}
