package com.scott_tigers.oncall.test;

public class Test {

    public static void main(String[] args) {
//	String regex = "(.+)(,.+)";
	String input = "Sean, Dae, Huansong, Guoping, Jiazheng, Mohammed";
	String result = input.replaceAll("(.+)(,)(.+)", "$1$2 and$3");
	System.out.println("result=" + (result));
    }

}
