/*
 * COPYRIGHT (C) 2017 Aktana, Inc. All Rights Reserved.
 *
 * Created on Jun 19, 2020
 */
package com.scott_tigers.oncall.schedule;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.scott_tigers.oncall.bean.Engineer;
import com.scott_tigers.oncall.shared.EngineerFiles;

/**
 * (Put description here)
 * 
 * @author bruscob
 */
public class Engineers {

    private List<Engineer> engineers;

    public Engineers() {
	try {
	    engineers = new CsvMapper()
		    .reader()
		    .forType(Engineer.class)
		    .with(CsvSchema.emptySchema().withHeader())
		    .<Engineer>readValues(new File(EngineerFiles.MASTER_LIST.getFileName()))
		    .readAll();
	} catch (IOException e) {
	    e.printStackTrace();
	    System.exit(-1);
	}
	System.out.println("engineers.size()=" + (engineers.size()));
    }

    public List<Engineer> getEngineers() {
	return engineers;
    }

    public void setEngineers(List<Engineer> engineers) {
	this.engineers = engineers;
    }

    @Override
    public String toString() {
	new Exception().printStackTrace();
	return "Engineers [engineers=" + engineers + "]";
    }

}
