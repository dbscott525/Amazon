package com.scott_tigers.oncall;

import java.io.IOException;

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
    	new OptimalSchedule(new Engineers().getEngineers()).getSchedule();
    }

}
