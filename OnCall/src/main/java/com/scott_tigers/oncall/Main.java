package com.scott_tigers.oncall;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.IteratorUtils;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/*
 * COPYRIGHT (C) 2017 Aktana, Inc. All Rights Reserved.
 *
 * Created on Jun 19, 2020
 */

/**
 * (Put description here)
 * 
 * @author bruscob
 */
public class Main {

    public static void main(String[] args) throws IOException {

        CsvSchema csv = CsvSchema.emptySchema()
                                 .withHeader();

        MappingIterator<Engineer> readValues = new CsvMapper().reader()
                                                              .forType(Engineer.class)
                                                              .with(csv)
                                                              .readValues(new File(Constants.ENGINEERS_CSV_FILE));

        List<Engineer> actualList = IteratorUtils.toList(readValues);
        System.out.println("actualList=" + (actualList));

        while (readValues.hasNext()) {
            System.out.println("readValues.next()=" + (readValues.next()));
        }

        Engineers engineers = readEngineers();
        System.out.println("engineers=" + (engineers));
    }

    private static Engineers readEngineers() throws IOException {
        return getEngineersObject(readEngineersFromCSVFile());
    }

    private static MappingIterator<Map<?, ?>> readEngineersFromCSVFile() throws IOException {
        CsvSchema csv = CsvSchema.emptySchema()
                                 .withHeader();

        MappingIterator<Map<?, ?>> readValues = new CsvMapper().reader()
                                                               .forType(Map.class)
                                                               .with(csv)
                                                               .readValues(new File(Constants.ENGINEERS_CSV_FILE));
        return readValues;
    }

    private static Engineers getEngineersObject(MappingIterator<Map<?, ?>> mappingIterator) throws IOException, JsonSyntaxException {
        String json = "{engineers: " + mappingIterator.readAll()
                                                      .toString()
                + "}";
        Engineers engineers = new Gson().fromJson(json, Engineers.class);
        return engineers;
    }

}
