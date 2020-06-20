/*
 * COPYRIGHT (C) 2017 Aktana, Inc. All Rights Reserved.
 *
 * Created on Jun 19, 2020
 */
package com.scott_tigers.oncall;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

/**
 * (Put description here)
 * 
 * @author bruscob
 */
public class Engineers {
    Engineers() {
        CsvSchema csv = CsvSchema.emptySchema()
                                 .withHeader();

//        MappingIterator<Engineer> readValues;
        try {
            readValues = new CsvMapper().reader()
                                        .forType(Engineer.class)
                                        .with(csv)
                                        .readValues(new File(Constants.ENGINEERS_CSV_FILE));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

//        List<Engineer> actualList = IteratorUtils.toList(readValues);

    }

    private List<Engineer> engineers;

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
