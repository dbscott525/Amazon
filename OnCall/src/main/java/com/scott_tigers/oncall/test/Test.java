package com.scott_tigers.oncall.test;

public class Test {

    private static final String QUOTE_COMMA_SEPARATER = "\",\"";
    static String testInput = "  {\r\n" +
	    "    \"onCallMember\": \"goelnike/atrayeem/mlil\",\r\n" +
	    "    \"startDateTime\": \"7/21/20 10:00\",\r\n" +
	    "    \"endDateTime\": \"7/22/20 10:00\"\r\n" +
	    "  },\r\n" +
	    "  {\r\n" +
	    "    \"onCallMember\": \"pooyasp/lujudy/zhongyaw\",\r\n" +
	    "    \"startDateTime\": \"7/22/20 10:00\",\r\n" +
	    "    \"endDateTime\": \"7/23/20 10:00\"\r\n" +
	    "  }";

    public static void main(String[] args) {
//	String regex = "(\"onCallMember\": \")(goelnike)/atrayeem/mlil\",";
//	String regex = "(\"onCallMember\": \")(goelnike)(/)atrayeem/mlil\",";
//	String regex = "(\"onCallMember\": \")([a-z]+)(/)atrayeem/mlil\",";
//	String regex = "(\"onCallMember\": \")([a-z]+)(/)(atrayeem)/mlil\",";
//	String regex = "(\"onCallMember\": \")([a-z]+)/(atrayeem)/mlil\",";
//	String regex = "(\"onCallMember\": \")([a-z]+)/([a-z]+)/mlil\",";
//	String regex = "(\"onCallMember\": \")([a-z]+)/([a-z]+)/(mlil)\",";
	String regex = "(\"onCallMember\": \")([a-z]+)/([a-z]+)/([a-z]+)\",";
	String testOutput = testInput.replaceAll(regex,
		"$1$2" + QUOTE_COMMA_SEPARATER + "$3" + QUOTE_COMMA_SEPARATER + "$4\",");

	System.out.println("testOutput=" + (testOutput));

    }

}
