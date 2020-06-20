/*
 * COPYRIGHT (C) 2017 Aktana, Inc. All Rights Reserved.
 *
 * Created on Jun 19, 2020
 */
package com.scott_tigers.oncall;

/**
 * (Put description here)
 * 
 * @author bruscob
 */
public class Engineer {
    private String name;
    private int    level;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "Engineer [" + (name != null ? "name=" + name + ", " : "") + "level=" + level + "]";
    }
}
